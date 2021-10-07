package com.app.dailyweather.view;

/**
 * Created by dmbTEAM on 1/16/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.dailyweather.R;
import com.app.dailyweather.data.LocationUtil;
import com.app.dailyweather.model.Location;
import com.app.dailyweather.network.NetworkUtil;
import com.app.dailyweather.settings.AppConstants;
import com.johnhiott.darkskyandroidlib.models.DataBlock;
import com.johnhiott.darkskyandroidlib.models.DataPoint;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;

public class LocationFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefresh;

    private Location mLocation;

    private Map<String, String> precipitationTypeMap = new HashMap<>();

    public LocationFragment() {
    }

    public static LocationFragment newInstance(Location location) {
        LocationFragment fragment = new LocationFragment();
        fragment.setLocation(location);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

        precipitationTypeMap.put("snow", getContext().getString(R.string.snow));
        precipitationTypeMap.put("sleet", getContext().getString(R.string.sleet));
        precipitationTypeMap.put("rain", getContext().getString(R.string.rain));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather();
            }
        });
        populateData();
    }

    private void requestWeather() {
        NetworkUtil.getWeatherData(mLocation, new NetworkUtil.WeatherDataCallback() {
            @Override
            public void onData(WeatherResponse data) {
                mLocation.setWeatherResponse(data);
                mSwipeRefresh.setRefreshing(false);
                populateData();
                LocationUtil.saveLocation(mLocation, getContext());
            }

            @Override
            public void onError(RetrofitError error) {
                mSwipeRefresh.setRefreshing(false);
                ((MainActivity)getActivity()).showMessage(getString(R.string.no_forecast));
            }
        });
    }

    private void populateData() {
        if (mLocation != null && mLocation.getWeatherResponse() != null) {
            String timezone = mLocation.getWeatherResponse().getTimezone();
            DataPoint currently = mLocation.getWeatherResponse().getCurrently();
            if (currently != null) {
                setText(R.id.date, ViewUtil.getLocalFormattedDate(currently.getTime(), timezone));
                setText(R.id.temp, ViewUtil.getRoundedValue(currently.getTemperature()) + AppConstants.TEMPERATURE_UNIT);
                setText(R.id.summary, currently.getSummary());
                setText(R.id.humidity, ViewUtil.getPercentValue(currently.getHumidity()));
                setText(R.id.wind, ViewUtil.getRoundedValue(currently.getWindSpeed()) + AppConstants.SPEED_UNIT);
                setText(R.id.feels_like, ViewUtil.getRoundedValue(currently.getApparentTemperature()) + AppConstants.TEMPERATURE_UNIT);
                setText(R.id.dew_point, ViewUtil.getRoundedValue(currently.getDewPoint()) + AppConstants.TEMPERATURE_UNIT);
                setText(R.id.pressure, ViewUtil.getRoundedValue(currently.getPressure()) + AppConstants.PRESSURE_UNIT);
                setText(R.id.visibility, currently.getVisibility()+ AppConstants.DISTANCE_UNIT);
                if (currently.getPrecipIntensity() != null && !currently.getPrecipIntensity().equals("0")) {
                    setText(R.id.precipitation_type, precipitationTypeMap.get(currently.getPrecipType()));
                    setText(R.id.precipitation, ViewUtil.getPercentValue(currently.getPrecipProbability()));
                }

            }
            DataBlock hourly = mLocation.getWeatherResponse().getHourly();
            if (hourly != null) {
                setText(R.id.hourly_summary, hourly.getSummary());
                RecyclerView hourlyForecast = (RecyclerView) getView().findViewById(R.id.hourly);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                hourlyForecast.setLayoutManager(layoutManager);
                hourlyForecast.setAdapter(new HourlyAdapter(hourly.getData(), timezone));
            }

            DataBlock daily = mLocation.getWeatherResponse().getDaily();
            if (daily != null && daily.getData().size() > 0) {
                List<DataPoint> upcomingDays = new ArrayList<>();
                for (int i = 1; i < daily.getData().size(); i++) {
                    upcomingDays.add(daily.getData().get(i));
                }
                DataPoint today = daily.getData().get(0);
                setText(R.id.sunrise, ViewUtil.getFormattedTime(today.getSunriseTime(), timezone));
                setText(R.id.sunset, ViewUtil.getFormattedTime(today.getSunsetTime(), timezone));
                setText(R.id.min_temp, ViewUtil.getRoundedValue(today.getTemperatureMin()) + AppConstants.TEMPERATURE_UNIT);
                setText(R.id.max_temp, ViewUtil.getRoundedValue(today.getTemperatureMax()) + AppConstants.TEMPERATURE_UNIT);
                RecyclerView dailyForecast = (RecyclerView) getView().findViewById(R.id.daily);
                dailyForecast.setLayoutManager(new LinearLayoutManager(getContext()));
                dailyForecast.setAdapter(new DailyAdapter(upcomingDays, timezone));
            }
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();
        requestWeather();
    }*/

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    private void setText(int id, String text) {
        if (text != null) {
            TextView textView = ((TextView)getView().findViewById(id));
            if (textView != null) {
                textView.setText(text);
            }
        }
    }
}
