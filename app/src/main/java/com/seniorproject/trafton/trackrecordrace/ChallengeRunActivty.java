/*
* Thanks go out to Ben Jakuben for the walk through on Google Maps and Location services
* Thanks go to Niko Maravitsas for the timer tutorial
* http://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
*
* This is the activity for the SENDER CHALLENGE portion. This sends the challenge to the user.
* */

package com.seniorproject.trafton.trackrecordrace;


import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChallengeRunActivty extends AppCompatActivity implements LocationProvider.LocationCallback {

    //TODO:SAVE EACH MOTHERFUCKING RUN
    //TODO: IMPLEMENT RUNNING STATS INCLUDING DISTANCE, SPEED AND kcals

    /*Variables for getting Friends for challenges*/
    public List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected String[] mUsernames;
    /* THESE ABOVE CAN BE REMOVED IN REGULAR RUNS */

    public static final String TAG = "CHALLENGERUN";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationProvider mLocationProvider;
    Polyline route;
    private boolean isRunning = false;
    private ArrayList<LatLng> geoPoints = new ArrayList<LatLng>();
    private ArrayList<Double> distances = new ArrayList<Double>();
    private ArrayList<Double> speeds = new ArrayList<Double>();
    protected DecimalFormat df = new DecimalFormat("#.##");

    private TextView mRunTimeText;
    private TextView mRunSpeedText;
    private TextView mRunDistText;
    private TextView mRunCalsText;

    private Boolean mIsPlayButtonClicked;

    protected boolean startingPoint;
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
        /*DONE: change the layout slightly so that it includes the ability to send a challenge*/
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mLocationProvider = new LocationProvider(this, this);
        geoPoints = new ArrayList<LatLng>();

        /*ONLY FOR IF THERE IS GOING TO BE CHALLENGE DIALOG*/
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        //Add in code to inflate the tracking modules
        mRunTimeText = (TextView) findViewById(R.id.run_time_text);
        mRunDistText = (TextView) findViewById(R.id.run_dist_text);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_text);

        /*DONE: When the run is stopped, open up a new activity that allows a user to pick a friend to send a challenge to. */
        Toolbar runToolbar= (Toolbar) findViewById(R.id.toolbar_run);
        runToolbar.setTitle("Challenge run on " + getDate());
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

            /*DONE: new activity opens up allowing the user to select who to send the run to.
            * The run time and challenger ID will be saved to the challenge object
            * creates a new challenge object
            * This will create a relation with the challenge object*/
            case R.id.action_stop_run:
                //create a new dialog pop up
                createDialog();

                /*Inside of the regular run, the code will put a call to saveRun()*/
                //saveRun();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* --------- */


    /*---------- */

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

        /*Get friends list*/
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        getFriends();


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
                route = mMap.addPolyline(new PolylineOptions().width(5).color(android.R.color.holo_blue_dark).geodesic(true).visible(true));
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

    //handle new location
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng point = new LatLng(currentLatitude, currentLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 20));

        Double currentSpeed = toMPH(location.getSpeed());

        /*Get the running speed, even when the user has stopped running.*/
        mRunSpeedText.setText((df.format(currentSpeed + " m/h")));

        /*If this is the first location, then create a marker for the starting point*/
        if(geoPoints.size() == 0){
            MarkerOptions options = new MarkerOptions()
                    .position(point)
                    .title("Starting Point!");
            mMap.addMarker(options);
        }

        /*If the user has not paused the run, then get the metrics*/
        if(isRunning){
            /*Get Metrics*/
            geoPoints.add(point);
            route.setPoints(geoPoints);
            route = mMap.addPolyline(new PolylineOptions().width(5).color(android.R.color.holo_blue_dark).geodesic(true).visible(true));
            mRunSpeedText.setText((df.format(currentSpeed + " m/h")));
            speeds.add(currentSpeed);
            if(geoPoints.size() > 1){
                mRunDistText.setText(df.format(getNewDistance()) + " miles");
            }

        }

    }

    /*
    *Methods to calculate metrics. All measurements returned are approximations.
     */

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

    //calculates the current KCals being burned
    public void calculateCals(){
        /*TODO: FILL THIS MOTHERFUCKER IN*/

    }

    /*Method to update the screen on each location poll
    * Returns the speed as a Double*/
    public Double toMPH(float val){
        Float valTemp = val;
        Double mps = valTemp.doubleValue();
        mps = mps * 2.23694;
        return mps;

    }

    //get today's date in a simple format
    public String getDate(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");
        String todaysDate = formatter.format(today);
        return todaysDate;
    }


    /*------------------FINISHED CONTENT FOR JUST THE REGULAR RUN-------------------*/

    /*THIS METHOD IS ONLY FOR THE NON-CHALLENGE RUNS */
    //TODO implement this
    /*Save the run in the backend*/
    protected void saveRun(double dist, double time, double kcals){
        Run run = new Run(mCurrentUser,dist,time,kcals);
    }

    /*STOP HERE IF THIS VERSION IS UPDATED TO REPLACE THE REGULAR RUNNING VERSION
    * }*/

    /*Create Alert Dialog to pick challenger
    * Displays a list of the user's friends and allows them to select one friend to send the challenge to*/
    public void createDialog(){
        final AlertDialog.Builder challengeBuilder = new AlertDialog.Builder(ChallengeRunActivty.this);
        challengeBuilder.setTitle("Pick a Friend to Challenge");
        //TODO:INSERT FUNCTIONALITY FOR CREATING LISTS AND PICKING
        getFriends();
        challengeBuilder.setSingleChoiceItems(mUsernames, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d(TAG, "item selected: " + which);
            }
        });


        challengeBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Get updated selected position*/
                ListView lw = ((AlertDialog)dialog).getListView();
                int pos = lw.getCheckedItemPosition();
                Log.d(TAG, "item selected: " + pos);

                Challenge chal = new Challenge(mCurrentUser,0,mFriends.get(pos),0);
                //TODO: chal.setDistance(distanceRan);
                final ParseUser contender = mFriends.get(pos);
                chal.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, e.toString());
                        } else {
                            Log.d(TAG, "saved");
                            Log.d("PUSH", "Push send to: " + contender.getUsername());
                            sendPushNotification(contender, mCurrentUser);

                            //The line below is simply for testing purposes
                            //sendPushNotification(mCurrentUser,mCurrentUser);

                        }
                    }
                });
                Log.d(TAG, "Contender is: " + mFriends.get(pos).toString());
                Toast.makeText(getApplicationContext(), "You have sent a challenge to " + mFriends.get(pos).getUsername() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        /*Dismiss the dialog if the user does not want to send a challenge*/
        challengeBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        challengeBuilder.show();

    }

    public void getFriends(){
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mFriends = friends;
                /*If the user hasn't added ant friends, add the curren user to the list as indicator*/
                if (mFriends.size() == 0) {
                    mFriends.add(ParseUser.getCurrentUser());
                } else {
                    mUsernames = new String[friends.size()];
                    int i = 0;
                    for (ParseUser user : friends) {
                        mUsernames[i] = user.getUsername();
                        i++;
                    }

                }
            }
        });
    }

    /*Send a notification to a user that they have been challenged
    * @param the parse user to send notification to*/
    protected void sendPushNotification(ParseUser cont, ParseUser chal){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, cont.getObjectId());

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(chal.getUsername() + " has sent you a challenge!");
        push.sendInBackground();

    }


}
