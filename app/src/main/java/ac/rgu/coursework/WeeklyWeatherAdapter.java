package ac.rgu.coursework;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rgu.coursework.R;

import java.util.List;

import ac.rgu.coursework.model.WeatherData;
import ac.rgu.coursework.view.WeatherItemView;

/**
 * RecyclerView.Adapter for weekly weather items at the bottom of the main activity
 */
public class WeeklyWeatherAdapter extends RecyclerView.Adapter<WeeklyWeatherAdapter.ViewHolder> {

    private List<WeatherData> mWeatherDataList;

    private final Context mContext;

    /**
     * Adapter constructor
     *
     * @param weatherDataList List of weather data
     */
    @SuppressWarnings("WeakerAccess")
    public WeeklyWeatherAdapter(Context context, List<WeatherData> weatherDataList) {
        this.mContext = context;
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

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set day
        TextView dayTextView = holder.weatherItemView.findViewById(R.id.tv_day);
        Resources res = mContext.getResources();
        String dayString;
        switch (position) {
            case (0):
                dayString = res.getString(R.string.monday);
                break;
            case (1):
                dayString = res.getString(R.string.tuesday);
                break;
            case (2):
                dayString = res.getString(R.string.wednesday);
                break;
            case (3):
                dayString = res.getString(R.string.thursday);
                break;
            case (4):
                dayString = res.getString(R.string.friday);
                break;
            default:
                dayString = res.getString(R.string.sunday);
                break;
        }
        dayTextView.setText(dayString);

        // Set icon
        ImageView weatherIcon = holder.weatherItemView.findViewById(R.id.iv_weekly_icon);
        weatherIcon.setImageDrawable(res.getDrawable(R.drawable.ic_weather_sunny));

        // Set max temp
        TextView maxTempTextView = holder.weatherItemView.findViewById(R.id.tv_max_temp);
        String maxTemp = String.valueOf(mWeatherDataList.get(position).tempMax);
        maxTempTextView.setText(maxTemp);

        // Set min temp
        TextView minTempTextView = holder.weatherItemView.findViewById(R.id.tv_min_temp);
        String minTemp = String.valueOf(mWeatherDataList.get(position).tempMin);
        minTempTextView.setText(minTemp);
    }

    @Override
    public int getItemCount() {
        return mWeatherDataList.size();
    }

    // Provide a reference to the views for each data item
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
