package com.app.dailyweather.application;

import android.support.multidex.MultiDexApplication;

import com.app.dailyweather.R;
import com.app.dailyweather.settings.AppConstants;
import com.google.android.gms.ads.MobileAds;
import com.johnhiott.darkskyandroidlib.ForecastApi;

/**
 * Created by dmbTEAM on 1/26/2017.
 */

public class WeatherApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ForecastApi.create(AppConstants.DARK_SKY_API_KEY);
        MobileAds.initialize(getApplicationContext(), getString(R.string.appId));
    }
}
