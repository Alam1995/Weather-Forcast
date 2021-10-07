package com.app.dailyweather.model;

import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

/**
 * Created by dmbTEAM on 1/16/2017.
 */

public class Location {
    private String name;
    private double latitude;
    private double longitude;
    private WeatherResponse weatherResponse;
    private boolean isCurrent;

    public Location(String name) {
        this.name = name;
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public WeatherResponse getWeatherResponse() {
        return weatherResponse;
    }

    public void setWeatherResponse(WeatherResponse weatherResponse) {
        this.weatherResponse = weatherResponse;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Location)) {
            return false;
        }
        Location location = (Location) obj;
        if (location.getName() != null && name != null) {
            return location.name.equals(name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode();
    }
}
