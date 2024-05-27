package fr.uavignon.ceri.tp3;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.uavignon.ceri.tp3.data.City;
import fr.uavignon.ceri.tp3.data.WeatherRepository;

public class ListViewModel extends AndroidViewModel {
    private WeatherRepository repository;
    private LiveData<List<City>> allCities;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Throwable> webServiceThrowable = new MutableLiveData<>();

    public ListViewModel (Application application) {
        super(application);
        repository = WeatherRepository.get(application);
        allCities = repository.getAllCities();
    }

    LiveData<List<City>> getAllCities() {
        return allCities;
    }

    public void deleteCity(long id) {
        repository.deleteCity(id);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Throwable> getWebServiceThrowable() {
        return webServiceThrowable;
    }

    public void loadWeatherAllCities() {
        isLoading.setValue(true); // Commencer le chargement
        repository.loadWeatherAllCities();
        // Le chargement s'arrête dans le Repository via isLoading lorsque les appels sont terminés
    }

}
