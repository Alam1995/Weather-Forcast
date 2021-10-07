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

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<DataPoint> mData;
    private String mTimezone;

    private Map<String, Integer> mIconMap = new HashMap<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public ImageView image;
        public TextView temperature;
        public ViewHolder(ViewGroup v) {
            super(v);
            time = (TextView) v.findViewById(R.id.hour);
            image = (ImageView) v.findViewById(R.id.image);
            temperature = (TextView) v.findViewById(R.id.temp);
        }
    }

    public HourlyAdapter(List<DataPoint> data, String timezone) {
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
    public HourlyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hour_item, parent, false);
        ViewHolder vh = new ViewHolder((ViewGroup) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataPoint item = mData.get(position);
        holder.time.setText(ViewUtil.getFormattedTime(item.getTime(), mTimezone));
        holder.image.setImageResource(mIconMap.get(item.getIcon()));
        holder.temperature.setText(ViewUtil.getRoundedValue(item.getTemperature())
                + AppConstants.TEMPERATURE_UNIT);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
