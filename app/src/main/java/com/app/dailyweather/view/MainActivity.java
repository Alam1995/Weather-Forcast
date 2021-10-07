package com.app.dailyweather.view;

/**
 * Created by dmbTEAM on 1/16/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.dailyweather.R;
import com.app.dailyweather.data.LocationUtil;
import com.app.dailyweather.model.Location;
import com.app.dailyweather.network.NetworkUtil;
import com.app.dailyweather.settings.AppConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final int REQUEST_CODE_ASK_PERMISSION_LOCATION = 11;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private List<Location> mLocations = new ArrayList<>();

    private ViewPager mViewPager;

    private FloatingActionButton mFab;

    private MenuItem mDeleteItem;

    private GoogleApiClient mGoogleApiClient;

    private Map<String, Integer> mIconMap = new HashMap<>();

    private ImageView mPoweredBy;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPoweredBy = (ImageView) findViewById(R.id.poweredBy);
        mPoweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.DARK_SKY_POWERED_BY));
                startActivity(browserIntent);
            }
        });


        initIcons();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(mViewPager.getAdapter().getPageTitle(position));
                changeBackground();
                showDeleteItem(position != 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLocationSearch();
            }
        });

        mLocations = LocationUtil.getSavedLocations(this);
        refreshLocations();
        loadForecasts();

        locationCheck();
        showBannerAdd();

    }

    private void initIcons() {
        mIconMap.put("clear-day", R.drawable.back_gradient);
        mIconMap.put("clear-night", R.drawable.back_gradient);
        mIconMap.put("rain", R.drawable.back_gradient);
        mIconMap.put("snow", R.drawable.back_gradient);
        mIconMap.put("sleet", R.drawable.back_gradient);
        mIconMap.put("wind", R.drawable.back_gradient);
        mIconMap.put("fog", R.drawable.back_gradient);
        mIconMap.put("cloudy", R.drawable.back_gradient);
        mIconMap.put("partly-cloudy-day", R.drawable.back_gradient);
        mIconMap.put("partly-cloudy-night", R.drawable.back_gradient);
        mIconMap.put("hail", R.drawable.back_gradient);
        mIconMap.put("thunderstorm", R.drawable.back_gradient);
        mIconMap.put("tornado", R.drawable.back_gradient);
    }

    private void loadForecasts() {
        if (NetworkUtil.isNetworkConnected(this)){
            for (Location location : mLocations) {
                requestForecast(location);
            }
        } else {
            showMessage(getString(R.string.no_internet));
        }
    }

    public void changeBackground() {
        Location currentLocation = mLocations.get(mViewPager.getCurrentItem());
        if (currentLocation != null && currentLocation.getWeatherResponse() != null) {
            findViewById(R.id.main_content).setBackgroundResource
                    (mIconMap.get(currentLocation.getWeatherResponse().getCurrently().getIcon()));
        }
    }

    public void requestForecast(final Location location) {
        NetworkUtil.getWeatherData(location, new NetworkUtil.WeatherDataCallback() {
            @Override
            public void onData(WeatherResponse data) {
                location.setWeatherResponse(data);
                refreshLocations();
                LocationUtil.saveLocation(location, MainActivity.this);
            }

            @Override
            public void onError(RetrofitError error) {
                showMessage(getString(R.string.no_forecast));
            }
        });
    }

    public void openLocationSearch() {

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.googleplaceapikey));
        }

        // Set the fields to specify which types of place data to return.
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place != null) {
                    /*Location location = buildLocation(place.getName().toString(),
                            place.getLatLng().latitude, place.getLatLng().longitude);*/
                    Location location = new Location(place.getName());
                    addLocation(location);
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Main", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    public Location buildLocation(String name, double latitude, double longitude) {
        Location result = new Location(name);
        result.setLatitude(latitude);
        result.setLongitude(longitude);
        return result;
    }

    public Location buildLocation(double latitude, double longitude) {
        Location result = new Location(latitude, longitude);
        result.setName(getLocationName(latitude, longitude));
        return result;
    }

    public String getLocationName(double latitude, double longitude) {
        String result = getString(R.string.current_location);
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        // In this sample, get just a single address.
                        1);
                if (addresses != null && addresses.size() > 0) {
                    result = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mDeleteItem = menu.findItem(R.id.action_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            if (mLocations.size() > 0) {
                Location location = mLocations.get(mViewPager.getCurrentItem());
                removeLocation(location);
            } else {
                showMessage(getString(R.string.cannot_delete_location));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteItem(boolean show) {
        if (mDeleteItem != null) {
            mDeleteItem.setVisible(show);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
        android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null) {
            Location location = buildLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
            location.setCurrent(true);
            addLocation(location);
        } else {
            showMessage(getString(R.string.no_device_location));
        }
    }

    public void addLocation(Location location) {
        if (!mLocations.contains(location)) {
            if (location.isCurrent()) {
                mLocations.add(0, location);
            } else {
                mLocations.add(location);
            }
            LocationUtil.saveLocation(location, this);
            refreshLocations();
            if (!location.isCurrent()) {
                mViewPager.setCurrentItem(mLocations.size() - 1);
            }
            requestForecast(location);
        }

    }

    public void removeLocation(Location location) {
        if (!location.isCurrent() && mLocations.contains(location)) {
            mLocations.remove(location);
            LocationUtil.removeLocation(location, this);
            refreshLocations();
        } else {
            showMessage(getString(R.string.cannot_delete_location));
        }
    }

    private void refreshLocations() {
        if (mSectionsPagerAdapter != null) {
            mSectionsPagerAdapter.setLocations(mLocations);
            mSectionsPagerAdapter.notifyDataSetChanged();
        } else {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mLocations);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        if (mLocations.size() > 0) {
            setTitle(mLocations.get(mViewPager.getCurrentItem()).getName());
            changeBackground();
            showDeleteItem(mViewPager.getCurrentItem() != 0);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    public void locationCheck() {
        if (NetworkUtil.isNetworkConnected(this)) {
            int hasCallPermission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasCallPermission == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSION_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    buildGoogleApiClient();
                } else {
                    // Permission Denied
                    showMessage(getString(R.string.location_denied));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showMessage(getString(R.string.no_device_location));
    }

    public void showMessage(String message) {
        final Snackbar snackBar = Snackbar.make(mFab, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(getString(R.string.dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showAlertDialg();
    }

    public void showAlertDialg(){
        String appName = getResources().getString(R.string.app_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Close "+appName);
        builder.setMessage("Do you want to close "+appName +" App?");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT).show();
               // showFullAdd();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showFullAdd(){
        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    public void showBannerAdd(){
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


}
