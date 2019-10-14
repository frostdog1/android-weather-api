package ac.rgu.coursework;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

/**
 * Created by Thomas 14/10/2019
 */
public class SettingsActivity extends AppCompatActivity {

    private LocationsAdapter locationsAdapter;

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
                    addLocation(locationEditText.getText().toString());
                }
                return false;
            }
        });

        // Set up click listener for add location button
        Button addBtn = findViewById(R.id.btn_add_location);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation(locationEditText.getText().toString());
            }
        });

        locationsAdapter = new LocationsAdapter("London");

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

    private void addLocation(String location) {

    }
}
