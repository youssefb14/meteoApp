package fr.uavignon.ceri.tp3.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Entity(tableName = "weather_table", indices = {@Index(value = {"name", "country"},
        unique = true)})
public class City  {

    public static final String TAG = City.class.getSimpleName();

    public static final long ADD_ID = -1;

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="_id")
    private long id;

    @NonNull
    @ColumnInfo(name="name")
    private String name;

    @NonNull
    @ColumnInfo(name="country")
    private String country;

    @ColumnInfo(name="countryCode")
    private String countryCode;

    @ColumnInfo(name="temperature")
    private Float temperature; // °C

    @ColumnInfo(name="humidity")
    private Integer humidity; // percentage

    @ColumnInfo(name="windSpeed")
    private Integer windSpeed; // km/h

    @ColumnInfo(name="windDirection")
    private String windDirection; // N,S,E,O

    @ColumnInfo(name="cloudiness")
    private Integer cloudiness; // percentage

    @ColumnInfo(name="icon")
    private String icon; // icon name (ex: 09d)

    @ColumnInfo(name="description")
    private String description; // description of the current weather condition (ex: light intensity drizzle)

    @ColumnInfo(name="lastUpdate")
    private long lastUpdate; // Last time when data was updated (Unix time)

    @Ignore
    public City(@NonNull String name, @NonNull String country) {
        this.name = name;
        setCountry(country);
    }

    public City(@NonNull String name, String country, Float temperature, Integer humidity, Integer windSpeed, String windDirection, Integer cloudiness, String icon, String description, long lastUpdate) {
        this.name = name;
        setCountry(country);
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.cloudiness = cloudiness;
        this.icon = icon;
        this.description = description;
        this.lastUpdate = lastUpdate;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getFullName() { return name+"("+country+")"; }

    public Float getTemperature() {
        return temperature;
    }

    public Integer getHumidity() { return humidity; }

    public Integer getWindSpeed() {
        return windSpeed != null ? windSpeed : 0;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getFullWind() {
        return windSpeed + " km/h (" + windDirection + ")";
    }

    public Integer getCloudiness() {
        return cloudiness;
    }

    public String getIcon() {
        return icon;
    }
    public String getIconUri() {
        if (icon == null || icon.isEmpty())
            return null;
        else
            return "@drawable/owm_"+icon+"_2x";
    }
    public String getSmallIconUri() {
        if (icon == null || icon.isEmpty())
            return null;
        else
            return "@drawable/owm_"+icon;
    }

    public String getDescription() {
        return description;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
    public String getStrLastUpdate() {
        Date date = new Date(lastUpdate*1000L);
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT, new Locale("FR","fr"));

        return shortDateFormat.format(date);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
        String code = mapCountryNameToCode.get(country);
        if (code == null)
            this.countryCode = "";
        else
            this.countryCode = code;
    }

    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public void setTemperature(float tempCelsius) {
        this.temperature = tempCelsius;
    }
    public void setTempFarenheit (float tempFarenheit) {
        temperature = (float)((5.0/9.0) * (tempFarenheit-32.0));
    }
    public void setTempKelvin (float tempKelvin) {
        temperature = tempKelvin-273.15f;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }
    public void setWindSpeedMPerS(float windSpeedMS) {
        this.windSpeed = (int)(windSpeedMS*3600f/1000f); // m/s to km/h
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }
    public void setWindDirection(float degree) {
        String[] arrComp = {"N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};
        int val = (int)((((float)degree)/22.5)+.5);
        this.windDirection = arrComp[val % 16];
    }

    public void setCloudiness(int cloudiness) {
        this.cloudiness = cloudiness;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setDescription(String description) { this.description = description; }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        String temp = this.temperature==null ? "--" : String.valueOf(Math.round(this.temperature));
        return this.name+"("+this.country+"): "+temp+"°C";
    }

    private static Map<String, String> mapCountryNameToCode = getCountryNameToCodeMap();

    @NonNull
    static Map<String, String> getCountryNameToCodeMap() {
        final Map<String, String> displayNameToCountryCode = new HashMap<>();
        for (String countryCode : Locale.getISOCountries()) {
            final Locale locale = new Locale("", countryCode);
            displayNameToCountryCode.put(locale.getDisplayCountry(), countryCode);
        }
        return displayNameToCountryCode;
    }
}
