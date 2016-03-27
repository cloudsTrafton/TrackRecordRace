package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 3/12/16.
 * Creates new rows in Challenge object in Parse database. Also makes for easy access and modularity
 */
import com.parse.*;

@ParseClassName("Challenge")
public class Challenge extends ParseObject{
    private ParseUser challenger;
    private float chalTime;
    private ParseUser contender;
    private float conTime;
    private float distance;

    //Requires a default constructor
    public Challenge(){

    }

    public Challenge(ParseUser chal, float chal_time, ParseUser con, float con_time){
        challenger = chal;
        this.put("Challenger", challenger);
        chalTime = chal_time;
        this.put("ChallengerTime",chalTime);
        contender = con;
        this.put("Contender",contender);
        conTime = con_time;
        this.put("ContenderTime", conTime);
        this.put("Distance", 00.00);

    }

    /*Getters and setters*/
    public ParseUser getChallenger() {
        return challenger;
    }

    public void setChallenger(ParseUser challenger) {
        this.challenger = challenger;
        //this.put("Challenger", challenger);
    }

    public float getChalTime() {
        return chalTime;
    }

    public void setChalTime(float chalTime) {
        //this.put("ChallengerTime",chalTime);
        this.chalTime = chalTime;
    }

    public ParseUser getContender() {
        return contender;
    }

    public void setContender(ParseUser contender) {
        this.contender = contender;
        //this.put("Contender",contender);
    }

    public float getConTime() {
        return conTime;
    }

    public void setConTime(float conTime) {
        this.conTime = conTime;
        //this.put("ContenderTime", conTime);
    }

    public float getDistance(){
        return distance;
    }

    public void setDistance(float dist){
        this.distance = dist;
    }


}
