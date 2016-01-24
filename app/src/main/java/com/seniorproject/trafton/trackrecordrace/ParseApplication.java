package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 12/6/15.
 */
import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "poYJezuhjoh6MjgbgXSU8ZupYS9wYOAqqvowwxS0", "Ha595bt9sV3ylPodMaVBd0q50aipe77FctdFJwCj");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}