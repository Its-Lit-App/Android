package com.kac.its_lit_android;


import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import java.util.Date;

public class eventInfo {
    public ParseObject PO = null;
    private String id;
    private int totalVotes;
    private int scoreVotes;
    private String title;
    private String content;
    private Date creationTime;
    private double lat, lon;

    public eventInfo(String t, String c, Date date, LatLng point){
        totalVotes = 0;
        scoreVotes = 0;
        title = t;
        content = c;
        creationTime = date;
        lat = point.latitude;
        lon = point.longitude;
    }

    public eventInfo(String t, String c, Date date, int totalVotes, int scoreVotes, LatLng point){
        this.totalVotes = totalVotes;
        this.scoreVotes = scoreVotes;
        title = t;
        content = c;
        creationTime = date;

        lat = point.latitude;
        lon = point.longitude;
    }

    public void upVote(){
        scoreVotes++;
        totalVotes++;
    }

    public void downVote(){
        scoreVotes--;
        totalVotes++;
    }

    public int getTotalVotes(){
        return totalVotes;
    }

    public int getScoreVotes(){
        return scoreVotes;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public Date getDate(){
        return creationTime;
    }

    public String getId() { return id; }
    public void setId(String s) { id = s; }
    public void setTotalVotes(int s) { totalVotes = s; }
    public void setScoreVotes(int s) { scoreVotes = s; }

    public double getLat() { return lat; }
    public double getLon() { return lon; }
}
