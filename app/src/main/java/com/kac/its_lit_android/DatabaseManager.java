package com.kac.its_lit_android;

import com.parse.*;
import android.support.v4.app.FragmentActivity;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

public class DatabaseManager {
    public MapsActivity mapsActivity = null;
  public DatabaseManager(MapsActivity fragmentActivity) {
      this.mapsActivity = fragmentActivity;
    Parse.enableLocalDatastore((FragmentActivity)fragmentActivity);
    Parse.initialize(new Parse.Configuration.Builder((FragmentActivity)fragmentActivity).applicationId(DBID.appID).server(DBID.serverID).build());
    
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
  //function to get an event objects from the database based on 4 coordinates given
  public void loadBetweenCoordinates(double latStart, double latEnd, double lonStart, double lonEnd) {
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
    final eventInfo eventFinal = null;
        query.whereNotEqualTo("lat", -1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject o : objects) {
                    //Print out the test object on the DB:
                    eventInfo event = new eventInfo(o.getString("title"), o.getString("content"), o.getDate("date"), o.getInt("totalVotes"), o.getInt("scoreVotes"), new LatLng(o.getDouble("lat"), o.getDouble("lon")));
                    mapsActivity.createMarkerFromDB(event);
                    System.out.println("Found object: " + o.getObjectId());
                }
            }
        });
  }
  //Void to update an event object async
  public void updateDatabase(eventInfo e) {
    
  }
}