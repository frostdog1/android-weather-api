package ac.rgu.coursework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ac.rgu.coursework.interfaces.WeatherDownloaderController;
import ac.rgu.coursework.model.LocationObject;
import ac.rgu.coursework.model.WeatherData;
import ac.rgu.coursework.view.SimpleWeatherView;
import ac.rgu.coursework.view.TintedImageButton;

public class MainActivity extends AppCompatActivity implements WeatherDownloaderController {

    // RecyclerView used to display weekly weather information
    private RecyclerView mWeeklyWeatherRV;

    private SharedPreferences mPrefs;

    private List<WeatherData> mWeatherData = new ArrayList<>();

    // Today's weather TextViews
    private TextView mLocationTV, mTempTV, mDescTV;
    // Today's weather SimpleWeatherViews
    private SimpleWeatherView mHumidityView, mWindView, mPressureView;
    // Weather icon
    private FrameLayout mWeatherIcon;

    // Night/Moon weather icon
    private FrameLayout timeWeatherIcon;

    // User's favourite location ID
    private int mLocationID;
    // User's favourite location name
    private String mLocationName;

    private LocationsDatabase locationsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(OneStormApplication.getContext());

        // Find views
        mLocationTV = findViewById(R.id.location_tv);
        mTempTV = findViewById(R.id.today_temp_tv);
        mDescTV = findViewById(R.id.today_desc_tv);

        mHumidityView = findViewById(R.id.weather_view_humidity);
        mWindView = findViewById(R.id.weather_view_wind);
        mPressureView = findViewById(R.id.weather_view_pressure);

        mWeatherIcon = findViewById(R.id.top_section_weather_img);

        // Refresh button
        TintedImageButton mRefreshBtn = findViewById(R.id.btn_refresh);
        mRefreshBtn.setOnClickListener(v -> {
            // Refresh the data
            getWeatherData();

            // Reset background for night mode change
            setDayNightDisplay();

            // Indicate to the user that something is happening
            Toast.makeText(MainActivity.this, getResources()
                    .getString(R.string.action_refresh), Toast.LENGTH_SHORT).show();
        });

