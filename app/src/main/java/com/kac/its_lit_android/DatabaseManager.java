package com.kac.its_lit_android;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.parse.*;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

public class DatabaseManager {
    public MapsActivity mapsActivity = null;
    public List<String> localVotes = new ArrayList<String>();
    private HashMap<String, eventInfo> eventInfoMap = new HashMap<String, eventInfo>();
  public DatabaseManager(MapsActivity fragmentActivity) {
      this.mapsActivity = fragmentActivity;
      //Parse.enableLocalDatastore((FragmentActivity)fragmentActivity);
      //Crashes for some reason if you minimize then reopen:
      Parse.initialize(new Parse.Configuration.Builder((FragmentActivity)fragmentActivity).applicationId(DBID.appID).server(DBID.serverID).build());

      try {
          InputStream inputStream = mapsActivity.getApplicationContext().openFileInput("localVotes.dat");

          if ( inputStream != null ) {
              InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
              BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
              String receiveString = "";
              StringBuilder stringBuilder = new StringBuilder();

              while ( (receiveString = bufferedReader.readLine()) != null ) {
                  stringBuilder.append(receiveString);
                  localVotes.add(receiveString);
              }

              inputStream.close();
              String result = stringBuilder.toString();
          }
      }
      catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }


  }
    public void saveLocalVotes(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("localVotes.dat", Context.MODE_PRIVATE));
            for (String s : localVotes){
                outputStreamWriter.write(s + "\n");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            System.out.println("Exception" +  "File write failed: " + e.toString());
        }
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
    object.put("userID", e.getUserID());

    //object.put("city", e.city);
    object.saveInBackground();
      e.PO = object;
  }
  //function to get an event objects from the database based on 4 coordinates given
  public void loadBetweenCoordinates(LatLngBounds oldBounds) {
      //Loads the area that is two times the screen:
      double distanceVert = oldBounds.northeast.latitude - oldBounds.southwest.latitude;
      double distanceHoriz = oldBounds.northeast.longitude - oldBounds.southwest.longitude;
      LatLng northEast = new LatLng(oldBounds.northeast.latitude + distanceHoriz, oldBounds.northeast.longitude + distanceVert);
      LatLng southWest = new LatLng(oldBounds.southwest.latitude - distanceHoriz, oldBounds.southwest.longitude - distanceVert);
      //Double the bounds for the loaded pins
      LatLngBounds bounds = new LatLngBounds(southWest, northEast);
      ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
      final eventInfo eventFinal = null;
        query.whereGreaterThanOrEqualTo("lat", bounds.southwest.latitude).whereLessThanOrEqualTo("lat", bounds.northeast.latitude).whereGreaterThanOrEqualTo("lon", bounds.southwest.longitude).whereLessThanOrEqualTo("lon", bounds.northeast.longitude);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject o : objects) {

                    System.out.println(o.getObjectId());
                    if (!eventInfoMap.containsKey(o.getObjectId())) {
                        //Print out the test object on the DB:
                        eventInfo event = new eventInfo(o.getString("title"), o.getString("content"), o.getDate("date"), o.getInt("totalVotes"), o.getInt("scoreVotes"), new LatLng(o.getDouble("lat"), o.getDouble("lon")), o.getString("userID"));
                        event.setId(o.getObjectId());
                        mapsActivity.createMarkerFromDB(event);
                        eventInfoMap.put(o.getObjectId(), event);
                        System.out.println("Load object: " + o.getObjectId());
                    }
                }
            }
        });
  }
  //Void to update an event object async
  public void updateDatabase(eventInfo e) {
    
  }
}