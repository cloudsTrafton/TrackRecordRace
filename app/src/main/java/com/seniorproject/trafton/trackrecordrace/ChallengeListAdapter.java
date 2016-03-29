package com.seniorproject.trafton.trackrecordrace;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Claudia on 3/23/16.
 * RecyclerView Adapter for displaying Challenges in the Challenges Tab of the main interface
 */
public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ViewHolder> {
    //itemsData contains each challenge
    private List<Challenge> itemsData;

    public ChallengeListAdapter(List<Challenge> itemsData) {
        this.itemsData = itemsData;
    }

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

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        //viewHolder.titleText.setText("Challenge sent to " + itemsData.get(position).getContender().getUsername() + " on " + itemsData.get(position).getCreatedAt().toString());
        viewHolder.titleText.setText(" Challenge sent by " + itemsData.get(position).getChallenger().getUsername() + " on " + itemsData.get(position).getCreatedOn());
        viewHolder.distanceText.setText("Distance: " + itemsData.get(position).getDistance() + " miles");
        viewHolder.theirTimeText.setText("Their Time: " + itemsData.get(position).getChalTime() + " mins");
        /*if (itemsData.get(position).getConTime() == 0){
            viewHolder.myTimeText.setText("Their Time: Pending");
        }
        else {
            viewHolder.myTimeText.setText("Their Time: " + itemsData.get(position).getConTime());
        } */



    }

    // inner class to hold a reference to each item of RecyclerView
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

        //@Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.goChallengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:When the button is clicked, sent a bundle of data in an intent
                }
            });

        }
    }
    //End inner class for viewHolder


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return itemsData == null ? 0 : itemsData.size();
        Log.d("ADAPTER", itemsData.toString());
        return itemsData.size();
    }
}
