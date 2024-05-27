package fr.uavignon.ceri.tp3.data;

import com.squareup.moshi.Json;

public class WeatherResponse {
    public static class WeatherInfo {
        @Json(name = "description")
        public String description;

        @Json(name = "icon")
        public String icon;

        // Pas de champ supplémentaire nécessaire ici pour l'instant
    }

    public static class Main {
        @Json(name = "temp")
        public double temp;

        @Json(name = "humidity")
        public int humidity;
    }

    public static class Wind {
        @Json(name = "speed")
        public double speed;

        @Json(name = "deg")
        public int deg;
    }

    public static class Clouds {
        @Json(name = "all")
        public int all;
    }

    @Json(name = "weather")
    public WeatherInfo[] weather;

    @Json(name = "main")
    public Main main;

    @Json(name = "wind")
    public Wind wind;

    @Json(name = "clouds")
    public Clouds clouds;

    @Json(name = "dt")
    public long dt;

}
