package com.seniorproject.trafton.trackrecordrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

//Main activity handles data from parse.
//Also handles fragments for main navigation
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Parse.enableLocalDatastore(this);
 /*       Parse.initialize(this, "poYJezuhjoh6MjgbgXSU8ZupYS9wYOAqqvowwxS0", "Ha595bt9sV3ylPodMaVBd0q50aipe77FctdFJwCj");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true); */

        // Determine whether the current user is an anonymous user
        // If user is anonymous, send the user to LoginSignupActivity.class
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent intent = new Intent(MainActivity.this,
                    LoginSignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Get current user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                // Send logged in users to the main menu
                Intent intent = new Intent(MainActivity.this, MainTabsActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Send user to LoginSignupActivity.class
                Intent intent = new Intent(MainActivity.this,
                        LoginSignupActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

}