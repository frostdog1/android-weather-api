package ac.rgu.coursework.interfaces;

import ac.rgu.coursework.model.WeatherData;

/**
 * Communicate between the RecyclerAdapter and MainActivity
 * Created by Thomas 29/09/2019
 */
public interface WeatherItemController {

    void onWeatherViewClicked(WeatherData weatherData);
}
