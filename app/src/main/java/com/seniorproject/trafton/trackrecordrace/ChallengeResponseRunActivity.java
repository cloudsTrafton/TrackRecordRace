package com.seniorproject.trafton.trackrecordrace;

/*This is where users will respond to challenges and also where winners and losers are determined*/

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChallengeResponseRunActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    public static final String TAG = "RESPONSERUN";
    /*Challenge Variables*/
    public String challengeID;

    protected String challengeDistance;
    protected Double challengeDistanceNum;
    protected String challengeTime;
    protected Double challengeTimeNum;
    protected TextView mDistanceHackText;
    protected TextView mTimeHackText;
    protected String challengerName;

    public ParseUser challenger;
    private ParseObject chal;

    protected boolean win;
    protected TimeFormatter mTimeFormatter = new TimeFormatter();

    /*Regular old running variables*/
    private GoogleMap mMap;
    private LocationProvider mLocationProvider;
    protected ParseUser mCurrentUser = ParseUser.getCurrentUser();
    double weight = mCurrentUser.getDouble(ParseConstants.KEY_USER_WEIGHT);
    double wins = mCurrentUser.getDouble(ParseConstants.KEY_USER_WINS);
    double losses = mCurrentUser.getDouble(ParseConstants.KEY_USER_LOSSES);

    private ArrayList<LatLng> geoPoints = new ArrayList<LatLng>();
    private ArrayList<Double> distances = new ArrayList<Double>();
    private ArrayList<Double> speeds = new ArrayList<Double>();
    protected DecimalFormat df = new DecimalFormat("##.##");

    CalorieCalc calorieCounter = new CalorieCalc(weight);
    protected Double calories = 0.0;
    private boolean isRunning = false;
    protected int seconds;

    private Menu menu;
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

    /*In addition to setting up the activity, it also must get the information from the intent sent by the RecyclerView*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*-------RECIEVE DATA FROM INTENT---------------------*/
        setContentView(R.layout.activity_challenge_response_run);
        Intent intent = getIntent();
        challengerName = intent.getStringExtra(ParseConstants.BUNDLE_CHALLENGER);

        challengeDistance = intent.getStringExtra(ParseConstants.BUNDLE_DISTANCE);
        challengeDistanceNum = Double.parseDouble(challengeDistance);
        Log.d(TAG, "Challenge distance: " + challengeDistanceNum);

        challengeTime = intent.getStringExtra(ParseConstants.BUNDLE_TIME);
        challengeTimeNum = Double.parseDouble(challengeTime);
        Log.d(TAG, "Challenge Time: " + challengeTimeNum);

        //get the ID of the challenge
        challengeID = intent.getStringExtra(ParseConstants.BUNDLE_ID);
        Log.d(TAG, "Challenge ID: " + challengeID);

        mDistanceHackText = (TextView) findViewById(R.id.chal_disthack);
        mDistanceHackText.setText(df.format(toMiles(challengeDistanceNum)) + " miles");
        mTimeHackText = (TextView) findViewById(R.id.chal_timehack);
        mTimeHackText.setText(mTimeFormatter.getFormattedString((challengeTimeNum)));
        /*--------END RECIEVE DATA FROM INTENT--------------*/
        setUpMapIfNeeded();
        mLocationProvider = new LocationProvider(this, this);

        //Add in code to inflate the tracking modules
        mRunTimeText = (TextView) findViewById(R.id.run_time_text);
        mRunDistText = (TextView) findViewById(R.id.run_dist_text);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_text);
        mRunCalsText = (TextView) findViewById(R.id.run_kcals_text);

        Toolbar runToolbar= (Toolbar) findViewById(R.id.toolbar_run);
        runToolbar.setTitle("Challenge with " + challengerName);
        runToolbar.setSubtitle(getDate());
        runToolbar.setSubtitleTextColor(Color.WHITE);
        runToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(runToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("XXX", "Menu created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps_run, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

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
                forfeitChallenge();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
                mRunDistText.setText(df.format(toMiles(getNewDistance())) + " miles");
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

    //returns the latest distance between geoPoints. Append to total number
    public Double getNewDistance(){
        float[] results = new float[1];
        LatLng prev = geoPoints.get(geoPoints.size()-2);
        LatLng latest = geoPoints.get(geoPoints.size()-1);
        Location.distanceBetween(prev.latitude, prev.longitude, latest.latitude, latest.longitude, results);
        Float res = results[0];
        Double dist = res.doubleValue();
        Log.d(TAG, "New distance is: " + dist);
        distances.add(dist);
        return calcDistance();
    }

    //Get the total distance so far and check if the runner has completed the proposed distance
    public Double calcDistance(){
        Double sum = 0.0;
        for(int i = 0; i < distances.size();i++){
            sum+=distances.get(i);
        }
        if (sum >= challengeDistanceNum){
            finishResponse();
            Log.d(TAG, "Race is finished!");
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

    /*Gets Distance in Miles*/
    public Double toMiles(Double val){
        val = val * 0.000621371192f;
        return val;
    }
    /*-------------------------Get the Metrics----------------------------------------------------*/

    //get today's date in a simple format
    public String getDate(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");
        String todaysDate = formatter.format(today);
        return todaysDate;
    }

    /*THESE METHODS IS ONLY FOR RESPONSE RUNS */


    /*When the user finishes, do all of the logic here  */
    public void finishResponse(){
        isRunning = false;
        Log.d(TAG,"Seconds ran here: " + seconds);
        Log.d(TAG, "Challenger's time: " + challengeTimeNum);

        //current user wins
        if(seconds < challengeTimeNum){
            win = true;
            Log.d(TAG, "Looking for: " + challengerName);
            queryChallenger(challengerName, win);
            finish();
        }
        //current user loses
        else {
            win = false;
            queryChallenger(challengerName, win);
            finish();
        }

    }

    /*Get the challenger*/
    public void queryChallenger(String challengerUsername, final boolean currentUserWin){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", challengerUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Successful Query");
                    /*Check for the length of the array. There should only be one result if there was indeed a match.*/
                    if (users.size() == 1) {
                        challenger = users.get(0);
                        Log.d(TAG, "Challenger: " + challenger.getUsername());
                        if (currentUserWin) {
                            Log.d(TAG, "Current user wins");
                            //Toast.makeText(getApplicationContext(), "YOU WIN", Toast.LENGTH_SHORT).show();
                            assignWinnerLoser(mCurrentUser, challenger);
                            sendPushNotification(challenger, mCurrentUser, getResources().getString(R.string.send_loss_push));
                            Toast.makeText(getApplicationContext(),"You won! Congratulations!!",Toast.LENGTH_LONG).show();

                        } else if (!currentUserWin) {
                            Log.d(TAG, "Challenger user wins");
                            //Toast.makeText(getApplicationContext(), "YOU LOSE", Toast.LENGTH_SHORT).show();
                            assignWinnerLoser(challenger, mCurrentUser);
                            sendPushNotification(challenger, mCurrentUser, getResources().getString(R.string.send_win_push));
                            Toast.makeText(getApplicationContext(),"You lost! You'll have to train harder next time!",Toast.LENGTH_LONG).show();
                        }

                        /*----------------------------*/
                    }
                    /*If there was no username returned*/
                    else if (users.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Challenger not found", Toast.LENGTH_SHORT).show();
                    }
                }
                // Something went wrong with the query
                else {
                    Log.d(TAG, "Something is wrong with the query");
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "Something went wrong with your query :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //------------------------------

    //Access the information of the challenge, also declares the challenge complete
    public void assignWinnerLoser(final ParseUser winner, final ParseUser loser){
        ParseQuery query = new ParseQuery("Challenge");
        Log.d("RESPONSERUN", "ID: " + challengeID);
        query.whereEqualTo("objectId", challengeID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Integer chalSize = objects.size();
                    Log.d("RESPONSERUN", "Number chals returned: " + chalSize.toString());
                    if (objects.size() == 0) {
                        //Display a message saying you have no challenges at this time
                        Log.d("RESPONSERUN", "No challenges with this ID");
                    } else {
                        chal = objects.get(0);
                        Log.d("RESPONSERUN", chal.getObjectId());
                        ParseObject challengeWOD = ParseObject.createWithoutData("Challenge", chal.getObjectId());
                        challengeWOD.put("isComplete", true);
                        challengeWOD.put("winner", winner);
                        challengeWOD.put("loser", loser);
                        Log.d("RESPONSERUN", "Winner: " + winner.getUsername());
                        Log.d("RESPONSERUN", "Loser: " + loser.getUsername());

                        challengeWOD.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e !=null){
                                    Log.d(TAG,e.getMessage());
                                }
                                else {
                                    Log.d(TAG,"Successfully saved challenge");
                                }
                            }
                        });

                    }

                } else {
                    /*There must have been a problem*/
                    Log.d("RESPONSERUN", e.toString());
                }

                //-----------SAVE-------------

                //----------------------------
            }
        });
    }

    //-------------------------------------

    //Separate method to save to backend
    public void saveToParse(final ParseUser user){
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("EDITFRIENDS", user.getUsername() + " was saved successfully");
                } else if (e != null) {
                    Log.d("EDITFRIENDS", e.getMessage());
                }
            }
        });
    }
    /*-----------------*/
    /*Sends out a push to the challenger that they have won or lost*/
    protected void sendPushNotification(ParseUser challenger, ParseUser current, String message){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, challenger.getObjectId());
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(current.getUsername() + message);
        push.sendInBackground();

    }

    //---STOP CHALLENGE------------------
    public void forfeitChallenge(){
        /*Pause the run and update the icon to reflect it*/
        timeSwapBuffer += timeInMillis;
        timeHandler.removeCallbacks(updateTimerThread);
        menu.findItem(R.id.action_begin_run).setIcon(R.drawable.ic_play_arrow_white_24dp);
        isRunning = false;

        AlertDialog.Builder finishedDialog = new AlertDialog.Builder(ChallengeResponseRunActivity.this);
        finishedDialog.setTitle("Forfeit Challenge?");
        finishedDialog.setMessage("This will end the challenge and will count as a lose. Are you sure you want to quit?");
        finishedDialog.setPositiveButton("Forfeit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Complete and record as a loss
                win = false;
                queryChallenger(challengerName, win);
                Toast.makeText(getApplicationContext(),"You have forfeited the challenge.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        finishedDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTime = SystemClock.uptimeMillis();
                timeHandler.postDelayed(updateTimerThread, 0);
                menu.findItem(R.id.action_begin_run).setIcon(R.drawable.ic_pause_white_24dp);
                isRunning = true;
            }
        });
        finishedDialog.show();
    }
    //-----------------------------------


}
