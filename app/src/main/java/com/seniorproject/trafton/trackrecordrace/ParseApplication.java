package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 12/6/15.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Challenge.class);
        Parse.initialize(this, "poYJezuhjoh6MjgbgXSU8ZupYS9wYOAqqvowwxS0", "Ha595bt9sV3ylPodMaVBd0q50aipe77FctdFJwCj");

        //create Parse installation
        //PushService.
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}