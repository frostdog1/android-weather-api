package ac.rgu.coursework.interfaces;

public interface WeatherDownloaderController {

    void onWeatherError();

    void onWeatherDownloaded(String result);
}
