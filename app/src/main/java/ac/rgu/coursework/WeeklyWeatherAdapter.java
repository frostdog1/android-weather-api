package ac.rgu.coursework;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import java.util.ArrayList;
import java.util.List;

import ac.rgu.coursework.interfaces.WeatherItemController;
import ac.rgu.coursework.model.WeatherData;
import ac.rgu.coursework.view.WeatherItemView;

public class WeeklyWeatherAdapter extends RecyclerView.Adapter<WeeklyWeatherAdapter.ViewHolder> {

    private ArrayList<WeatherItemView> mWeatherViews = new ArrayList<>();

    private List<WeatherData> mWeatherDataList;

    private final WeatherItemController mController;

    private final Context mContext;

    /**
     * Adapter constructor
     *
     * @param controller      Interface for sending clicks to the MainActivity
     * @param weatherDataList List of weather data
     */
    @SuppressWarnings("WeakerAccess")
    public WeeklyWeatherAdapter(Context context, WeatherItemController controller, List<WeatherData> weatherDataList) {
        this.mContext = context;
        this.mController = controller;
        this.mWeatherDataList = weatherDataList;
    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @return New WeeklyWeatherItemView placed inside a ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        WeatherItemView v = (WeatherItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weekly_weather_item_view, parent, false);

        mWeatherViews.add(v);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO
    }

    @Override
    public int getItemCount() {
        return mWeatherDataList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Our data items are WeatherItemViews
        private WeatherItemView weatherItemView;

        private ViewHolder(WeatherItemView v) {
            super(v);
            weatherItemView = v;
        }
    }
}
