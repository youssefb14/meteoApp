package fr.uavignon.ceri.tp3.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fr.uavignon.ceri.tp3.data.database.CityDao;
import fr.uavignon.ceri.tp3.data.database.WeatherRoomDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static fr.uavignon.ceri.tp3.data.database.WeatherRoomDatabase.databaseWriteExecutor;
import fr.uavignon.ceri.tp3.data.webservice.OWMInterface;

public class WeatherRepository {

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Throwable> webServiceThrowable = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Throwable> getWebServiceThrowable() {
        return webServiceThrowable;
    }

    private static final String TAG = WeatherRepository.class.getSimpleName();

    private LiveData<List<City>> allCities;
    private MutableLiveData<City> selectedCity;

    private CityDao cityDao;

    private static volatile WeatherRepository INSTANCE;

    public synchronized static WeatherRepository get(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new WeatherRepository(application);
        }

        return INSTANCE;
    }

    public WeatherRepository(Application application) {
        WeatherRoomDatabase db = WeatherRoomDatabase.getDatabase(application);
        cityDao = db.cityDao();
        allCities = cityDao.getAllCities();
        selectedCity = new MutableLiveData<>();
    }

    public LiveData<List<City>> getAllCities() {
        return allCities;
    }

    public MutableLiveData<City> getSelectedCity() {
        return selectedCity;
    }


    public long insertCity(City newCity) {
        Future<Long> flong = databaseWriteExecutor.submit(() -> {
            return cityDao.insert(newCity);
        });
        long res = -1;
        try {
            res = flong.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (res != -1)
            selectedCity.setValue(newCity);
        return res;
    }

    public int updateCity(City city) {
        Future<Integer> fint = databaseWriteExecutor.submit(() -> {
            return cityDao.update(city);
        });
        int res = -1;
        try {
            res = fint.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (res != -1)
            selectedCity.setValue(city);
        return res;
    }

    public void deleteCity(long id) {
        databaseWriteExecutor.execute(() -> {
            cityDao.deleteCity(id);
        });
    }

    public void getCity(long id)  {
        Future<City> fcity = databaseWriteExecutor.submit(() -> {
            Log.d(TAG,"selected id="+id);
            return cityDao.getCityById(id);
        });
        try {
            selectedCity.setValue(fcity.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadWeatherCity(City city) {
        isLoading.postValue(true);
        OWMInterface owmInterface = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(OWMInterface.class);

        String requestUrl = city.getName() + "," + city.getCountryCode();
        Call<WeatherResponse> call = owmInterface.getCurrentWeather(requestUrl, "93426d4ffc63745bd943a8c6673448e6");
        Log.d(TAG, "Making weather API request: " + call.request().url().toString());

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    // Log the response
                    Log.d(TAG, "Weather data received: " + weatherResponse);

                    if (weatherResponse.main != null && weatherResponse.weather != null && weatherResponse.weather.length > 0) {

                        city.setTemperature((float) (weatherResponse.main.temp - 273.15f));
                        city.setDescription(weatherResponse.weather[0].description);
                        city.setIcon(weatherResponse.weather[0].icon);


                        city.setHumidity(weatherResponse.main.humidity);

                        if (weatherResponse.wind != null) {
                            city.setWindSpeed((int) (weatherResponse.wind.speed * 3.6));
                            city.setWindDirection(weatherResponse.wind.deg);
                        }
                        if (weatherResponse.clouds != null) {
                            city.setCloudiness(weatherResponse.clouds.all);
                        }
                        city.setLastUpdate(weatherResponse.dt);

                        updateCity(city);
                    } else {
                        Log.e(TAG, "Invalid weather data received: Missing main or weather info");
                    }
                } else {
                    switch (response.code()) {
                        case 404:
                            Log.e(TAG, "Weather data not found: " + response.code() + " - " + response.message());
                            break;
                        case 401:
                            Log.e(TAG, "Unauthorized Access: Check API Key: " + response.code() + " - " + response.message());
                            break;
                        default:
                            Log.e(TAG, "Failed to load weather data: " + response.code() + " - " + response.message());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                isLoading.postValue(false);
                webServiceThrowable.postValue(t);
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    public void loadWeatherAllCities() {
        databaseWriteExecutor.execute(() -> {
            List<City> cities = cityDao.getSynchrAllCities();
            if (cities != null) {
                for (City city : cities) {
                    loadWeatherCity(city);
                }
            }
        });
    }
}
