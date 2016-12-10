package com.tantv.vnradiotruyen;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.tantv.vnradiotruyen.network.MySession;
import com.tantv.vnradiotruyen.network.core.ApiClient;
import com.tantv.vnradiotruyen.network.core.ApiConfig;

import java.util.Locale;


/**
 * Created by tantv on 3/4/15.
 */
public class App extends Application{
    private static App instance = null;
    public static int mIsCheckActivityRunning;

    public static final String BROADCAST_SEND_TO_MAIN_ACTIVITY = "com.broadcast.mainactivity";

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale("ja");
        res.updateConfiguration(conf, dm);

        ApiConfig apiConfig = ApiConfig.builder(getApplicationContext())
                .baseUrl(getResources().getString(R.string.url_base))
                .sessionStore(new MySession())
                .build();
        ApiClient.getInstance().init(apiConfig);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }
}
