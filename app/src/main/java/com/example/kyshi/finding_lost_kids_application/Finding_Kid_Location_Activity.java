package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.kyshi.finding_lost_kid_application.R;

public class Finding_Kid_Location_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttolostchild = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.finding_kid_location);
        //int d =(int)Math.random();
        ImageView map = (ImageView)findViewById(R.id.mapView);
        final ImageView loc = (ImageView)findViewById(R.id.location);

        map.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //Toast.makeText(getApplicationContext(),"Map Untouched! From X: " + event.getX() + ", Y: " + event.getY(), Toast.LENGTH_SHORT).show();
                    loc.setX(event.getX());
                    loc.setY(event.getY());
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Toast.makeText(getApplicationContext(),"Map Touched! From X: " + event.getX() + ", Y: " + event.getY(), Toast.LENGTH_SHORT).show();
                    loc.setX(event.getX());
                    loc.setY(event.getY());
                    return true;
                }

                return false;
            }
        });


    }


}
