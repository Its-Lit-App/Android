package com.kac.its_lit_android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;

/**
 * Created by Adrian on 11/6/2016.
 */

public class event_creation extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creation);



        Button close = (Button)findViewById(R.id.inputSubmit);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();

                EditText edit = (EditText) findViewById(R.id.inputTitle);
                String title = edit.getText().toString();
                edit = (EditText) findViewById(R.id.inputContent);
                String content = edit.getText().toString();
                System.out.println(title);

                i.putExtra("Title", title);
                i.putExtra("Content", content);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }



}
