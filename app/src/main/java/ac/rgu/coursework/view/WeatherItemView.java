package ac.rgu.coursework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * Displays weather for a day
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
