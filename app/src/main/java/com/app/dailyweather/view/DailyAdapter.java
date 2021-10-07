package com.app.dailyweather.view;

/**
 * Created by dmbTEAM on 1/23/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.dailyweather.R;
import com.app.dailyweather.settings.AppConstants;
import com.johnhiott.darkskyandroidlib.models.DataPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private List<DataPoint> mData;
    private String mTimezone;

    private Map<String, Integer> mIconMap = new HashMap<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView day;
        public ImageView image;
        public TextView temperatureMin;
        public TextView temperatureMax;

        public ViewHolder(ViewGroup v) {
            super(v);
            day = (TextView) v.findViewById(R.id.day);
            image = (ImageView) v.findViewById(R.id.image);
            temperatureMin = (TextView) v.findViewById(R.id.temp_min);
            temperatureMax = (TextView) v.findViewById(R.id.temp_max);
        }
    }

    public DailyAdapter(List<DataPoint> data, String timezone) {
        mData = data;
        mTimezone = timezone;
        initIcons();
    }

    private void initIcons() {
        mIconMap.put("clear-day", R.drawable.icon_sun);
        mIconMap.put("clear-night", R.drawable.icon_moon);
        mIconMap.put("rain", R.drawable.icon_rain);
        mIconMap.put("snow", R.drawable.icon_snow);
        mIconMap.put("sleet", R.drawable.icon_hail);
        mIconMap.put("wind", R.drawable.icon_wind);
        mIconMap.put("fog", R.drawable.icon_fog);
        mIconMap.put("cloudy", R.drawable.icon_cloud);
        mIconMap.put("partly-cloudy-day", R.drawable.icon_cloud_sun);
        mIconMap.put("partly-cloudy-night", R.drawable.icon_cloud_moon);
        mIconMap.put("hail", R.drawable.icon_hail);
        mIconMap.put("thunderstorm", R.drawable.icon_thunderstorm);
        mIconMap.put("tornado", R.drawable.icon_tornado);
    }

    @Override
    public DailyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item, parent, false);
        ViewHolder vh = new ViewHolder((ViewGroup) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataPoint item = mData.get(position);
        holder.day.setText(ViewUtil.getDayOfWeek(item.getTime()));
        holder.image.setImageResource(mIconMap.get(item.getIcon()));
        holder.temperatureMin.setText(ViewUtil.getRoundedValue(item.getTemperatureMin())
                + AppConstants.TEMPERATURE_UNIT);
        holder.temperatureMax.setText(ViewUtil.getRoundedValue(item.getTemperatureMax())
                + AppConstants.TEMPERATURE_UNIT);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}

