package com.seniorproject.trafton.trackrecordrace;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Claudia on 4/11/16.
 */
public class RunListAdapter extends RecyclerView.Adapter<RunListAdapter.ViewHolder> {
    protected List<Run> itemsData;
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");
    String formattedDate;
    public RunListAdapter(List<Run> itemsData) {
        this.itemsData = itemsData;
    }
    protected DecimalFormat df = new DecimalFormat("#.##");
    protected TimeFormatter mTimeFormatter = new TimeFormatter();

    //View Holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTitleText;
        public TextView distanceText;
        public TextView timeText;
        public TextView paceText;
        public TextView caloriesText;

        public ClipData.Item currentItem;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            dateTitleText = (TextView) itemLayoutView.findViewById(R.id.my_run_date_title);
            distanceText = (TextView) itemLayoutView.findViewById(R.id.my_run_distance);
            timeText = (TextView) itemLayoutView.findViewById(R.id.my_run_time);
            paceText =(TextView) itemLayoutView.findViewById(R.id.my_pace_text);
            caloriesText = (TextView) itemLayoutView.findViewById(R.id.my_cals_text);


        }

    }
        /*------------------------------------------------*/

    // Create new views (invoked by the layout manager)
    @Override
    public RunListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_runs_card_layout, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        formattedDate = formatter.format(itemsData.get(position).getCreatedOn());
        String formattedTime = mTimeFormatter.getFormattedString(itemsData.get(position).getTime());

        Double pace = itemsData.get(position).getTime()/itemsData.get(position).getDistance();
        String formattedPace = mTimeFormatter.getFormattedString(pace);

        viewHolder.dateTitleText.setText(" " + formattedDate);
        viewHolder.distanceText.setText(df.format(itemsData.get(position).getDistance()) + " miles");
        viewHolder.timeText.setText(formattedTime + " mins");
        viewHolder.paceText.setText(formattedPace + " mins/mile"); //TODO implement pacing
        viewHolder.caloriesText.setText(df.format(itemsData.get(position).getCalories()) + " calories burned");

    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return itemsData == null ? 0 : itemsData.size();
        Log.d("ADAPTER", itemsData.toString());
        return itemsData.size();
    }
}
