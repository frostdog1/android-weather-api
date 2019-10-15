package ac.rgu.coursework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.rgu.coursework.R;

import ac.rgu.coursework.interfaces.WeatherDownloaderController;
import ac.rgu.coursework.interfaces.WeatherItemController;
import ac.rgu.coursework.model.WeatherData;
import ac.rgu.coursework.view.TintedImageButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas 29/09/2019
 */
public class MainActivity extends AppCompatActivity implements WeatherItemController, WeatherDownloaderController {

    private RecyclerView mWeeklyWeatherRV;
    private SharedPreferences mPrefs;

    private List<WeatherData> mWeatherData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        TintedImageButton settingsBtn = findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settings, 10);
            }
        });

        // Find our weekly weather recycler view and set it horizontal using layout manager
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mWeeklyWeatherRV = findViewById(R.id.weekly_data_rv);
        mWeeklyWeatherRV.setLayoutManager(layoutManager);

        getWeatherData();
    }

    @Override
    public void onWeatherViewClicked(WeatherData weatherData) {
        // TODO
    }

    @Override
    public void onWeatherDownloaded(String result) {
        // Finished downloading weather JSON, parse the result and pass it to the WeeklyWeatherAdapter

        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weatherObject = (JSONObject) weatherArray.get(0);
            JSONObject mainObject = (JSONObject) jsonObject.get("main");
            JSONObject windObject = (JSONObject) jsonObject.get("wind");

            // Work out the temperature in the desired unit - the API provides it in Kelvin by default
            String temperatureUnit = mPrefs.getString("temperature_unit", "C");
            double temperature = convertTemperature(mainObject.getInt("temp"), temperatureUnit);

            // TODO do something with WeatherData object
            /*mWeatherData.add(new WeatherData(weatherObject.getString("main"), weatherObject.getString("description"),
                    windObject.getInt("speed"), windObject.getInt("deg"), mainObject.getString("humidity"),
                    temperature, temperatureUnit));*/

            WeeklyWeatherAdapter weeklyWeatherAdapter = new WeeklyWeatherAdapter(this, this, mWeatherData);
            mWeeklyWeatherRV.setAdapter(weeklyWeatherAdapter);
        } catch (Exception e) {
            // TODO something if it has an error parsing
        }
    }

    @Override
    public void onWeatherError() {

    }

    /**
     * Method for converting temperatures
     *
     * @param temp Location temperature provided by API in Kelvin
     * @param unit User's set temperature unit, default is Celsius
     * @return Temperature in user's temperature unit
     */
    private double convertTemperature(int temp, String unit) {
        if (unit.equals("K")) {
            return temp;
        } else if (unit.equals("F")) {
            return (temp - 273.15) * 9 / 5 + 32;
        } else {
            return temp - 273.15;
        }
    }

    /**
     * Download and set weather data
     */
    private void getWeatherData() {
        /* uncomment and make this work later
        // Get location from SharedPreferences, default location from resources (Aberdeen)
        String mLocation = mPrefs.getString("user_location", getResources().getString(R.string.default_location));

        // Start downloading weather JSON Asynchronously
        AsyncWeatherDownloader weatherDownloader = new AsyncWeatherDownloader(this, mLocation);
        weatherDownloader.execute();
        */

        for (int i = 0; i != 5; i++) {
            mWeatherData.add(new WeatherData("Sunny", "",
                    -1, -1, "", -1, 15, 8, "C"));
        }

        WeeklyWeatherAdapter weeklyWeatherAdapter = new WeeklyWeatherAdapter(this, this, mWeatherData);
        mWeeklyWeatherRV.setAdapter(weeklyWeatherAdapter);
    }
}