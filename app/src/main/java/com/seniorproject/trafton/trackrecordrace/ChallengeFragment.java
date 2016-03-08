package com.seniorproject.trafton.trackrecordrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ChallengeFragment extends Fragment {
    public ChallengeFragment() {
        // Required empty public constructor
    }

    Button mNewChallengeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_challenge, container, false);

        mNewChallengeButton = (Button) rootView.findViewById(R.id.new_challenge_button);
        mNewChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        SendChallengeActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}
