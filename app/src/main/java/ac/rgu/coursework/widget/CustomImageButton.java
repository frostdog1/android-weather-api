package ac.rgu.coursework.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

/**
 * A subclass of AppCompatImageButton to add workarounds for bugs in Android Framework and Support
 * Library.
 */
public class CustomImageButton extends AppCompatImageButton {

    public CustomImageButton(Context context) {
        super(context);
    }

    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Override drawableStateChanged so we can update the button tint if the background changes light/dark
     */
    @Override
    protected void drawableStateChanged() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // Pre-N ImageView doesn't correctly invalidate drawables
            Drawable drawable = getDrawable();
            if (drawable != null && drawable.isStateful()
                    && drawable.setState(getDrawableState())) {
                invalidateDrawable(drawable);
            }
        }
        super.drawableStateChanged();
    }
}
