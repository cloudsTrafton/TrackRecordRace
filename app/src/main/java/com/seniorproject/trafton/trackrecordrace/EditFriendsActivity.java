package com.seniorproject.trafton.trackrecordrace;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {
    private RecyclerView mFriendsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    protected List<ParseUser> mUsers;
    public String[] mUsernames;
    //String[] dummyNames = {"Luke", "Claudia", "Dan"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friends_recycle_view);

        Toolbar editFriendsToolbar= (Toolbar) findViewById(R.id.toolbar_edit_friends);
        editFriendsToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(editFriendsToolbar);



        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mFriendsRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new FriendsListAdapter(mUsernames);
        getAllUsers();


    }

    @Override
    protected void onResume(){
        super.onResume();
        getAllUsers();

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
                Intent friendsIntent = new Intent(
                        EditFriendsActivity.this,
                        SearchFriendActivity.class);
                startActivity(friendsIntent);
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

    public void getAllUsers(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending("username");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null){
                    //success
                    mUsers = users;
                    mUsernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user: mUsers){
                        mUsernames[i] = user.getUsername();
                        i++;
                    }

                    mAdapter = new FriendsListAdapter(mUsernames);
                    mFriendsRecyclerView.setAdapter(mAdapter);
                }

                else {
                    Log.e("EDITFRIENDS", e.getMessage());
                    //create an error alert builder
                    //TODO
                    //create alert dialog error
                }
            }
        });
    }


}
