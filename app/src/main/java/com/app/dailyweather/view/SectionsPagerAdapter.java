package com.app.dailyweather.view;

/**
 * Created by dmbTEAM on 1/16/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.dailyweather.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private List<Location> mLocations = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm, List<Location> locations) {
        super(fm);
        this.mLocations = locations;
    }

    public void setLocations(List<Location> locations) {
        this.mLocations = locations;
    }

    @Override
    public Fragment getItem(int position) {
        return LocationFragment.newInstance(mLocations.get(position));
    }

    @Override
    public int getCount() {
        return mLocations.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mLocations.get(position).getName();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
