package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 4/11/16.
 */
public class TimeFormatter {

    public TimeFormatter(){

    }

    public Double secondsToMinutesDecimal(Double seconds){
        return seconds/60;
    }

    public String getFormattedString(Double seconds){
        Double mins = seconds / 60;
        mins = Math.floor(mins);
        int evenMins = mins.intValue();
        Double remainder = seconds % 60;
        int evenRemainder = remainder.intValue();
        return evenMins + ":" + evenRemainder;
    }

    /*Gets Distance in Miles*/
    public Double toMiles(Double val){
        val = val * 0.000621371192f;
        return val;
    }
}
