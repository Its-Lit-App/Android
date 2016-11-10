package com.kac.its_lit_android;

import com.parse.*;
import android.support.v4.app.FragmentActivity;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

public class DatabaseManager {
  public DatabaseManager(FragmentActivity fragmentActivity) {
    Parse.enableLocalDatastore(fragmentActivity);
    Parse.initialize(new Parse.Configuration.Builder(fragmentActivity).applicationId(DBID.appID).server(DBID.serverID).build());
    
  }
  //Save an event object to the database async
  public void saveToDatabase(eventInfo e) {
    ParseObject object = new ParseObject("Event");
    object.put("lat", e.getLat());
    object.put("lon", e.getLon());
    object.put("title", e.getTitle());
    object.put("totalVotes", e.getTotalVotes());
    object.put("scoreVotes", e.getScoreVotes());
    object.put("content", e.getContent());
    object.put("date", e.getDate());
    //object.put("city", e.city);
    object.saveInBackground();
  }
  //function to get an event object from the database
  public eventInfo getFromDatabase(int eventID) {
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
    final eventInfo eventFinal = null;
        query.whereEqualTo("eventID", eventID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject o : objects) {
                    //Print out the test object on the DB:
                    eventInfo event = new eventInfo(o.getString("title"), o.getString("content"), o.getDate("date"), o.getInt("totalVotes"), o.getInt("scoreVotes"), new LatLng(o.getDouble("lat"), o.getDouble("lon")));
                    /*event.eventID = o.getInt("eventID");
                    event.color = o.getInt("color");
                    event.totalVotes = o.getInt("totalVotes");
                    event.score = o.getInt("score");
                    event.promotionStatus = o.getInt("promotionStatus");
                    event.date = o.getInt("date");
                    event.city = o.getInt("city");*/
                    System.out.println("Found object: " + o.getObjectId());
                    //return event;
                }
            }
        });
      return null;
  }
  //Void to update an event object async
  public void updateDatabase(eventInfo e) {
    
  }
}