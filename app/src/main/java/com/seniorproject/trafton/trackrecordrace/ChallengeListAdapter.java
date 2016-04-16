package com.seniorproject.trafton.trackrecordrace;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Claudia on 3/23/16.
 * RecyclerView Adapter for displaying Challenges in the Challenges Tab of the main interface
 */
public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ViewHolder> {
    //itemsData contains each challenge
    protected List<Challenge> itemsData;
    public ChallengeListAdapter(List<Challenge> itemsData) {
        this.itemsData = itemsData;
    }

    /*---------------------------- */
    // inner class to hold a reference to each item of RecyclerView
    //Context is needed for intents to accept or decline a challenge
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleText;
        public TextView distanceText;
        public TextView theirTimeText;
        public Button goChallengeButton;
        public Button deleteChallengeButton;

        public ClipData.Item currentItem;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            titleText = (TextView) itemLayoutView.findViewById(R.id.title_text);
            distanceText = (TextView) itemLayoutView.findViewById(R.id.distance_text);
            theirTimeText = (TextView) itemLayoutView.findViewById(R.id.their_time_text);
            goChallengeButton = (Button) itemLayoutView.findViewById(R.id.go_challenge_button);
            deleteChallengeButton = (Button) itemLayoutView.findViewById(R.id.delete_challenge_button);


        }

    }
    /*------------------------------------------------*/

    // Create new views (invoked by the layout manager)
    @Override
    public ChallengeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.challenge_card_layout, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TimeFormatter mTimeFormatter = new TimeFormatter();
        DecimalFormat df = new DecimalFormat("#.##");

        viewHolder.titleText.setText(" Challenge sent by " + itemsData.get(position).getChallenger().getUsername() + " on " + itemsData.get(position).getCreatedOn());
        viewHolder.distanceText.setText("Distance: " +  df.format(mTimeFormatter.toMiles(itemsData.get(position).getDistance())) + " miles");
        viewHolder.theirTimeText.setText("Their Time: " + mTimeFormatter.getFormattedString(itemsData.get(position).getChalTime()) + " mins");

        /*Get the metrics for clicking the buttons!
        * All data is sent in meters and seconds for consistency and is then converted for aesthetics*/
        final Double distIntent = itemsData.get(position).getDistance();
        Log.d("CHALLENGEADAPTER", "sending: " + distIntent);
        final Double timeIntent = itemsData.get(position).getChalTime();
        Log.d("CHALLENGEADAPTER", "sending: " + timeIntent);
        final String challengerName = itemsData.get(position).getChallenger().getUsername();
        Log.d("CHALLENGEADAPTER", "sending: " + challengerName);


        /*Checks for a button click*/
        viewHolder.goChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHALLENGEADAPTER", "send button was clicked!");
                //TODO:When the button is clicked, sent a bundle of data in an intent
                Intent intent = new Intent(v.getContext(), ChallengeResponseRunActivity.class);
                intent.putExtra(ParseConstants.BUNDLE_DISTANCE, distIntent.toString());
                intent.putExtra(ParseConstants.BUNDLE_TIME, timeIntent.toString());
                intent.putExtra(ParseConstants.BUNDLE_CHALLENGER,challengerName);
                v.getContext().startActivity(intent);
            }
        });

            /*If the user wishes to delete the challenge, they will get a loss*/
        viewHolder.deleteChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:When the button is clicked, remove the challenge from Parse, add a loss to the user
            }
        });

    }




    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return itemsData == null ? 0 : itemsData.size();
        Log.d("ADAPTER", itemsData.toString());
        return itemsData.size();
    }
}
