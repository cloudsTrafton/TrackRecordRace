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

public class MyRunsFragment extends Fragment {
    public MyRunsFragment() {
        // Required empty public constructor
    }
    public final String TAG = "RUNSFRAGMENT";
    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    List<Run> mRuns = new ArrayList<Run>();


    private RecyclerView mRunsList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    protected TextView fetchingRunsLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_runs, container, false);

        fetchingRunsLabel = (TextView) rootView.findViewById(R.id.fetching_runs_label);
        /*Create RecyclerViews*/
        mRunsList = (RecyclerView) rootView.findViewById(R.id.MyRunsCardList);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        getRuns();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mRuns.clear();
        getRuns();
    }

    //Get the runs from the backend
    public void getRuns(){
        ParseQuery innerQuery = new ParseQuery("_User");
        innerQuery.whereEqualTo("username", mCurrentUser.getUsername());
        ParseQuery query = new ParseQuery("Run");
        query.whereMatchesQuery(ParseConstants.KEY_SELF_OWNER, innerQuery);
        query.include("CreatedAt");
        query.setLimit(mCurrentUser.getInt("displayNum"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Integer test = objects.size();
                    Log.d(TAG, "Number returned: " + test.toString());
                    if (objects.size() == 0) {
                        fetchingRunsLabel.setText("You haven't completed any runs yet! \n Press the run icon to complete your first run!");
                    } else {
                        /*Parse each object into a readable format*/
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject item = objects.get(i);
                            ParseUser owner = (ParseUser) item.get("Owner");
                            double time = castInt((Number) item.get("Time"));
                            double distance = castInt((Number) item.get("Distance"));
                            double calories = castInt((Number) item.get("Calories"));
                            Date createdAt = item.getCreatedAt();
                            Run temp = new Run(owner, distance, time, calories, createdAt);
                            mRuns.add(temp);
                        }

                    }

                    mAdapter = new RunListAdapter(mRuns);
                    mRunsList.setLayoutManager(mLayoutManager);
                    mRunsList.setAdapter(mAdapter);
                    Integer size = mRuns.size();
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
