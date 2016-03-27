package com.seniorproject.trafton.trackrecordrace;

/**
 * Created by Claudia on 3/22/16.
 */
public class ChallengeData {
    protected String challenger;
    protected String contender;
    protected String challengerTime;
    protected String contenderTime;
    protected String distance;
    protected String date;

    public ChallengeData(String challenger, String contender, String challengerTime, String contenderTime, String distance, String date) {
        this.challenger = challenger;
        this.contender = contender;
        this.challengerTime = challengerTime;
        this.contenderTime = contenderTime;
        this.distance = distance;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContenderTime() {
        return contenderTime;
    }

    public void setContenderTime(String contenderTime) {
        this.contenderTime = contenderTime;
    }
}
