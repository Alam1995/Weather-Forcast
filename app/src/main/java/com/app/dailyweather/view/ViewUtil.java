package com.app.dailyweather.view;

/**
 * Created by dmbTEAM on 1/23/2017.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ViewUtil {

    public static String getRoundedValue(double value) {
        String result = String.valueOf(Math.round(value));
        return result;
    }

    public static String getRoundedValue(String value) {
        Double val = Double.valueOf(value);
        return getRoundedValue(val);
    }

    public static String getPercentValue(String value) {
        double val = Double.parseDouble(value);
        int percent = (int)Math.round(val * 100);
        String result = percent + "%";
        return result;
    }

    public static String getFormattedTime(String time, String timezone) {
        long timeLong = Long.parseLong(time);
        return getFormattedTime(timeLong, timezone);
    }

    public static String getFormattedTime(long time, String timezone) {
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(calendar.getTime());
    }

    public static String getDayOfWeek(long time) {
        DateFormat sdf = new SimpleDateFormat("EEEE");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);
        return sdf.format(calendar.getTime());
    }

    public static String getLocalFormattedDate(long time, String timezone) {
        DateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(calendar.getTime());
    }
}
