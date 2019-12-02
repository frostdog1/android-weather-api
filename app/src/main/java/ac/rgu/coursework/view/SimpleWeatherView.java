package ac.rgu.coursework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rgu.coursework.R;

import ac.rgu.coursework.model.WeatherData;

/**
 * Displays weather for a day
 * Created by Thomas 09/10/2019
 */
public class SimpleWeatherView extends FrameLayout {

    // Weather data associated with this view
    private WeatherData mWeatherData;

    private TextView mTitleView;
    private TextView mValueView;

    public String mDataType;

    /**
     * Constructor for inflating from XML.
     */
    public SimpleWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get data type - Wind, pressure, humidity
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleWeatherView);
        mDataType = arr.getString(R.styleable.SimpleWeatherView_data_type);
        arr.recycle(); // Make object available for garbage collector

        inflate(getContext(), R.layout.simple_weather_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitleView = findViewById(R.id.simple_weather_view_title);
        mValueView = findViewById(R.id.simple_weather_view_value);
    }

    public void setText(String text) {
        setDisplayValues(text);
    }

    /**
     * Set display titles and their corresponding values
     */
    private void setDisplayValues(String text) {
        if (mDataType != null) {
            switch (mDataType) {
                case "wind":
                    mTitleView.setText(getResources().getString(R.string.title_wind));
                    mValueView.setText(text);
                    break;
                case "pressure":
                    mTitleView.setText(getResources().getString(R.string.title_pressure));
                    mValueView.setText(text);
                    break;
                default:
                    // Humidity
                    mTitleView.setText(getResources().getString(R.string.title_humidity));
                    mValueView.setText(text);
            }
        }
    }
}
