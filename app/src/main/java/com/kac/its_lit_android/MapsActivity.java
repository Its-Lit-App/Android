package com.kac.its_lit_android;

import android.app.Activity;
import android.graphics.Camera;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.AdapterView;
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
public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback {

    //Variables for the drawer view
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerTitles;

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



        //Handle Drawer initialization
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
        getSupportActionBar().setTitle("It's Lit");


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        databaseManager = new DatabaseManager(this);
    }

    //Function for when the hamburger button has been pressed:
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        //System.out.println("Test.");
        if (menuItem.getItemId() == android.R.id.home) {
            //System.out.println("Home Pressed.");
            if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //Define our listener class for the app button:
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //view.setBackgroundColor(Color.CYAN);
            mDrawerList.clearChoices();
            selectItem(position);
        }
    }

    //Function that gets called when a navigation items gets selected:
    private void selectItem(int position) {
        System.out.println("Selected item: " + mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
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

        //
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //Only load the current on screen markers if after the map has loaded:
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                databaseManager.loadBetweenCoordinates(bounds);
            }
        });

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







