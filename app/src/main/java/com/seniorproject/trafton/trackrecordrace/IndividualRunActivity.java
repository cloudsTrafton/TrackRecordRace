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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IndividualRunActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    public static final String TAG = "INDIVIDUALRUN";
    private GoogleMap mMap;
    private LocationProvider mLocationProvider;
    protected ParseUser mCurrentUser = ParseUser.getCurrentUser();
    double weight = mCurrentUser.getDouble(ParseConstants.KEY_USER_WEIGHT);

    private ArrayList<LatLng> geoPoints = new ArrayList<LatLng>();
    private ArrayList<Double> distances = new ArrayList<Double>();
    private ArrayList<Double> speeds = new ArrayList<Double>();
    protected DecimalFormat df = new DecimalFormat("#.##");

    CalorieCalc calorieCounter = new CalorieCalc(weight);
    protected Double calories = 0.0;
    private boolean isRunning = false;
    protected int seconds;

    private TextView mRunTimeText;
    private TextView mRunSpeedText;
    private TextView mRunDistText;
    private TextView mRunCalsText;

    /*------------------Handle Timer--------------------------------*/
    //Thank you to Nikos Maravitsas for the tutorial on timers
    private Handler timeHandler = new Handler();

    private long startTime = 0L;
    long timeInMillis = 0L;
    long timeSwapBuffer = 0L;
    long updatedTime = 0L;

    /*Code to update the timer, begins a new timer thread.*/
    private Runnable updateTimerThread = new Runnable() {
        public void run(){
            timeInMillis = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuffer + timeInMillis;

            //Get integer value from time update and put into textView
            seconds = (int) (updatedTime/1000);
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
    /*__________________End Timer Code-----------------------------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*DONE: change the layout slightly so that it includes the ability to send a challenge*/
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mLocationProvider = new LocationProvider(this, this);


        //Add in code to inflate the tracking modules
        mRunTimeText = (TextView) findViewById(R.id.run_time_text);
        mRunDistText = (TextView) findViewById(R.id.run_dist_text);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_text);
        mRunCalsText = (TextView) findViewById(R.id.run_kcals_text);

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

    /*Handles possibilities of menu items being selected. Play and stop buttons control tracking */
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
            case R.id.action_stop_run:;

                //TODO add this to the individual running portion
                //saveRun(getNewDistance(),seconds,calories);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* --------- */


    /*---------- */
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

    /*Check to make sure that the map is not null*/
    private void setUpMapIfNeeded() {
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
        /*Void for now...*/
    }

    /*---------------------------HANDLE NEW LOCATION---------------------------------------*/
    /*This method does a lot of the heavy lifting and updates the metrics as well as the views*/

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng point = new LatLng(currentLatitude, currentLongitude);

        Double currentSpeed = toMPH(location.getSpeed());
        float currentSpeedMetersPerMinute = location.getSpeed() * 60;

        /*If this is the first location, then create a marker for the starting point*/
        if(geoPoints.size()==1){
            MarkerOptions options = new MarkerOptions().position(geoPoints.get(0)).title("Starting Point!");
            mMap.addMarker(options);
        }


        /*If the user has not paused the run, then get the metrics*/
        if(isRunning){
            geoPoints.add(point);
            speeds.add(currentSpeed);

            /*Redraw the polyline on the map*/
            PolylineOptions routeOptions = new PolylineOptions().addAll(geoPoints).color(R.color.ColorPolyline).width(10).visible(true);
            mMap.addPolyline(routeOptions);

            /*---------UPDATE VIEWS---------*/
            if(geoPoints.size() > 1){
                mRunDistText.setText(df.format(getNewDistance()) + " miles");
            }
            mRunSpeedText.setText((df.format(currentSpeed)) + " miles/hour");
            if (currentSpeed != 0){
                mRunCalsText.setText(df.format(calories));
                calories += calorieCounter.getCaloriesVO2(currentSpeedMetersPerMinute);
            }
            else {
                //Do not update
            }
            /*---------FINISH UPDATING VIEWS---------*/
        }
        else {
            mRunSpeedText.setText("0 miles/hour");
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
    }

    /*------------------------END HANDLE NEW LOCATION---------------------------------------------*/

    /*-------------------------Get the Metrics----------------------------------------------------*/

    //returns the latest distance between geoPoints. Append to total number. Multiplier returns distance in miles
    public Double getNewDistance(){
        float[] results = new float[1];
        LatLng prev = geoPoints.get(geoPoints.size()-2);
        LatLng latest = geoPoints.get(geoPoints.size()-1);
        Location.distanceBetween(prev.latitude, prev.longitude, latest.latitude, latest.longitude, results);
        Float res = results[0] * 0.000621371192f;
        Double dist = res.doubleValue();
        Log.d(TAG, "New distance is: " + dist);
        distances.add(dist);
        return calcDistance();
    }

    //Get the total distance so far
    public Double calcDistance(){
        Double sum = 0.0;
        for(int i = 0; i < distances.size();i++){
            sum+=distances.get(i);
        }
        return sum;
    }

    /*Method to update the screen on each location poll
    * Returns the speed as a Double*/
    public Double toMPH(float val){
        Float valTemp = val;
        Double mps = valTemp.doubleValue();
        mps = mps * 2.23694;
        return mps;

    }
    /*-------------------------Get the Metrics----------------------------------------------------*/

    //get today's date in a simple format
    public String getDate(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");
        String todaysDate = formatter.format(today);
        return todaysDate;
    }

    /*THIS METHOD IS ONLY FOR THE NON-CHALLENGE RUNS */
    protected void saveRun(double dist, double time, double kcals){
        Run run = new Run(mCurrentUser,dist,time,kcals);
    }
    /*-----END ONLY FOR NON-CHALLENGE RUNS------------*/



    /*------------------FINISHED CONTENT FOR JUST THE REGULAR RUN-------------------*/
}