package com.seniorproject.trafton.trackrecordrace;

import com.parse.ParseClassName;
import com.parse.*;
/**
 * Created by Claudia on 3/29/16.
 * Registered class for storing runs in Parse
 */

@ParseClassName("Run")
public class Run extends ParseObject{
    ParseUser owner;
    double distance;
    double time;
    double cals;

    //maybe add avg speed?

    /*Required default constructor*/
    public Run(){

    }

    /*Actual usable constructor.
    * Time is uploaded in seconds. When viewed, will be parsed to minutes.
    * Also sends data to backend and creates a new row*/
    public Run(ParseUser o, double d, double t, double c){
        owner = o;
        this.put("Owner",owner);
        distance = d;
        this.put("Distance",distance);
        time = t;
        this.put("Time",time);
        cals = c;
    }

    /*Getters*/
    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public double getCals() {
        return cals;
    }

    /*Setters*/
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setCals(double cals) {
        this.cals = cals;
    }


}
