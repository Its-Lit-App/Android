package com.kac.its_lit_android;

import android.app.Activity;
import android.graphics.Camera;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;



import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Date;
import java.util.HashMap;
import com.parse.*;

import static android.widget.Toast.LENGTH_LONG;

//GoogleMap.OnInfoWindowClickListener,
public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback {

    private String title, content;
    private LatLng p;
    private GoogleMap mMap;
    private HashMap<Marker, eventInfo> eventMap;
    private DatabaseManager databaseManager;
    private LatLng GAINESVILLE = new LatLng(29.6516, -82.3248);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseManager = new DatabaseManager(this);
        databaseManager.loadBetweenCoordinates(0,0,0,0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        eventMap = new HashMap<Marker, eventInfo>();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                p = point;
                Intent i = new Intent(MapsActivity.this, event_creation.class);
                Bundle bundle = i.getExtras();
                startActivityForResult(i, 1);
            }
        });

        mMap.setOnInfoWindowClickListener(this);

        //Setup listener for map moving

        mMap.setOnCameraMoveListener(this);

        //Default Map Location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(GAINESVILLE));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(GAINESVILLE)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
    }

    //When the map is moved, this function is called:
    @Override
    public void onCameraMove() {



    }

    public void onInfoWindowClick(Marker marker){
        eventInfo eventinfo = eventMap.get(marker);
        Toast.makeText(getBaseContext(), eventinfo.getTitle() + eventinfo.getContent(),
                LENGTH_LONG).show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                title = data.getStringExtra("Title");
                content = data.getStringExtra("Content");
                createMarker(p, title, content);


            }
        }
    }
    public void createMarker(LatLng point, String title, String content){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(point.latitude, point.longitude))
                .title(title));

        eventInfo data = new eventInfo(title, content, new Date(), p);
        databaseManager.saveToDatabase(data);
        System.out.println(point.latitude + "---" + point.longitude);
        eventMap.put(marker, data);
    }

    public void createMarkerFromDB(eventInfo event) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLat(), event.getLon()))
                .title(event.getTitle()));
        System.out.println(event.getLat() + "---" + event.getLon());
        eventMap.put(marker, event);
    }

}







