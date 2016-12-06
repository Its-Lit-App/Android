package com.kac.its_lit_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.HashMap;

//GoogleMap.OnInfoWindowClickListener,
public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback {

    //Variable for debugging:
    public static boolean debug = false;
    //Variables for info window:
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private TextView infoVotes;
    private Button infoButtonDown;
    private Button infoDeleteButton;
    private OnInfoWindowElemTouchListener infoDeleteButtonListener;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private OnInfoWindowElemTouchListener infoButtonDownListener;

    //Variables for the drawer view
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerTitles;

    //private String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
    private String android_id;
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
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

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

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
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
        switch (mDrawerTitles[position]) {
            case "Help":
            {
                Intent i = new Intent(MapsActivity.this, help_window.class);
                Bundle bundle = i.getExtras();
                startActivityForResult(i, 1);
            }
            break;
            case "Settings":
            {
                Intent i = new Intent(MapsActivity.this, settings_window.class);
                Bundle bundle = i.getExtras();
                startActivityForResult(i, 1);
            }
            break;
            case "Featured":
            {
                Intent i = new Intent(MapsActivity.this, featured_window.class);
                Bundle bundle = i.getExtras();
                startActivityForResult(i, 1);
            }
            break;

            default:
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));
        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoDeleteButton = (Button)infoWindow.findViewById(R.id.deleteButton);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
        this.infoVotes = (TextView)infoWindow.findViewById(R.id.votes);
        this.infoButtonDown = (Button)infoWindow.findViewById(R.id.buttonDown);


        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                ContextCompat.getDrawable(this, R.drawable.btn_up_unpressed),
                ContextCompat.getDrawable(this, R.drawable.btn_up_pressed))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                //vote the marker up
                voteMarker(marker, false);
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        this.infoButtonDownListener = new OnInfoWindowElemTouchListener(infoButtonDown,
                ContextCompat.getDrawable(this, R.drawable.btn_down_unpressed),
                ContextCompat.getDrawable(this, R.drawable.btn_down_pressed))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                //vote the marker down
                voteMarker(marker, true);

            }
        };
        this.infoButtonDown.setOnTouchListener(infoButtonDownListener);

        this.infoDeleteButtonListener = new OnInfoWindowElemTouchListener(infoDeleteButton)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                final eventInfo eventinfo = eventMap.get(marker);
                final Marker m2 = marker;
                String eID = null;
                if (eventinfo.getId() == null) {
                    eID = eventinfo.PO.getObjectId();
                    eventinfo.setId(eventinfo.PO.getObjectId());
                } else {
                    eID = eventinfo.getId();
                }
                final String IDFinal = eID;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                //First we need to make sure our object is synced with the database
                // Retrieve the object by id
                query.getInBackground(IDFinal, new GetCallback<ParseObject>() {
                    public void done(ParseObject o, ParseException e) {
                        if (e == null) {
                            o.deleteInBackground();
                        }
                    }
                });
                eventInfo e2 = eventMap.get(marker);
                e2 = null;
                marker.remove();
            }
        };
        this.infoDeleteButton.setOnTouchListener(infoDeleteButtonListener);


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                eventInfo eventinfo = eventMap.get(marker);
                // Setting up the infoWindow with current's marker info
                infoVotes.setText(Integer.toString(eventinfo.getScoreVotes()));
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(eventMap.get(marker).getContent());
                infoButtonListener.setMarker(marker);
                infoButtonDownListener.setMarker(marker);
                infoDeleteButtonListener.setMarker(marker);
                if( eventinfo.getUserID() == android_id || debug)
                    infoDeleteButton.setVisibility(View.VISIBLE);
                else
                    infoDeleteButton.setVisibility(View.GONE);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        //End of info window setup

        eventMap = new HashMap<Marker, eventInfo>();
        // Enable MyLocation Layer of Google Map
        if(checkLocationPermission())
            mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(myLocation != null) {
            // Get latitude of the current location
            double latitude = myLocation.getLatitude();

            // Get longitude of the current location
            double longitude = myLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                p = point;
                Intent i = new Intent(MapsActivity.this, event_creation.class);
                Bundle bundle = i.getExtras();
                startActivityForResult(i, 1);
            }
        });

        //mMap.setOnInfoWindowClickListener(this);

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

    public void voteMarker(Marker marker, final boolean down) {
        // Here we can perform some action triggered after clicking the button
        final Marker m2 = marker;
        final eventInfo eventinfo = eventMap.get(marker);
        String eID = null;
        if (eventinfo.getId() == null) {
            eID = eventinfo.PO.getObjectId();
            eventinfo.setId(eventinfo.PO.getObjectId());
        } else {
            eID = eventinfo.getId();
        }
        final String IDFinal = eID;
        if (!databaseManager.localVotes.contains(eventinfo.getId()) || debug) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
            //First we need to make sure our object is synced with the database
            // Retrieve the object by id
            query.getInBackground(IDFinal, new GetCallback<ParseObject>() {
                public void done(ParseObject o, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                        eventinfo.setScoreVotes(o.getInt("scoreVotes"));
                        eventinfo.setTotalVotes(o.getInt("totalVotes"));
                        if (down) {
                            eventinfo.downVote();
                            System.out.println(m2.getTitle() + "'s button down clicked!");
                        } else {
                            eventinfo.upVote();
                            System.out.println(m2.getTitle() + "'s button up clicked!");
                        }
                        m2.setIcon(getImageForMarker(eventinfo));
                        o.put("totalVotes", eventinfo.getTotalVotes());
                        o.put("scoreVotes", eventinfo.getScoreVotes());
                        o.saveInBackground();

                        if (!databaseManager.localVotes.contains(eventinfo.getId())) {
                            databaseManager.localVotes.add(o.getObjectId());
                        }
                        databaseManager.saveLocalVotes(getApplicationContext());

                        infoVotes.setText(Integer.toString(eventinfo.getScoreVotes()));
                        m2.showInfoWindow();
                    }
                }
            });
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Already voted for that Event!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
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
        eventInfo data = new eventInfo(title, content, new Date(), p, android_id);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(point.latitude, point.longitude))
                .title(title)
                .icon(getImageForMarker(data)));

        databaseManager.saveToDatabase(data);
        System.out.println(point.latitude + "---" + point.longitude);
        eventMap.put(marker, data);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }




    public void createMarkerFromDB(eventInfo event) {

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLat(), event.getLon()))
                .title(event.getTitle())
                .icon(getImageForMarker(event)));
        System.out.println(event.getLat() + "---" + event.getLon());
        eventMap.put(marker, event);
    }

    public BitmapDescriptor getImageForMarker(eventInfo e) {
        if (e.getScoreVotes() <= 0) {
            return BitmapDescriptorFactory.fromResource(R.drawable.mkr_1);
        } else if (e.getScoreVotes() <= 5) {
            return BitmapDescriptorFactory.fromResource(R.drawable.mkr_2);
        } else if (e.getScoreVotes() <= 10) {
            return BitmapDescriptorFactory.fromResource(R.drawable.mkr_3);
        } else  {
            return BitmapDescriptorFactory.fromResource(R.drawable.mkr_4);
        }
    }

    public static void moderationModeOn() {
        debug = true;
    }

    public static void moderationModeOff() {
        debug = false;
    }
}