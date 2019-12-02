package ac.rgu.coursework;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rgu.coursework.R;

import ac.rgu.coursework.interfaces.WeatherDownloaderController;
import ac.rgu.coursework.interfaces.WeatherItemController;
import ac.rgu.coursework.model.WeatherData;
import ac.rgu.coursework.view.SimpleWeatherView;
import ac.rgu.coursework.view.TintedImageButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Thomas 29/09/2019
 */
public class MainActivity extends AppCompatActivity implements WeatherItemController, WeatherDownloaderController {

    private ScrollView mMainLayout;
    private FrameLayout mBackgroundTop;

    private RecyclerView mWeeklyWeatherRV;
    private SharedPreferences mPrefs;

    private List<WeatherData> mWeatherData = new ArrayList<>();

    // Today's weather TextViews
    private TextView mLocationTV, mTempTV, mDescTV;
    // Today's weather SimpleWeatherViews
    private SimpleWeatherView mHumidityView, mWindView, mPressureView;
    // Weather icon
    private FrameLayout mWeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Find views
        mMainLayout = findViewById(R.id.main_layout);
        mBackgroundTop = findViewById(R.id.top_section_background);

        mLocationTV = findViewById(R.id.location_tv);
        mTempTV = findViewById(R.id.today_temp_tv);
        mDescTV = findViewById(R.id.today_desc_tv);

        mHumidityView = findViewById(R.id.weather_view_humidity);
        mWindView = findViewById(R.id.weather_view_wind);
        mPressureView = findViewById(R.id.weather_view_pressure);

        mWeatherIcon = findViewById(R.id.top_section_weather_img);

        // Settings button click listener
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

        // Get weather data
        getWeatherData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Show connection popup if there is no internet
        if (!isConnected()) showConnDialog();
    }

    @Override
    public void onWeatherViewClicked(WeatherData weatherData) {
        // TODO .. or not?
    }

    /**
     * Weather successfully downloaded from API, process the results
     *
     * @param result          JSON result from downloading forecast
     * @param isTodayForecast boolean to check if the forecast is for today's weather or weekly weather
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onWeatherDownloaded(String result, boolean isTodayForecast) {
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

            int windSpeed = windObject.getInt("speed");

            String description = weatherObject.getString("description");
            // Set background theme
            setTheme(description);

            if (isTodayForecast) {
                // Set today's forecast

                // Set location
                String mLocation = mPrefs.getString("user_location", getResources().getString(R.string.default_location));
                mLocationTV.setText(mLocation);

                // Set temperature
                mTempTV.setText(String.valueOf((int) temperature));

                // Set description
                mDescTV.setText(description);

                // Set humidity
                mHumidityView.setText(mainObject.getString("humidity"));

                // Set wind
                mWindView.setText(String.valueOf(windSpeed));

                // Set pressure
                mPressureView.setText(String.valueOf(mainObject.getInt("pressure")));
            } else {
                // Set weekly forecast
                mWeatherData.add(new WeatherData(weatherObject.getString("main"), description,
                        windSpeed, windObject.getInt("deg"), mainObject.getString("humidity"),
                        temperature, mainObject.getDouble("temp_max"), mainObject.getDouble("temp_min"), temperatureUnit));

                WeeklyWeatherAdapter weeklyWeatherAdapter = new WeeklyWeatherAdapter(this, this, mWeatherData);
                mWeeklyWeatherRV.setAdapter(weeklyWeatherAdapter);
            }
        } catch (Exception e) {
            // TODO something if it has an error parsing
        }
    }

    @Override
    public void onWeatherError(Exception e) {

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
        // Get location from SharedPreferences, default location from resources (Aberdeen)
        String mLocation = mPrefs.getString("user_location", getResources().getString(R.string.default_location));

        // Start downloading weather JSON Asynchronously
        AsyncWeatherDownloader weatherDownloader = new AsyncWeatherDownloader(this, mLocation, true);
        weatherDownloader.execute();
    }

    /**
     * Set the background theme depending on time and weather
     *
     * @param weather Sunny | Cloudy | Snowy | Rainy
     */
    // TODO set mWeatherIcon too
    private void setTheme(String weather) {
        boolean isNight = getNightCycle();

        // Set background color for day/night
        if (isNight) {
            mBackgroundTop.setBackgroundColor(getResources().getColor(R.color.top_section_background_night));
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.bottom_section_background_night));
        } else {
            mBackgroundTop.setBackgroundColor(getResources().getColor(R.color.top_section_background_day));
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.bottom_section_background_day));
        }

        // Set background weather image
        switch (weather) {
            case "Cloudy":

                break;
            default:

                break;
        }
    }

    /**
     * Figure out if its night or day
     *
     * @return True if night, false if day
     */
    private boolean getNightCycle() {
        Calendar cal = Calendar.getInstance();
        int timeOfDay = cal.get(Calendar.HOUR_OF_DAY);

        return timeOfDay < 6 || timeOfDay > 20;
    }

    /**
     * Check whether the device is connected to the internet
     *
     * @return true if the device is connected to the internet, false if it isn't
     */
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        // Get wifi and mobile data info
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        // Return result of if device is connected to wifi or mobile data
        return (wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected());
    }

    /**
     * Show a dialog popup with option to quit the app or go to WiFi i
     */
    private void showConnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_connection_loss))
                .setCancelable(true)
                .setNegativeButton(getResources().getString(R.string.action_wifi_connect), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                // No click listener as we only want to close the dialog
                .setPositiveButton(getResources().getString(R.string.action_ignore), null);

        // Show the dialog
        builder.create().show();
    }
}