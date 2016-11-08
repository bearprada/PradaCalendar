package example.prada.lab.pradaoutlook;

import android.app.Application;

import com.karumi.dexter.Dexter;

/**
 * Created by prada on 11/9/16.
 */

public class POApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Dexter.initialize(this);
    }
}
