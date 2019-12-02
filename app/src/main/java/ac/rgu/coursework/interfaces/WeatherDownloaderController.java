package ac.rgu.coursework.interfaces;

// Interface to communicate between MainActivity and AsyncWeatherDownloader
public interface WeatherDownloaderController {

    void onWeatherError(Exception e);

    void onWeatherDownloaded(String result, boolean isTodayForecast);
}
