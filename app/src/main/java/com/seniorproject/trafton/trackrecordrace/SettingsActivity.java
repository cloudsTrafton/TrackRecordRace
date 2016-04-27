package com.seniorproject.trafton.trackrecordrace;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsActivity extends AppCompatActivity {

    ParseUser mCurrentUser = ParseUser.getCurrentUser();
    Context mContext = this;

    Button mSaveButton;
    Button mCancelButton;

    EditText mWeightChange;
    EditText mIndividualRunsDisplay;


    String mWeightText;
    Integer mWeightNum;
    Integer mIndividualRunsNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar settingsToolbar= (Toolbar) findViewById(R.id.toolbar_activity_settings);
        settingsToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(settingsToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mWeightChange = (EditText) findViewById(R.id.setting_change_weight);
        mIndividualRunsDisplay = (EditText) findViewById(R.id.setting_change_individual_runs);

        mSaveButton = (Button) findViewById(R.id.save_settings_button);
        mCancelButton = (Button) findViewById(R.id.cancel_settings_button);

        /*Save the settings once the button has been clicked*/
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUsed(mWeightChange.getText())){
                    mWeightText = mWeightChange.getText().toString();
                    Log.d("SETTINGS", "Weight: " + mWeightText);
                    mWeightNum = Integer.parseInt(mWeightText);
                    mCurrentUser.put("weight", mWeightNum);
                }

                if(isUsed(mIndividualRunsDisplay.getText())){
                    mIndividualRunsNum = Integer.parseInt(mIndividualRunsDisplay.getText().toString());
                    Log.d("SETTINGS", "display " + mIndividualRunsNum);
                    mCurrentUser.put("displayNum", mIndividualRunsNum);
                }

                saveToParse(mCurrentUser);
                Toast.makeText(mContext, "Settings successfully saved!\n Your update will be displayed shortly.",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void saveToParse(ParseUser user){
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("SETTINGS", e.getMessage());

                }
            }
        });
    }

    public boolean isUsed(Editable viewText){
        if(viewText.toString().equals("")){
            return false;
        }
        else {
            return true;
        }

    }
}
