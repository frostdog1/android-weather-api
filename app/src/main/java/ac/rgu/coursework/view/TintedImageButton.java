package ac.rgu.coursework.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;

import androidx.core.widget.ImageViewCompat;

import com.rgu.coursework.R;

import ac.rgu.coursework.widget.CustomImageButton;

public class TintedImageButton extends CustomImageButton {

    public TintedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set dark or white tint depending on if the background is light or dark
     *
     * @param isBackgroundDark Is the background dark
     */
    public void setTint(boolean isBackgroundDark) {
        Resources res = getResources();
        ColorStateList tintList = isBackgroundDark ? res.getColorStateList(R.color.white_mode_tint) :
                res.getColorStateList(R.color.dark_mode_tint);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            // Work around broken workaround in ImageViewCompat
            if (this.getImageTintMode() == null) {
              //  this.setImageTintMode(PorterDuff.Mode.SRC_IN);
            }
        }

        ImageViewCompat.setImageTintList(this, tintList);
    }
}