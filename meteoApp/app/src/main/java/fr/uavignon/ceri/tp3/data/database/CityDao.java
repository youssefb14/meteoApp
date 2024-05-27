package fr.uavignon.ceri.tp3.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.uavignon.ceri.tp3.data.City;

@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(City city);

    @Query("DELETE FROM weather_table")
    void deleteAll();

    @Query("SELECT * from weather_table ORDER BY name ASC")
    LiveData<List<City>> getAllCities();

    @Query("SELECT * from weather_table ORDER BY name ASC")
    List<City> getSynchrAllCities();

    @Query("DELETE FROM weather_table WHERE _id = :id")
    void deleteCity(double id);

    @Query("SELECT * FROM weather_table WHERE _id = :id")
    City getCityById(double id);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    int update(City city);
}
