package com.kac.its_lit_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Kevin on 11/29/2016.
 */

public class featured_window extends AppCompatActivity {

    private ListView featuredList;
    private ArrayAdapter<String> listAdapter;
    private List<Map<String, String>> dat = new ArrayList<Map<String, String>>();
    private eventInfo[] featured;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.featured_window);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Featured");

        featured = MapsActivity.getFeatured();
        featuredList = (ListView) findViewById(R.id.featuredList);
        dat = new ArrayList<Map<String, String>>();
        for(int i = 0; i < featured.length; i++){
            HashMap<String,String> datum = new HashMap<String, String>();
            datum.put("EventName", featured[i].getTitle());
            datum.put("EventDetails", featured[i].getContent());
            dat.add(datum);
        }

        featuredList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                MapsActivity.setPan(featured[position]);
                finish();
            }
        });

        SimpleAdapter adapter = new SimpleAdapter(this, dat, android.R.layout.simple_list_item_2, new String[] {"EventName", "EventDetails"}, new int[] {android.R.id.text1, android.R.id.text2});
        featuredList.setAdapter(adapter);
    }

    //Function for when the hamburger button has been pressed:
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        //System.out.println("Test.");
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}