package ac.rgu.coursework;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import java.util.ArrayList;
import java.util.List;

import ac.rgu.coursework.model.LocationObject;

/**
 * Recycler.Adapter to populate list of locations in Settings activity
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<ToggleButton> toggleButtons = new ArrayList<>();

    private final List<LocationObject> mLocations;

    private int favouriteID;

    // SharedPreferences used to get and update favourite location
    private final SharedPreferences mPrefs;

    /**
     * Adapter constructor
     *
     * @param locations List of LocationObjects from database
     */
    @SuppressWarnings("WeakerAccess")
    public LocationsAdapter(List<LocationObject> locations) {
        this.mLocations = locations;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(OneStormApplication.getContext());
        favouriteID = mPrefs.getInt("favourite_id", 2657832);
    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @return New LinearLayout placed inside a ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView locationTextView = holder.layout.findViewById(R.id.location_item_tv);
        // Get LocationObject from position and set the display text to the location
        locationTextView.setText(mLocations.get(position).getLocation());

        final ToggleButton toggleButton = holder.layout.findViewById(R.id.favourite_btn);
        // Set the favourite ID to checked
        if (mLocations.get(position).getId() == favouriteID) {
            toggleButton.setChecked(true);
        }
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Get the parent of the toggle button, use that to find the index in the RecyclerView
                    // Use that position to get the ID from the list of LocationObjects
                    // ToggleButton's LinearLayout parent -> ViewHolder -> RecyclerView
                    LinearLayout toggleButtonParent = (LinearLayout) buttonView.getParent();
                    LinearLayout parentsParent = ((LinearLayout) toggleButton.getParent());

                    int indexOfToggleBtn = ((RecyclerView) parentsParent.getParent()).indexOfChild(toggleButtonParent);
                    int id = mLocations.get(indexOfToggleBtn).getId();

                    mPrefs.edit().putInt("favourite_id", id).apply();

                    // Uncheck all other checkboxes
                    for (int i = 0; i != toggleButtons.size(); i++) {
                        if (toggleButtons.get(i) != buttonView) {
                            toggleButtons.get(i).setChecked(false);
                        }
                    }
                } else {
                    // User unchecked all checkboxes, set to default (aberdeen,gb)
                    mPrefs.edit().putInt("favourite_id", 2657832).apply();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mLocations.size());
    }

    // Provide a reference to the views for each data item
    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Our data items are WeatherItemViews
        private LinearLayout layout;

        private ViewHolder(LinearLayout v) {
            super(v);
            layout = v;
        }
    }

    /**
     * Add location to the Adapter
     */
    public void addLocation(LocationObject locationObject) {
        mLocations.add(locationObject);
        // Refresh changes
        notifyDataSetChanged();


    }

    /**
     * Remove location from Adapter
     *
     * @param id ID of location
     */
    public void removeLocation(int id) {
        // Loop through the locations until the ID is found
        LocationObject locationToDelete = null;
        for (int i = 0; i != mLocations.size(); i++) {
            if (mLocations.get(i).getId() == id) {
                locationToDelete = mLocations.get(i);
                break;
            }
        }

        // Try to remove the location, notify if something went wrong
        if (locationToDelete != null) {
            mLocations.remove(locationToDelete);
        } else {
            Toast.makeText(OneStormApplication.getContext(), "Error removing location!",
                    Toast.LENGTH_SHORT).show();
        }

        // Refresh changes
        notifyDataSetChanged();
    }
}
