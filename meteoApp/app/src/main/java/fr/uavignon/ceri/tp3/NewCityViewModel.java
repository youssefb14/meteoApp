package fr.uavignon.ceri.tp3;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.uavignon.ceri.tp3.data.City;
import fr.uavignon.ceri.tp3.data.WeatherRepository;

public class NewCityViewModel extends AndroidViewModel {
    public static final String TAG = DetailViewModel.class.getSimpleName();

    private WeatherRepository repository;
    private MutableLiveData<City> city;

    public NewCityViewModel (Application application) {
        super(application);
        repository = WeatherRepository.get(application);
        city = new MutableLiveData<>();
    }

    public void setCity(long id) {
        repository.getCity(id);
        city = repository.getSelectedCity();
    }

    LiveData<City> getCity() {
        return city;
    }

    public long insertOrUpdateCity(City newCity) {
        long res = 0;
        if (city.getValue() == null) {
            res = repository.insertCity(newCity);
            // return -1 if there is a conflict
            setCity(res);
        } else {
            // ID does not change for updates
            newCity.setId(city.getValue().getId());
            int nb = repository.updateCity(newCity);
            // return the nb of rows updated by the query
            if (nb ==0)
                res = -1;
        }
        Log.d(TAG,"insert city="+city.getValue());
        return res;
    }
}
