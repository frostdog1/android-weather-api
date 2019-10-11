package ac.rgu.coursework;

import android.os.AsyncTask;

import ac.rgu.coursework.interfaces.WeatherDownloaderController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Asyncronous task for downloading weather data
 * Created by Thomas 30/09/2019
 */
public class AsyncWeatherDownloader extends AsyncTask<Void, Void, String> {

    private WeatherDownloaderController mController;
    private final String mLocationQuery;

    /**
     * Class constructor
     *
     * @param controller    Interface used to send result to MainActivity
     * @param locationQuery User's input location
     */
    @SuppressWarnings("WeakerAccess")
    public AsyncWeatherDownloader(WeatherDownloaderController controller, String locationQuery) {
        this.mController = controller;
        this.mLocationQuery = locationQuery;
    }

    @Override
    protected String doInBackground(Void... voids) {
        URL url;
        StringBuilder jsonResult = new StringBuilder();
        try {
            // Quick Links for homepage
            url = new URL("http://api.openweathermap.org/data/2.5/weather?q="
                    + mLocationQuery
                    + "&APPID=ed75b176d8a7d9c678bd03c58c59a601");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            // Handle the response
            int status = conn.getResponseCode();
            // Error code 200 = Success
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    jsonResult.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            mController.onWeatherError();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // Return JSON result in the form of a string so it can be processed later
        return jsonResult.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // Send result to MainActivity
        mController.onWeatherDownloaded(s);
    }
}
