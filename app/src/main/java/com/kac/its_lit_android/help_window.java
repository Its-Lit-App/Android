package com.kac.its_lit_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

/**
 * Created by Kevin on 11/29/2016.
 */

public class help_window extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_window);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Help");

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