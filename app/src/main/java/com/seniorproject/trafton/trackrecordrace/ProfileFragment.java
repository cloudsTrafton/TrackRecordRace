package com.seniorproject.trafton.trackrecordrace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }

    //Get the user data
    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    String mUsername = mCurrentUser.getUsername().toString();

    TextView mWinsView;
    TextView mLossesView;

    List<Double> mDistances = new ArrayList<Double>();
    List<Double> mPaces = new ArrayList<Double>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        /*Query for each run to get specific numbers*/
        ParseQuery innerQuery = new ParseQuery("_User");
        innerQuery.whereEqualTo("username", mCurrentUser.getUsername());
        ParseQuery query = new ParseQuery("Run");
        query.whereMatchesQuery(ParseConstants.KEY_SELF_OWNER, innerQuery);
        query.include("CreatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Integer numRuns = objects.size();
                    Log.d("PROFILEFRAGMENT", "Number of runs returned: " + numRuns.toString());

                    //Get lists of different running attributes and sort them

                    //Set the text views
                    TextView mRunsView = (TextView) rootView.findViewById(R.id.runs_user);
                    mRunsView.setText("Number of Runs: " + numRuns);

                    //get distances and sort
                    for(int i = 0; i < numRuns; i++){
                        mDistances.add(objects.get(i).getDouble("Distance"));
                        mPaces.add(objects.get(i).getDouble("Distance")-objects.get(i).getDouble("Time"));
                    }
                    Collections.sort(mDistances);
                    Collections.sort(mPaces);

                    TextView mRunsLongest = (TextView) rootView.findViewById(R.id.runs_distance);
                    mRunsLongest.setText("Longest Distance: " + mDistances.get(mDistances.size()-1) + " miles");

                    TextView mRunsBestPace = (TextView) rootView.findViewById(R.id.runs_time);
                    mRunsBestPace.setText("Best Pace: " + mPaces.get(mPaces.size() - 1) + " miles/minute");

                    TextView mChallengeWins = (TextView) rootView.findViewById(R.id.challenge_wins);
                    mChallengeWins.setText("Wins: " + mCurrentUser.getInt("wins"));

                    if (objects.size() == 0) {
                        //Display a message saying you have no challenges at this time
                    }
                }
            }
        });
        //-----------end Query----------

        //Begin Query for Challenges
        ParseQuery innerQueryChal = new ParseQuery("_User");
        innerQueryChal.whereEqualTo("username",mCurrentUser.getUsername());
        ParseQuery queryChal = new ParseQuery("Challenge");
        queryChal.whereMatchesQuery(ParseConstants.KEY_SELF_CONTENDER, innerQueryChal);
        queryChal.include("Challenger");
        queryChal.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Integer chalSize = objects.size();
                    Log.d("PROFILE", "Number returned: " + chalSize.toString());
                    if (objects.size() == 0) {
                        //Display a message saying you have no challenges at this time
                    } else {
                        /*Parse each object into a readable format*/
                        for (int i = 0; i < objects.size(); i++) {

                        }

                    }

                } else {
                    /*There must have been a problem*/
                    Log.d("PROFILE", e.toString());
                }
            }
        });

        //-----------end Query----------

        //Set the labels
        TextView myRunsLabel = (TextView) rootView.findViewById(R.id.my_stuff_label);
        myRunsLabel.setText(mUsername + "'s" + " Run Stats");

        TextView myChallengesLabel = (TextView) rootView.findViewById(R.id.challenge_stuff_label);
        myChallengesLabel.setText(mUsername + "'s" + " Challenge Stats");


        return rootView;
    }

}
