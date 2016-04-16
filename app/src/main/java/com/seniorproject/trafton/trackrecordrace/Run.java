package com.seniorproject.trafton.trackrecordrace;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Claudia on 3/29/16.
 * Registered class for storing runs in Parse
 */

@ParseClassName("Run")
public class Run extends ParseObject{
    ParseUser owner;
    Double distance;
    Double time;
    Double calories;

    private Date createdOn;

    /*Required default constructor*/
    public Run(){

    }

    /*Actual usable constructor.
    * Time is uploaded in seconds. When viewed, will be parsed to minutes.
    * Also sends data to backend and creates a new row*/
    public Run(ParseUser o, Double d, Double t, Double c){
        owner = o;
        this.put("Owner",owner);
        distance = d;
        this.put("Distance",distance);
        time = t;
        this.put("Time",time);
        calories = c;
        this.put("Calories",calories);
    }

    //Load Constructor
    public Run(ParseUser o, Double d, Double t, Double c, Date date){
        owner = o;
        distance = d;
        time = t;
        calories = c;
        createdOn = date;

    }

    /*Getters*/
    public Double getDistance() {
        return distance;
    }

    public Double getTime() {
        return time;
    }

    public Double getCalories() {
        return calories;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    /*Setters*/
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public void setCalories(Double cals) {
        this.calories = cals;
    }



}
