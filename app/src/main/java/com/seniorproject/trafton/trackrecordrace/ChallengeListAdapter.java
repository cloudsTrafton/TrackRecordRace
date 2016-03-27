package com.seniorproject.trafton.trackrecordrace;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        viewHolder.titleText.setText("Challenge sent to " + itemsData.get(position).getContender().getUsername() + " on " + itemsData.get(position).getCreatedAt().toString());
        //viewHolder.distanceText.setText(itemsData[position].getDistance() + "");
        viewHolder.myTimeText.setText("Your Time: " + itemsData.get(position).getChalTime() + " secs");
        if (itemsData.get(position).getConTime() == 0){
            viewHolder.myTimeText.setText("Their Time: Pending");
        }
        else {
            viewHolder.myTimeText.setText("Their Time: " + itemsData.get(position).getConTime());
        }



    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleText;
        public TextView distanceText;
        public TextView myTimeText;
        public TextView theirTimeText;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            titleText = (TextView) itemLayoutView.findViewById(R.id.title_text);
            distanceText = (TextView) itemLayoutView.findViewById(R.id.distance_text);
            myTimeText = (TextView) itemLayoutView.findViewById(R.id.my_time_text);
            theirTimeText = (TextView) itemLayoutView.findViewById(R.id.their_time_text);

        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return itemsData == null ? 0 : itemsData.size();
        Log.d("ADAPTER", itemsData.toString());
        return itemsData.size();
    }
}
