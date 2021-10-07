package com.app.dailyweather.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.app.dailyweather.model.Location;
import com.app.dailyweather.settings.AppConstants;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dmbTEAM on 1/18/2017.
 */

public class NetworkUtil {

    /**
     * Checks if the device is connected to a network.
     *
     * @param context application context
     * @return true if the device is connected
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static void getWeatherData(Location location, final WeatherDataCallback callback) {
        RequestBuilder weather = new RequestBuilder();

        Request request = new Request();
        request.setLat(String.valueOf(location.getLatitude()));
        request.setLng(String.valueOf(location.getLongitude()));
        request.setUnits(AppConstants.UNITS);
        request.setLanguage(Request.Language.ENGLISH);
        request.addExcludeBlock(Request.Block.CURRENTLY);
        request.removeExcludeBlock(Request.Block.CURRENTLY);

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                callback.onData(weatherResponse);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.onError(retrofitError);
            }
        });
    }

    public interface WeatherDataCallback {
        void onData(WeatherResponse data);
        void onError(RetrofitError error);
    }

}
