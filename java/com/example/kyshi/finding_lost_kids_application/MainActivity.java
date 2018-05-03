package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kyshi.finding_lost_kid_application.R;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttocountactivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        intenttocountactivity = new Intent(mContext, CountNumber.class);
        /*** 여기서 서버 연결을 확인하고 서버 연결이 되는 환경이면 User_Home_Activity 로 넘겨주자 ***/

        if(true){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intenttocountactivity);
                }
            }, 2000);
            
        } else{

        }
    }


}
