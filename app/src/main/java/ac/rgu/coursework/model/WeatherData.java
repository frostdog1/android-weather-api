package ac.rgu.coursework.model;

/**
 * Class for holding weather data
 */
public class WeatherData {
    // Main weather description, eg "Drizzle"
    public final String main;

    public final String tempMax;

    public final String tempMin;

    public final String temperatureUnit;

    public WeatherData(String main,  String tempMax, String tempMin, String temperatureUnit) {
        this.main = main;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.temperatureUnit = temperatureUnit;
    }
}
