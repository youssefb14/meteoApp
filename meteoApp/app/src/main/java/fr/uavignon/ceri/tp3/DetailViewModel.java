package fr.uavignon.ceri.tp3;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.uavignon.ceri.tp3.data.City;
import fr.uavignon.ceri.tp3.data.WeatherRepository;

public class DetailViewModel extends AndroidViewModel {
    public static final String TAG = DetailViewModel.class.getSimpleName();

    private WeatherRepository repository;
    private MutableLiveData<City> city;

    public void setCity(long id) {
        repository.getCity(id);
        city = repository.getSelectedCity();
    }
    LiveData<City> getCity() {
        return city;
    }

    private final LiveData<Boolean> isLoading;
    private final LiveData<Throwable> webServiceThrowable;

    public DetailViewModel(Application application) {
        super(application);
        repository = WeatherRepository.get(application);
        isLoading = repository.getIsLoading();
        webServiceThrowable = repository.getWebServiceThrowable();
        city = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Throwable> getWebServiceThrowable() {
        return webServiceThrowable;
    }

    public void loadWeatherCity(City city) {
        repository.loadWeatherCity(city);
    }

    public void refreshWeatherForCity() {
        City currentCity = city.getValue();
        if (currentCity != null) {
            loadWeatherCity(currentCity);
        }
    }
}