        // Share button
        TintedImageButton mShareBtn = findViewById(R.id.btn_share);
        mShareBtn.setOnClickListener(v -> {
            // Construct message of today's weather info
            String temp = mTempTV.getText().toString();
            String desc = mDescTV.getText().toString();
            String humidity = mHumidityView.getText();
            String wind = mWindView.getText();
            String pressure = mPressureView.getText();

            String shareMsg = "Weather forecast for " + mLocationName + ": Temperature - " + temp
                    + ", Description - " + desc + ", Humidity - " + humidity + ", Wind - " + wind
                    + ", Pressure - " + pressure;

            // Send message as plain text intent for other apps to open
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "OneStorm Weather");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.action_share)));
        });

        // Settings button click listener
        TintedImageButton settingsBtn = findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(v -> {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(settings, 10);
        });

        // Find our weekly weather recycler view and set it horizontal using layout manager
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mWeeklyWeatherRV = findViewById(R.id.weekly_data_rv);
        mWeeklyWeatherRV.setLayoutManager(layoutManager);

        locationsDatabase = new LocationsDatabase();

        // Get user set location
        mLocationID = getLocationID();
        mLocationName = getLocationName();

        // Get weather data
        getWeatherData();

        // Set background color for day/night
        timeWeatherIcon = findViewById(R.id.day_night_img);
        setDayNightDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh user's location
        mLocationID = getLocationID();
        mLocationName = getLocationName();

        // Show connection popup if there is no internet
        if (!isConnected()) showConnDialog();
    }

    /**
     * Set theme based on time, aka darker background and moon/sun icon
     */
    private void setDayNightDisplay() {
        ScrollView mMainLayout = findViewById(R.id.main_layout);
        FrameLayout mBackgroundTop = findViewById(R.id.top_section_background);

        boolean isNight = getNightCycle();
        if (isNight) {
            timeWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_moon));
            mBackgroundTop.setBackgroundColor(getResources().getColor(R.color.top_section_background_night));
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.bottom_section_background_night));
        } else {
            timeWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_sun));
            mBackgroundTop.setBackgroundColor(getResources().getColor(R.color.top_section_background_day));
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.bottom_section_background_day));
        }
    }

    /**
     * Get users location from database using location ID from SharedPreferences
     *
     * @return User's location
     */
    private int getLocationID() {
        List<LocationObject> locationsList = locationsDatabase.getLocations();
        // Loop through locations until location with ID is found
        // If it isn't found, use default - user must have deleted the location
        // Aberdeen,GB = 2657832
        int favouriteID = mPrefs.getInt("favourite_id", 2657832);

        // Get user's favourite location, aberdeen id as default
        int userLocation = 2657832;
        for (int i = 0; i != locationsList.size(); i++) {
            if (locationsList.get(i).getId() == favouriteID) {
                userLocation = locationsList.get(i).getId();
                break;
            }
        }

        return userLocation;
    }

    /**
     * Get users location name from database using location ID from SharedPreferences
     *
     * @return User's location
     */
    private String getLocationName() {
        List<LocationObject> locationsList = locationsDatabase.getLocations();
        // Loop through locations until location with ID is found
        // If it isn't found, use default - user must have deleted the location
        // Aberdeen,GB = 2657832
        int favouriteID = mPrefs.getInt("favourite_id", 2657832);

        // Get user's favourite location, aberdeen id as default
        String userLocation = getResources().getString(R.string.default_location);
        for (int i = 0; i != locationsList.size(); i++) {
            if (locationsList.get(i).getId() == favouriteID) {
                userLocation = locationsList.get(i).getLocation();
                break;
            }
        }

        return userLocation;
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

        // Delete weather data if it already exists, the refresh button was clicked
        if (!mWeatherData.isEmpty()) mWeatherData.clear();

        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray listArray = (JSONArray) jsonObject.get("list");

            // Get temperature unit saved in SharedPreferences
            String temperatureUnit = mPrefs.getString("temperature_unit", "C");

            // Loop through 5 days of data in the json file downloaded
            for (int i = 0; i != 5; i++) {
                // Parse the JSON file
                JSONObject dayMainObj = (JSONObject) listArray.get(i);

                JSONArray weatherArray = (JSONArray) dayMainObj.get("weather");
                JSONObject weatherObject = (JSONObject) weatherArray.get(0);
                JSONObject mainObject = (JSONObject) dayMainObj.get("main");
                JSONObject windObject = (JSONObject) dayMainObj.get("wind");

                // Weather description used by both today weather and weekly weather
                String description = weatherObject.getString("main");

                // First iteration gets the data needed for today's weather and displays it
                // all other iterations get data needed for the weekly view and sends it to the
                // recycler view
                if (i == 0) {
                    // Work out the temperature in the desired unit - the API provides it in Kelvin by default
                    double todayTemperature = convertTemperature(mainObject.getInt("temp"), temperatureUnit);

                    int windSpeed = windObject.getInt("speed");

                    // Set icon based on description
                    setWeatherIcon(description);

                    // Set today's forecast

                    // Set location
                    mLocationTV.setText(mLocationName);

                    // Set temperature
                    mTempTV.setText((int) todayTemperature + " " + temperatureUnit);

                    // Set description
                    mDescTV.setText(description);

                    // Set humidity
                    String humidityString = mainObject.getString("humidity") + "%";
                    mHumidityView.setText(humidityString);

                    // Set wind
                    String windString = windSpeed + " mph";
                    mWindView.setText(windString);

                    // Set pressure
                    String pressureString = mainObject.getInt("pressure") + " mb";
                    mPressureView.setText(pressureString);
                } else {
                    // Set weekly forecast

                    // Get min and max temperature values in int type
                    DecimalFormat df = new DecimalFormat("#.#");
                    double maxTempDbl = mainObject.getDouble("temp_max");
                    String maxTemp = df.format(convertTemperature(maxTempDbl, temperatureUnit));
                    double minTempDbl = mainObject.getDouble("temp_min");
                    String minTemp = df.format(convertTemperature(minTempDbl, temperatureUnit));

                    // Add the new WeatherData object to the ArrayList to be sent to the WeeklyWeatherAdapter
                    mWeatherData.add(new WeatherData(description, maxTemp, minTemp, temperatureUnit));
                }
            }

            // Initialise RecyclerView and its Adapter
            // Set the RecyclerView adapter with the weather data from the downloaded JSON file
            WeeklyWeatherAdapter weeklyWeatherAdapter = new WeeklyWeatherAdapter(this, mWeatherData);
            mWeeklyWeatherRV.swapAdapter(weeklyWeatherAdapter, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWeatherError(Exception e) {
        e.printStackTrace();
    }

    /**
     * Method for converting temperatures
     *
     * @param temp Location temperature provided by API in Kelvin
     * @param unit User's set temperature unit, default is Celsius
     * @return Temperature in user's temperature unit
     */
    private double convertTemperature(double temp, String unit) {
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
        // Start downloading weather JSON Asynchronously
        AsyncWeatherDownloader weatherDownloader = new AsyncWeatherDownloader(this, mLocationID, true);
        weatherDownloader.execute();
    }

    /**
     * Set the background theme depending on time and weather
     *
     * @param weather Sun | Clouds | Snow | Rain
     */
    private void setWeatherIcon(String weather) {
        Log.d(" CUNT ", " fuck" + weather);
        // Set background weather image
        switch (weather) {
            case "Clouds":
                mWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_cloudy));
                break;
            case "Rain":
                mWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_rainy));
                break;
            case "Snow":
                mWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_snowy));
                break;
            case "Wind":
                mWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_windy));
                break;
            default:
                mWeatherIcon.setBackground(getResources().getDrawable(R.drawable.ic_sunny));
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
                .setNegativeButton(getResources().getString(R.string.action_wifi_connect), (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                // No click listener as we only want to close the dialog
                .setPositiveButton(getResources().getString(R.string.action_ignore), null);

        // Show the dialog
        builder.create().show();
    }
}