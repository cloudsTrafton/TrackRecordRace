package com.seniorproject.trafton.trackrecordrace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Date;
import java.util.List;


public class ChallengeFragment extends Fragment {
    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "CHALLENGEFRAGMENT";
    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    List<Challenge> mChallenges = new ArrayList<Challenge>();

    private RecyclerView mChallengeList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    protected TextView loadingText;

    /*Queries*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_challenge, container, false);

        /*Create RecyclerViews*/
        loadingText = (TextView) rootView.findViewById(R.id.challenges_text_display);
        mChallengeList = (RecyclerView) rootView.findViewById(R.id.SentChallengeCardList);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mChallenges.clear();
        getChallenges();

    }


    /*Method that returns all challenges sent to the current user.
    * Looks in Parse for when currentUser is the Contender.
    * The challenges they have sent will be viewable in "statistics"*/
    public void getChallenges(){
        ParseQuery innerQuery = new ParseQuery("_User");
        innerQuery.whereEqualTo("username", mCurrentUser.getUsername());
        ParseQuery query = new ParseQuery("Challenge");
        query.whereMatchesQuery(ParseConstants.KEY_SELF_CONTENDER, innerQuery);
        query.include("Challenger");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Integer test = objects.size();
                    Log.d(TAG, "Number returned: " + test.toString());
                    if (objects.size() == 0) {
                        loadingText.setText("You have no challenges at this time.");
                    } else {
                        /*Parse each object into a readable format*/
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject item = objects.get(i);
                            ParseUser challenger = (ParseUser) item.get("Challenger");
                            ParseUser contender = (ParseUser) item.get("Contender");
                            double chalTime = castInt((Number) item.get("ChallengerTime"));
                            double distance = castInt((Number) item.get("Distance"));
                            boolean isComplete = (boolean) item.get("isComplete");
                            String ID = item.getObjectId();
                            Log.d(TAG,"Challenge ID: " + ID);
                            Date createdAt = (Date) item.getCreatedAt();
                            Log.d(TAG, "Challenger: " + challenger.getUsername());
                            Challenge temp = new Challenge(challenger, chalTime, contender, distance, createdAt);
                            temp.setChallengeID(ID);
                            if (!isComplete) {
                                mChallenges.add(temp);
                            }
                        }

                    }

                    mAdapter = new ChallengeListAdapter(mChallenges, getContext());
                    mChallengeList.setLayoutManager(mLayoutManager);
                    mChallengeList.setAdapter(mAdapter);
                    Integer size = mChallenges.size();
                    Log.d(TAG, size.toString());
                } else {
                    /*There must have been a problem*/
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

    /*In the case that Parse passes whole numbers, use division to cast these to doubles*/
    public double castInt(Number j){
        double num = j.doubleValue();
        return num;
    }


}
