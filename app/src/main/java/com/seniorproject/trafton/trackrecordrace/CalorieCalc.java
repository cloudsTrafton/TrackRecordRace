package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 4/3/16.
 */
public class CalorieCalc {
    Double weight;
    //float speed;
    Double VO2;
    Double cals;

    public CalorieCalc(Double wt){
        weight = wt;

    }

    /*Calculates estimate of oxygen use efficiency for the body
    * Calculates slight differences between running and walking as read in from sensors*/
    public Double getVO2(float speed){
        if (speed < MiHtoMperMin(3.7)){
            VO2 = 0.1 * speed + 3.5;
        }
        else {
            VO2 = 0.2 * speed + 3.5;
        }
        return VO2;
    }

    /*Calculates calories based on VO2 estimate. also gives calories per second
    * since the location calls are rough*/
    public Double getCaloriesVO2(float speed){
        cals = (5 * lbsToKgs(weight) * getVO2(speed)/1000)/60;
        //Kcal/Min ~= 5 calories * massKg * VO2 / 1000
        return cals;
    }

    /*Convert Mi-h^-1 to meters per minute (i.e. useable value)*/
    public double MiHtoMperMin(double num){
        return num * 26.8;
    }

    public double lbsToKgs(double w){
        return w * 0.453592;
    }

}
