package com.seniorproject.trafton.trackrecordrace;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {
    private RecyclerView mFriendsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    public String[] mUsernames;
    protected ParseUser mCurrentUser = ParseUser.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friends_recycle_view);

        Toolbar editFriendsToolbar= (Toolbar) findViewById(R.id.toolbar_edit_friends);
        editFriendsToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(editFriendsToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(this,R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        editFriendsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //Get the relation
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);



        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mFriendsRecyclerView.setLayoutManager(mLayoutManager);
        getFriends();


    }

    @Override
    protected void onResume(){
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        getFriends();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e("XXX","Menu created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friends:
                addFriend();
                return true;
            case R.id.action_delete_friends:
                if(mFriends.size() == 0){
                    Toast.makeText(getApplicationContext(), "You don't have friends to remove", Toast.LENGTH_SHORT).show();
                }
                else {
                    removeFriend();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Search for a specific User
     * @param searchText
     */
    public void searchUser(String searchText) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", searchText);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });}

//Load my list of friends
    public void getFriends(){
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    //If no friends, create a message saying no friends
                    if (friends.size() == 0) {
                        mUsernames = new String[1];
                        mUsernames[0] = "No Friends to display :(.";
                    }
                    //If the user has friends, populate the list
                    else {
                        mFriends = friends;
                        mUsernames = new String[mFriends.size()];
                        int i = 0;
                        for (ParseUser friend : mFriends) {
                            mUsernames[i] = friend.getUsername();
                            i++;
                        }
                    }
                    mAdapter = new FriendsListAdapter(mUsernames);
                    mFriendsRecyclerView.setAdapter(mAdapter);

                } else {
                    Log.e("EDITFRIENDS", e.getMessage());
                }

            }
        });
    }

    //Separate method to save to backend
    public void saveToParse(){
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("EDITFRIENDS", e.getMessage());
                }
            }
        });
    }

    /*Add a friend in an alert dialog as a opposed to an activity*/
    public void addFriend(){
        AlertDialog.Builder addFriendBuilder = new AlertDialog.Builder(this);
        addFriendBuilder.setTitle("Add a New Friend");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        addFriendBuilder.setView(input);
        addFriendBuilder.setMessage("Enter the username of the user want to add.");

        addFriendBuilder.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchUsers(input.getText().toString());
            }
        });

        //cancel
        addFriendBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        addFriendBuilder.show();
    }

    //-------------------------------------

    public void removeFriend(){
        /*Creates a dialog that allows a user to enter the name of the desired friend to delete*/
        //final String usernameToRemove = "";
        AlertDialog.Builder removeFriendBuilder = new AlertDialog.Builder(this);
        removeFriendBuilder.setTitle("Remove a Friend");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        removeFriendBuilder.setView(input);
        removeFriendBuilder.setMessage("Enter the username of the friend to remove.");

        //set the buttons
        removeFriendBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String friendToRemove = input.getText().toString();
                boolean wasRemoved = false;
                    for (int i = 0; i < mFriends.size(); i++) {
                        Log.d("EDITFRIENDS", "entered: " + friendToRemove + "\n friend: " + mFriends.get(i).getUsername());
                        if (mFriends.get(i).getUsername().equals(friendToRemove)) {
                            mFriendsRelation.remove(mFriends.get(i));
                            wasRemoved = true;
                            Toast.makeText(getApplicationContext(), friendToRemove + " successfully removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!wasRemoved) {
                        Toast.makeText(getApplicationContext(), "No friend removed, check your spelling.", Toast.LENGTH_SHORT).show();
                    } else {
                        saveToParse();
                    }
            }
        });

        //cancel
        removeFriendBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        removeFriendBuilder.show();

    }
    //-----------------------------
    public String searchUsers(String input){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", input);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    /*Check for the length of the array. There should only be one result if there was indeed a match.*/
                    if (users.size() == 1) {
                        /*Successfully returned a single user*/
                        if (users.get(0).getUsername() == ParseUser.getCurrentUser().getUsername()) {
                            Toast.makeText(getApplicationContext(), "You cannot be friends with yourself on TrackRecordRace", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("EDITFRIENDS", users.get(0).getUsername());
                            /*Add the user as a friend with the relation*/
                            //Toast.makeText(getApplicationContext(), users.get(0).getUsername() + " is now your friend!", Toast.LENGTH_SHORT).show();
                            mFriendsRelation.add(users.get(0));
                            saveToParse();
                            Toast.makeText(getApplicationContext(), users.get(0).getUsername() + " is now your friend! Your update will be displayed shortly.", Toast.LENGTH_SHORT).show();
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

    //-----------------------------
}
