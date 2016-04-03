package com.seniorproject.trafton.trackrecordrace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }

    String mUsername;
    String mNumRuns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Get the user data
        ParseUser currentUser = ParseUser.getCurrentUser();
        String mUsername = currentUser.getUsername().toString();
        TextView myRunsLabel = (TextView) rootView.findViewById(R.id.my_stuff_label);
        TextView myChallengesLabel = (TextView) rootView.findViewById(R.id.challenge_stuff_label);
        myRunsLabel.setText(mUsername + "'s" + " Run Stats");
        myChallengesLabel.setText(mUsername + "'s" + " Challenge Stats");
        return rootView;
    }

}
