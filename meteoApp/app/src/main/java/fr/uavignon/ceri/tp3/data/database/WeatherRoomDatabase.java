package fr.uavignon.ceri.tp3.data.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.uavignon.ceri.tp3.data.City;

@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class WeatherRoomDatabase extends RoomDatabase {

    private static final String TAG = WeatherRoomDatabase.class.getSimpleName();

    public abstract CityDao cityDao();

    private static WeatherRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static WeatherRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WeatherRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                            // without populate
                        /*
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    WeatherRoomDatabase.class,"book_database")
                                    .build();

                     */

                            // with populate
                            INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    WeatherRoomDatabase.class,"book_database")
                                    .addCallback(sRoomDatabaseCallback)
                                    .build();

                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);

                    databaseWriteExecutor.execute(() -> {
                        // Populate the database in the background.
                        CityDao dao = INSTANCE.cityDao();
                        dao.deleteAll();

                        City[] cities = {new City("Avignon", "France"),
                        new City("Paris", "France"),
                        new City("Rennes", "France"),
                        new City("Montreal", "Canada"),
                        new City("Rio de Janeiro", "Brazil"),
                        new City("Papeete", "French Polynesia"),
                        new City("Sydney", "Australia"),
                        new City("Seoul", "South Korea"),
                        new City("Bamako", "Mali"),
                        new City("Istanbul", "Turkey")};

                        for(City newCity : cities)
                            dao.insert(newCity);
                        Log.d(TAG,"database populated");
                    });

                }
            };



}
