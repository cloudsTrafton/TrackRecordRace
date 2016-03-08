package com.seniorproject.trafton.trackrecordrace;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SendChallengeActivity extends ListActivity{

    public static final String TAG = "SendChalErr";
    protected List<ParseUser> mUsers;
    Button mSendChallengeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_challenge);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mSendChallengeButton = (Button)findViewById(R.id.send_challenge_button);
        mSendChallengeButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View arg0) {
                        //click button and send data to user
                    }
                }

        );
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Query backend for users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending("username");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null){
                    //success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user: mUsers){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            SendChallengeActivity.this,
                            android.R.layout.simple_list_item_checked, usernames
                    );
                    setListAdapter(adapter);


                }
                else {
                    Log.e(TAG, e.getMessage());
                    //create an error alert builder
                    //TODO
                    //create alert dialog error
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_challenge, menu);
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

    //Relate user to other user using ParseRelation
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
