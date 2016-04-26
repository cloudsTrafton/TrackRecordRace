package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 3/12/16.
 * Creates new rows in Challenge object in Parse database. Also makes for easy access and modularity
 */

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Challenge")
public class Challenge extends ParseObject{
    private ParseUser challenger;
    private double chalTime;
    private ParseUser contender;
    private double conTime;
    private double distance;
    private Date createdOn;
    private String challengeID;
    private boolean isComplete;

    private ParseUser winner;
    private ParseUser loser;

    //Requires a default constructor
    public Challenge(){

    }

    /*Constructor used to create a new challenge in the database*/
    public Challenge(ParseUser chal, double chal_time, double dist, ParseUser con, double con_time){
        challenger = chal;
        this.put("Challenger", challenger);
        chalTime = chal_time;
        this.put("ChallengerTime",chalTime);
        distance = dist;
        this.put("Distance", distance);
        contender = con;
        this.put("Contender",contender);
        conTime = con_time;
        this.put("ContenderTime", conTime);
        this.put("isComplete", false);

        ParseACL permissions = new ParseACL();
        permissions.setPublicWriteAccess(true);
        permissions.setPublicReadAccess(true);
        this.setACL(permissions);
    }

    /*Constructor used to load a new challenge into the application*/
    public Challenge(ParseUser chal, double chal_time, ParseUser con, double dist, Date d){
        challenger = chal;
        chalTime = chal_time;
        contender = con;
        distance = dist;
        createdOn = d;
        //challengeID = this.getObjectId();

    }

    public void setDone(){
        this.put("isComplete", true);
    }


    //Getting winner and loser
   /* public void setResult(ParseUser winner, ParseUser loser){
        this.put("winner", winner);
        Log.d("CHALLENGE", "winner is: " + winner.getUsername());
        this.put("loser", loser);
        Log.d("CHALLENGE", "loser is: " + loser.getUsername());
    }

    //set the completed flag
    public void setFinished(){
        this.put("isComplete", true);
    } */

    /*Getters and setters*/
    public ParseUser getChallenger() {
        return challenger;
    }

    public void setChallenger(ParseUser challenger) {
        this.challenger = challenger;
        //this.put("Challenger", challenger);
    }

    public double getChalTime() {
        return chalTime;
    }

    public void setChalTime(float chalTime) {
        //this.put("ChallengerTime",chalTime);
        this.chalTime = chalTime;
    }

    public String getID(){
        return challengeID;
    }

    public ParseUser getContender() {
        return contender;
    }

    public void setContender(ParseUser contender) {
        this.contender = contender;
    }

    public double getConTime() {
        return conTime;
    }

    public void setConTime(double conTime) {
        this.conTime = conTime;;
    }

    public double getDistance(){
        return distance;
    }

    public void setDistance(double dist){
        this.distance = dist;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    //set the challenge
    public void setChallengeID(String id){
        challengeID = id;
    }


}
