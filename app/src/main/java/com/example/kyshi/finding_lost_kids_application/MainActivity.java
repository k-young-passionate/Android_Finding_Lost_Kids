package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kyshi.finding_lost_kid_application.R;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttouserhomeactivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(true){
            intenttouserhomeactivity = new Intent(mContext, User_Home_Activity.class);
            startActivity(intenttouserhomeactivity);
        } else{

        }
    }
}
