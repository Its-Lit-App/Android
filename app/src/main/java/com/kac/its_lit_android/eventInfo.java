package com.kac.its_lit_android;


import java.util.Date;

public class eventInfo {
    private int totalVotes;
    private int scoreVotes;
    private String title;
    private String content;
    private Date creationTime;

    public eventInfo(String t, String c, Date date){
        totalVotes = 0;
        scoreVotes = 0;
        title = t;
        content = c;
        creationTime = date;
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

    public Date returnDate(){
        return creationTime;
    }
}
