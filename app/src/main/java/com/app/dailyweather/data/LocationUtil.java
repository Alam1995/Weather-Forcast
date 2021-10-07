package com.app.dailyweather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.dailyweather.model.Location;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by dmbTEAM on 1/17/2017.
 */

public class LocationUtil {

    public static final String SAVED_LOCATIONS = "savedLocations";

    public static ArrayList<Location> getSavedLocations(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        Type savedLocationsType = new TypeToken<ArrayList<Location>>() {}.getType();
        ArrayList<Location> savedLocations = new ArrayList<Location>();
        try {
            String savedLocationsJson =
                    prefs.getString(SAVED_LOCATIONS, gson.toJson(savedLocations, savedLocationsType));
            savedLocations = gson.fromJson(savedLocationsJson, savedLocationsType);
        } catch (Exception e) {
            savedLocations = new ArrayList<Location>();
        }
        return savedLocations;
    }

    public static final void saveLocation(Location location, Context context) {
        Gson gson = new Gson();
        Type savedLocationsType = new TypeToken<ArrayList<Location>>() {}.getType();
        ArrayList<Location> savedLocations = getSavedLocations(context);
        if (savedLocations.contains(location)) {
            for (Location savedLocation : savedLocations) {
                if (savedLocation.getName() != null && location.getName() != null && savedLocation.getName().equalsIgnoreCase(location.getName())) {
                    savedLocation.setWeatherResponse(location.getWeatherResponse());
                    break;
                }
            }
        } else {
            savedLocations.add(location);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(SAVED_LOCATIONS, gson.toJson(savedLocations, savedLocationsType));
        ed.commit();
    }

    public static final void removeLocation(Location location, Context context) {
        Gson gson = new Gson();
        Type savedLocationsType = new TypeToken<ArrayList<Location>>() {}.getType();
        ArrayList<Location> savedLocations = getSavedLocations(context);
        savedLocations.remove(location);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(SAVED_LOCATIONS, gson.toJson(savedLocations, savedLocationsType));
        ed.commit();
    }
}
