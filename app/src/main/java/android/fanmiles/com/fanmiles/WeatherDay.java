package android.fanmiles.com.fanmiles;

import java.util.Arrays;

public class WeatherDay {
    private String cityName;
    private String dayName;
    private String weatherMain;
    private String weatherDescription;
    private int temperatureCurrent;
    private int temperatureHigh;
    private int temperatureLow;

    private final String[] happy_weather = {
            "Clear",
            "Sunny"
    };

    private final String[] sad_weather = {
            "Rain"
    };

    public WeatherDay(){

    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String value) {
        this.cityName = value;
    }

    public String getName() {
        return this.dayName;
    }

    public void setName(String value) {
        this.dayName = value;
    }

    public String getDescription() {
        return this.weatherDescription;
    }

    public void setDescription(String value) {
        this.weatherDescription = value;
    }

    public String getMain() {
        return this.weatherMain;
    }

    public void setMain(String value) {
        this.weatherMain = value;
    }

    public int getCurrentTemperature() {
        return this.temperatureCurrent;
    }

    public void setCurrentTemperature(int value) {
        this.temperatureCurrent = value;
    }

    public int getHighestTemperature() {
        return this.temperatureHigh;
    }

    public void setHighestTemperature(int value) {
        this.temperatureHigh = value;
    }

    public int getLowestTemperature() {
        return this.temperatureLow;
    }

    public void setLowestTemperature(int value) {
        this.temperatureLow = value;
    }

    public int getIcon() {
        if (Arrays.asList(sad_weather).contains(this.getMain())) {
            return R.drawable.icon_sad;
        }

        if (Arrays.asList(happy_weather).contains(this.getMain())) {
            return R.drawable.icon_happy;
        }

        return R.drawable.icon_whatever;
    }
}
