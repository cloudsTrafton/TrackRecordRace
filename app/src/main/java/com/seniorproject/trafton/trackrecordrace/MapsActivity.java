/*
* Thanks go out to Ben Jakuben for the walk through on Google Maps and Location services
* Thanks go to Niko Maravitsas for the timer tutorial
* http://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
* */

package com.seniorproject.trafton.trackrecordrace;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    public static final String TAG = "cloudsTraf";
            //MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;

    //variable for toggle buttons
    private boolean isRunning = false;

    //ArrayList to store geopoints for current run
    private ArrayList<LatLng> geoPoints;

    //Array of distances
    private ArrayList<Float> distances;

    //total distance
    private float totalDistance;

    //polyline that represented the route
    Polyline tracker;

    /*Load up widgets for tracking */
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;

    private TextView mRunTimeText;
    private TextView mRunSpeedText;
    private TextView mRunDistText;
    private TextView mRunCalsText;

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
        mRunDistText = (TextView) findViewById(R.id.run_dist_text);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_text);

        Toolbar runToolbar= (Toolbar) findViewById(R.id.toolbar_run);
        runToolbar.setTitle("Run on " + getDate());
        runToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(runToolbar);
    }

    //Inflate the menu for the toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("XXX", "Menu created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps_run, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handles possibilities of menu items being selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_begin_run:
                if (!isRunning) {
                    startTime = SystemClock.uptimeMillis();
                    timeHandler.postDelayed(updateTimerThread,0);
                    item.setIcon(R.drawable.ic_pause_white_24dp);
                    isRunning = true;
                }
                else {
                    timeSwapBuffer += timeInMillis;
                    timeHandler.removeCallbacks(updateTimerThread);
                    item.setIcon(R.drawable.ic_play_arrow_white_24dp);
                    isRunning = false;

                }
                return true;

            case R.id.action_stop_run:
                //stop run, save run, and transport user to the stats for that run

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Code to update the timer, begins a new timer thread.*/
    private Runnable updateTimerThread = new Runnable() {
        public void run(){
            timeInMillis = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuffer + timeInMillis;

            //Get integer value from time update and put into textView
            int seconds = (int) (updatedTime/1000);
            //need two seconds variables for formatting purposes.
            int secs = seconds % 60;
            int mins = (seconds / 60);
            int hours = (mins / 60);

            mRunTimeText.setText("" + hours + ":" +
                    String.format("%02d", mins) + ":" +
                    String.format("%02d", secs));
            timeHandler.postDelayed(this, 0);
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
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    //handle new location
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //Get the new geopoints to redraw the line on each iteration
        geoPoints.add(latLng);
        //get the latest distance update
        /*if (geoPoints.size() > 2) {
            calculateDistance();
        } */
        //set the distance test
        //mRunDistText.setText(Float.toString(totalDistance) + " Meters");
        mRunSpeedText.setText((location.getSpeed() + " m/s"));

        //draw the polyline
        drawRoute();
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    /*
    *Methods to calculate metrics. All measurements returned are approximations.
     */
    //returns the latest distance between geoPoints. Append to total number
    public void calculateDistance(){
        //Location newLoc = new Location("Latest Location");
        //Location oldLoc = new Location("Last known Location");
        //LatLng newPt = geoPoints.get(geoPoints.size()- 1);
        //LatLng oldPt = geoPoints.get(geoPoints.size()-2);
        //distances.add(oldLoc.distanceTo(newLoc));
        //add to the distance variable
        //totalDistance = totalDistance + oldLoc.distanceTo(newLoc);
        //Log.d(TAG, "distance between points is: " + oldLoc.distanceTo(newLoc));
    }

    //calculates the current KCals being burned
    public void calculateKcals(){

    }

    //get today's date in a simple format
    public String getDate(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");
        String todaysDate = formatter.format(today);
        return todaysDate;
    }

    //method to draw polyline. Uses the recorded geopoints.
    public void drawRoute(){
        mMap.clear();
        PolylineOptions options = new PolylineOptions().width(5).color(android.R.color.holo_blue_dark).geodesic(true).visible(true);
        for(int i = 0; i < geoPoints.size(); i++){
            LatLng pt = geoPoints.get(i);
            options.add(pt);
        }
        Log.d(TAG,"GeoPoints recorded: " + geoPoints);
        mMap.addPolyline(options);
    }
}