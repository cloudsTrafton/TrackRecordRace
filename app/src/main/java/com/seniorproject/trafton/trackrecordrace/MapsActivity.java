/*
* Thanks go out to Ben Jakuben for the walk through on Google Maps and Location services*/

package com.seniorproject.trafton.trackrecordrace;


import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;

    //ArrayList to store geopoints for current run
    private ArrayList<LatLng> geoPoints;

    //polyline that represented the route
    Polyline tracker;

    /*Load up widgets for tracking */
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;

    private TextView mRunTimeText;
    private TextView mRunSpeedText;
    private TextView mDistSpeedText;
    private TextView mRunHillText;

    private Boolean mIsPlayButtonClicked;

    //Handler to control timer tracking
    //Thank you to Nikos Maravitsas for the tutorial on timers

    private Handler timeHandler = new Handler();

    private long startTime = 0L;
    long timeInMillis = 0L;
    long timeSwapBuffer = 0L;
    long updatedTime = 0L;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mLocationProvider = new LocationProvider(this, this);
        geoPoints = new ArrayList<LatLng>(); //added

        //Add in code to inflate the tracking modules
        mRunTimeText = (TextView) findViewById(R.id.run_time_text);
        mPlayButton = (ImageButton) findViewById(R.id.go_button);
        mPauseButton = (ImageButton) findViewById(R.id.stop_button);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = SystemClock.uptimeMillis();
                timeHandler.postDelayed(updateTimerThread,0);
            }
        });
    }

    /*Code to update the timer, begins a new timer thread.*/
    private Runnable updateTimerThread = new Runnable() {
        public void run(){
            timeInMillis = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuffer + timeInMillis;

            //Get integer value from time update and put into textView
            int seconds = (int) (updatedTime/1000);
            int mins = (seconds % 60);
            int hours = (mins % 60);

            mRunTimeText.setText("" + hours + ":" +
                    String.format("%02d", mins) +
                    String.format("%03d", seconds));
            timeHandler.postDelayed(this,0);
        }

    };

    /* */

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //Get the new geopoints to redraw the line on each iteration
        geoPoints.add(latLng);
        drawRoute();

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    //method to draw polyline. Uses the recorded geopoints.
    public void drawRoute(){
        mMap.clear();
        PolylineOptions options = new PolylineOptions().width(5).color(android.R.color.holo_green_dark).geodesic(true);
        for(int i = 0; i < geoPoints.size(); i++){
            LatLng pt = geoPoints.get(i);
            options.add(pt);
        }
        Log.d(TAG,"GeoPoints recorded: " + geoPoints);
        tracker = mMap.addPolyline(options);
    }
}