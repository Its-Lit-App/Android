package com.kac.its_lit_android;

import android.app.Activity;
import android.graphics.Camera;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private String title, content;
    private LatLng p;
    private GoogleMap mMap;
    private HashMap<Marker, eventInfo> eventMap;
    private DatabaseManager databaseManager;
    private LatLng GAINESVILLE = new LatLng(29.6516, -82.3248);
    private double[] distanceChecker = {50, 30, 25, 15, 10, 7.5, 5, 2.5, 1.5, 1, 0.5, 0.25, 0.1, 0.05, 0.025, 0.01, 0.005, 0.003, 0.002, 0.001, 0.0005};
    private CameraPosition lastUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //handle side nav
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        /*
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());*/

        databaseManager = new DatabaseManager(this);
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
        lastUpdate = mMap.getCameraPosition();

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        databaseManager.loadBetweenCoordinates(bounds);
    }

    //When the map is moved, this function is called:
    @Override
    public void onCameraMove() {

        CameraPosition newPosition = mMap.getCameraPosition();
        //Only update if we have changed enough position from before:
        if (pythag(lastUpdate, newPosition) > distanceChecker[(int)newPosition.zoom]) {
            System.out.println("Zoom: " + newPosition.zoom + ", Distance: " + distanceChecker[(int)newPosition.zoom]);
            lastUpdate = newPosition;
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            databaseManager.loadBetweenCoordinates(bounds);
            System.out.println("Updated at: " + bounds);
        }

    }

    public double pythag(CameraPosition oldPosition, CameraPosition newPosition) {
        Double latOld = oldPosition.target.latitude;
        Double lonOld = oldPosition.target.longitude;
        Double latNew = newPosition.target.latitude;
        Double lonNew = newPosition.target.longitude;

        return Math.sqrt(Math.pow(latOld-latNew, 2) + Math.pow(lonOld-lonNew, 2));
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







