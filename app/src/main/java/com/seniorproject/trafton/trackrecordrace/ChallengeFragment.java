package com.seniorproject.trafton.trackrecordrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class ChallengeFragment extends Fragment {
    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "CHALLENGEFRAGMENT";
    Button mNewChallengeButton;
    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    List<Challenge> mChallenges;

    private RecyclerView mChallengeList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

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
        mChallengeList = (RecyclerView) rootView.findViewById(R.id.SentChallengeCardList);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        //RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.RecChallengeCardList);


        mNewChallengeButton = (Button) rootView.findViewById(R.id.new_challenge_button);
        mNewChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ChallengeRunActivty.class);
                startActivity(intent);
            }
        });
        getChallenges();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getChallenges();

    }

    /*Load ALL of the challenges!!! :D!!!*/
    public void getChallenges(){
        ParseQuery query = new ParseQuery("Challenge");
        //ParseObject cur = ParseObject.createWithoutData("_User",mCurrentUser.getObjectId());
        query.whereEqualTo(ParseConstants.KEY_SELF_CONTENDER, mCurrentUser);
        Log.d(TAG,mCurrentUser.toString());
        query.whereEqualTo(ParseConstants.KEY_SELF_CHALLENGER, mCurrentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List objects, ParseException e) {
                if (e == null){
                    Integer test = objects.size();
                    Log.d(TAG, "Number returned: " + test.toString());
                    if(objects.size() == 0){
                        //Display a message saying you have no challenges at this time
                    }
                    else {
                        mChallenges.addAll(objects);
                    }

                    mAdapter = new ChallengeListAdapter(mChallenges);
                    mChallengeList.setLayoutManager(mLayoutManager);
                    mChallengeList.setAdapter(mAdapter);
                    Integer size = mChallenges.size();
                    Log.d(TAG,size.toString());
                }
                else {
                    /*There must have been a problem*/
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

}
