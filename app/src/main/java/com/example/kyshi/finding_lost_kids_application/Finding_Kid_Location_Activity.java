package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kyshi.finding_lost_kid_application.R;

public class Finding_Kid_Location_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttolostchild = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        intenttolostchild = Intent(mContext, );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.finding_kid_location);



        //setContentView(R.layout.activity_over_map);
    }


}
