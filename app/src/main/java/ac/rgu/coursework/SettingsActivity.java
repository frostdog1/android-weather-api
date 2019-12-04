package ac.rgu.coursework;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import java.util.List;

import ac.rgu.coursework.model.LocationObject;

/**
 * Created by Thomas 14/10/2019
 */
public class SettingsActivity extends AppCompatActivity {

    private LocationsAdapter locationsAdapter;

    private LocationsDatabase locDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show ActionBar back button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#637477")));

        // Set up KeyEvent listener for EditText
        final EditText locationEditText = findViewById(R.id.et_add_location);
        locationEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Enter key pressed
                    // TODO get ID From sean's thing
                    addLocation(1234, locationEditText.getText().toString());
                }
                return false;
            }
        });

        // Set up click listener for add location button
        Button addBtn = findViewById(R.id.btn_add_location);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO get ID From sean's thing
                addLocation(1234, locationEditText.getText().toString());
            }
        });

        // Get the locations from the database
        locDatabase = new LocationsDatabase();
        List<LocationObject> locationObjects = locDatabase.getLocations();

        // Send list of locations to adapter to populate RecyclerView
        locationsAdapter = new LocationsAdapter(locationObjects);

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
        locDatabase.saveLocation(id, location);

        // Add the location to the RecyclerView adapter
        LocationObject locationObject = new LocationObject(id, location);
        locationsAdapter.addLocation(locationObject);
    }

    /**
     * Remove location from database and RecyclerView
     *
     * @param id ID of location
     */
    private void removeLocation(int id) {
        locDatabase.removeLocation(id);

        locationsAdapter.removeLocation(id);
    }

    public boolean onItemLongClick(@NonNull MenuItem item) {
        // Dialog popup
        Dialog();
        return false;
    }

    public void Dialog() {

        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(getApplicationContext());
        dialogAlert.setTitle("Delete location?");

        dialogAlert.show();
    }
}
