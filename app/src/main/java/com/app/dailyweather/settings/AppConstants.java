package com.app.dailyweather.settings;

import com.johnhiott.darkskyandroidlib.models.Request;

/**
 * Created by dmbTEAM on 1/25/2017.
 */

public class AppConstants {

    /**
     * Your Dark Sky secret key.
     */
    public static final String DARK_SKY_API_KEY = "20d10a0e2a4aeb5225ed6f599d40466f";

    /**
     * Return weather conditions in the requested units. [UNITS] should be one of the following:
     *
     *  auto: automatically select units based on geographic location
     *  ca: same as si, except that windSpeed is in kilometers per hour
     *  uk2: same as si, except that nearestStormDistance and visibility are in miles and windSpeed is in miles per hour
     *  us: Imperial units (the default)
     *  si: SI units
     */
    public static final Request.Units UNITS = Request.Units.CA;

    /**
     * Temperature unit. Replace if it is not matching the UNITS nomenclature
     */
    public static final String TEMPERATURE_UNIT = String.valueOf((char)0xB0);

    /**
     * Speed unit. Replace if it is not matching the UNITS nomenclature
     */
    public static final String SPEED_UNIT = " kph";

    /**
     * Distance unit. Replace if it is not matching the UNITS nomenclature
     */
    public static final String DISTANCE_UNIT = " km";

    /**
     * PRESSURE unit. Replace if it is not matching the UNITS nomenclature
     */
    public static final String PRESSURE_UNIT = " hPa";

    /**
     * Enable or disable Admob in your Home screen
     */
    public static final boolean ENABLE_ADMOB_HOME_PAGE = false;

    /**
     * Enable or disable interstitial Admob
     */
    public static final boolean ENABLE_INTERSTITIAL_ADMOB = false;

    /**
     * DarkSky Contribution
     */
    public static final String DARK_SKY_POWERED_BY = "https://darksky.net/poweredby/";

}
