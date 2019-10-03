package ac.rgu.coursework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rgu.coursework.R;

import ac.rgu.coursework.interfaces.WeatherItemController;
import ac.rgu.coursework.model.WeatherData;
import ac.rgu.coursework.view.WeatherItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for weekly weather items
 * Created by Thomas 29/09/2019
 */
public class WeeklyWeatherAdapter extends BaseAdapter {

    private ArrayList<WeatherItemView> mWeatherViews = new ArrayList<>();

    private List<WeatherData> mWeatherDataList;

    private final WeatherItemController mController;

    private final Context mContext;

    /**
     * Adapter constructor
     * @param controller Interface for sending clicks to the MainActivity
     * @param weatherDataList List of weather data
     */
    public WeeklyWeatherAdapter(Context context, WeatherItemController controller, List<WeatherData> weatherDataList) {
        this.mContext = context;
        this.mController = controller;
        this.mWeatherDataList = weatherDataList;
    }

    @Override
    public int getCount() {
        return mWeatherViews.size();
    }

    @Override
    public Object getItem(int position) {
        return mWeatherViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        // We don't need to use IDs
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Sometimes the adapter reloads itself if it is resized - eg keyboard moved, rotated etc.
        if (convertView != null) return convertView;

        // Inflate WeatherItemView from XML
        @SuppressLint({"ViewHolder", "InflateParams"})
        WeatherItemView view = (WeatherItemView) LayoutInflater.from(mContext)
                .inflate(R.layout.weekly_weather_item_view, null);

        // Set it's OnClickListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.onWeatherViewClicked(mWeatherDataList.get(mWeatherViews.indexOf(v)));
            }
        });

        // Add to array for later access
        mWeatherViews.add(view);

        return view;
    }
}
