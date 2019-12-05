package ac.rgu.coursework.model;

/**
 * Class for holding weather data
 */
public class WeatherData {
    // Main weather desc, eg "Drizzle"
    public final String main;

    public final double tempMax;

    public final double tempMin;

    private final String temperatureUnit;

    public WeatherData(String main,  double tempMax, double tempMin, String temperatureUnit) {
        this.main = main;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.temperatureUnit = temperatureUnit;
    }
}
