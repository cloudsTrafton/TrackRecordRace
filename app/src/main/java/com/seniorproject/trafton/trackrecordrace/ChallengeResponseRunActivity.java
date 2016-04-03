package com.seniorproject.trafton.trackrecordrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ChallengeResponseRunActivity extends AppCompatActivity {

    protected String challengeDistance;
    protected String challengeTime;
    protected TextView testDistance;
    protected TextView testTime;

    /*In addition to setting up the activity, it also must get the information from the intent sent by the RecyclerView*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_response_run);
        Intent intent = getIntent();
        challengeDistance = intent.getStringExtra(ParseConstants.BUNDLE_DISTANCE);
        challengeTime = intent.getStringExtra(ParseConstants.BUNDLE_TIME);

        testDistance = (TextView) findViewById(R.id.test_distance);
        testDistance.setText(challengeDistance);
        testTime = (TextView) findViewById(R.id.test_time);
        testTime.setText(challengeTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge_response_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
