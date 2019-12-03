package ac.rgu.coursework;

import android.app.Application;
import android.content.Context;

/**
 * Application class to reference ApplicationContext
 */
public class OneStormApplication extends Application {

    private static OneStormApplication context;

    public OneStormApplication() {
        super();

        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
