package com.seniorproject.trafton.trackrecordrace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {

    final String TAG = "SEARCHFRIENDS";

    protected EditText mFriendSearch;
    protected Button mExecuteSearch;
    public List<ParseUser> mUsers;

    /*Users and relations*/
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        mFriendSearch = (EditText) findViewById(R.id.friend_search);
        mExecuteSearch = (Button) findViewById(R.id.execute_search_button);
        mExecuteSearch.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  /*Execute a Parse Query.
                                                  * Check if the input is there.*/
                                                  String input = mFriendSearch.getText().toString();
                                                  //If there is not input, do not execute the query. Throw error instead.
                                                  if (input == null) {
                                                      Toast.makeText(getApplicationContext(), "Please input the username you wish to search for", Toast.LENGTH_SHORT).show();
                                                  }
                                                  /*If there was valid input, query the backend*/
                                                  else {
                                                      searchUsers(input);

                                                  }
                                              }
                                          }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*Get updated backend data*/
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation= mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_friend, menu);
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
    /*
    Function to search for a user in Parse backend.
     */
    public String searchUsers(String input){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", input);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    /*Check for the length of the array. There should only be one result if there was indeed a match.*/
                    if (users.size() == 1) {
                        mUsers = users;
                        /*Successfully returned a single user*/
                        if (users.get(0).getUsername() == ParseUser.getCurrentUser().getUsername()) {
                            Toast.makeText(getApplicationContext(), "You cannot be friends with yourself on TrackRecordRace", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, users.get(0).getUsername());
                            /*Add the user as a friend with the relation*/
                            Toast.makeText(getApplicationContext(), users.get(0).getUsername() + " is now your friend!", Toast.LENGTH_SHORT).show();
                            mFriendsRelation.add(users.get(0));
                            mCurrentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Toast.makeText(getApplicationContext(), "Unknown error saving.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Toast.makeText(getApplicationContext(), users.get(0).getUsername() + " is now your friend!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    /*If there was no username returned*/
                    else if (users.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Could not find requested user. Please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unknown error.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Something went wrong.
                    Toast.makeText(getApplicationContext(), "Something went wrong with your query :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return input;
    }
}
