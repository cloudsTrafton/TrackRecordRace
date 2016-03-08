package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 3/5/16.
 */
public class FriendsData {
    private String friendName;

    public FriendsData(String name){

        this.friendName = name;
    }

    public void setName(String name){
        friendName = name;
    }

    public String getName(){
        return friendName;
    }
}
