package ac.rgu.coursework;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ac.rgu.coursework.model.LocationObject;

public class SettingsActivity extends AppCompatActivity {

    // Used with RecyclerView to display locations and favourite buttons
    private LocationsAdapter locationsAdapter;

    // Retrieve and store locations
    private LocationsDatabase locDatabase;

    // ArrayList variable that will hold the city names
    ArrayList<String> cityNameList = new ArrayList<>();

    // ArrayList variable that will hold the city ID's
    ArrayList<Integer> cityIdList = new ArrayList<>();

    // SharedPreferences to store temperature unit
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(OneStormApplication.getContext());

        // when activity first opens, read the contents of
        // locations.txt, which holds all the city names and ID's
        readLocations();

        // Show ActionBar back button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#637477")));

        setupTempUnitSpinner();

        /* create an AutoCompleteTextView which allows for suggestions to
         * show up under a input field as the user types*/
        final AutoCompleteTextView locationEditText = findViewById(R.id.et_add_location);

        // create an array adapter that allows for data to be displayed in a UI element
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNameList);

        // maps the list of city names (cityNameList) to the AutoCompleteTextView
        locationEditText.setAdapter(autoAdapter);

        // when an item is clicked, perform the functions below
        locationEditText.setOnItemClickListener((parent, view, position, id) -> {
            // get the string from the item the user selected
            String cityName = (String) parent.getItemAtPosition(position);
            // find the index of the location the user selected in cityNameList ArrayList
            int cityPos = cityNameList.indexOf(cityName);

            // use the index to get the city ID from cityIdList
            int cityId = cityIdList.get(cityPos);

            // add location using id and city
            addLocation(cityId, cityName);
        });

        // Get the locations from the database
        locDatabase = new LocationsDatabase();
        List<LocationObject> locationObjects = locDatabase.getLocations();

        // Send list of locations to adapter to populate RecyclerView
        locationsAdapter = new LocationsAdapter(locationObjects, locDatabase);

        RecyclerView recyclerView = findViewById(R.id.rv_location_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(locationsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // ActionBar back button clicked
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Add location to database and RecyclerView
     *
     * @param id       City ID
     * @param location Location
     */
    private void addLocation(int id, String location) {
        // Save the location to the database
        locDatabase.saveLocation(id, location);

        // Add the location to the RecyclerView adapter
        LocationObject locationObject = new LocationObject(id, location);
        locationsAdapter.addLocation(locationObject);
    }

    /**
     * Set up the temperature unit spinner
     */
    private void setupTempUnitSpinner() {
        // Find the spinner
        Spinner spinTempUnit = findViewById(R.id.spin_temp_units);

        // Make spinner contents and drop down style
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.temperature_units));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set it
        spinTempUnit.setAdapter(adapter);

        // Set default temperature unit to what the user saved by using index of unit in string resource array
        String tempUnit = mPrefs.getString("temperature_unit", "C");
        spinTempUnit.setSelection(tempUnit.equals("C") ? 0 : (tempUnit.equals("F") ? 1 : 2));

        // Respond to spinner item clicks
        spinTempUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                String temperatureUnit = spinTempUnit.getSelectedItem().toString();

                // Convert temperature unit to shorthand so it can be displayed
                String tempUnitShort = "C";
                if (temperatureUnit.equals("Fahrenheit")) {
                    tempUnitShort = "F";
                } else if (temperatureUnit.equals("Kelvin")) {
                    tempUnitShort = "K";
                }

                // Save the temperature unit with SharedPreferences
                mPrefs.edit().putString("temperature_unit", tempUnitShort).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    /**
     * Get locations and IDs from locations.txt file
     */
    public void readLocations() {
        // contain within a thread to avoid too much processing on UI thread
        Thread thread = new Thread(() -> {

            // create a BufferedReader variable, which provides efficient reading of lines (in this case)
            BufferedReader reader;

            // set up exceptions
            try { // test for errors as code below executes

                // read the locations.txt file from assets using the getAssets() method
                reader = new BufferedReader(new InputStreamReader(getAssets().open("locations.txt")));

                // set a variable that will hold all the data of a single line in the file
                String line;

                // only run the code below if the text file has data within it
                // it will iterate until a line has no data
                while ((line = reader.readLine()) != null) {

                    /*the data in the text file is split by commas in each line
                     * for example: 1234567, Aberdeen City.
                     * This needs to be split so it can be processed later */
                    String[] result = line.split(",", 2);

                    /*as the lines are now split into two
                     * cityID can be found using index of 0
                     * while cityName is index of 1
                     * this will do this for every line*/
                    int cityID = Integer.parseInt(result[0]);
                    String cityName = result[1];

                    // add the string of cityName to the List
                    cityNameList.add(cityName);

                    // add the string of cityID
                    cityIdList.add(cityID);

                }
                // in the event of a failure, the exception will be shown
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        // start the tread
        thread.start();
    }
}
