package ac.rgu.coursework;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import java.util.List;

import ac.rgu.coursework.interfaces.WeatherItemController;
import ac.rgu.coursework.model.WeatherData;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private final String mLocation;

    /**
     * Adapter constructor
     *
     * @param location TODO temporary string location, change to get data from database
     */
    @SuppressWarnings("WeakerAccess")
    public LocationsAdapter(String location) {
        this.mLocation = location;
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

        TextView locationTextView = v.findViewById(R.id.location_item_tv);
        locationTextView.setText(mLocation);

        ToggleButton toggleButton = v.findViewById(R.id.favourite_btn);
        toggleButton.setChecked(true);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return (mLocation == null ? 0 : 1);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Our data items are WeatherItemViews
        private LinearLayout layout;

        private ViewHolder(LinearLayout v) {
            super(v);
            layout = v;
        }
    }
}
