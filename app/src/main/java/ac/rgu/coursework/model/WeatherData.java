package ac.rgu.coursework.model;

/**
 * Class for holding weather data
 * Created by Thomas 29/09/2019
 */
public class WeatherData {
    // Main weather desc, eg "Drizzle"
    public final String main;

    // Weather description, eg "Light intensity drizzle"
    public final String desc;

    public final int windSpeed;

    public final int windDir;

    public final String humidity;

    public final double temperature;

    public final double tempMax;

    public final double tempMin;

    private final String temperatureUnit;

    public WeatherData(String main, String desc, int windSpeed, int windDir, String humidity, double temperature, double tempMax,
                       double tempMin, String temperatureUnit) {
        this.main = main;
        this.desc = desc;
        this.windSpeed = windSpeed;
        this.windDir = windDir;
        this.humidity = humidity;
        this.temperature = temperature;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.temperatureUnit = temperatureUnit;
    }
}
