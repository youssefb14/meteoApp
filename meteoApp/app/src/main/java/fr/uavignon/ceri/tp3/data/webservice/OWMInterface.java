package fr.uavignon.ceri.tp3.data.webservice;

import fr.uavignon.ceri.tp3.data.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OWMInterface {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(@Query("q") String cityCountry, @Query("appid") String apiKey);
}