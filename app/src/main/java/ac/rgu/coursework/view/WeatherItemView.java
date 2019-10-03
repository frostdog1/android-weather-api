package ac.rgu.coursework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * Displays weather for a day
 * Created by Thomas 29/09/2019
 */
public class WeatherItemView extends FrameLayout {

    /**
     * Constructor for inflating from XML.
     */
    public WeatherItemView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {

    }
}
