package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

public class Finding_Kid_Location_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttolostchild = null;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        intenttolostchild = Intent(mContext, );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.finding_kid_location);


        // 현재 사용상태 저장하는 sharedpreference 호출
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);


        ImageView reportbutton = findViewById(R.id.reportbutton);
        ImageView returnbutton = findViewById(R.id.returnbutton);
        ImageView map = (ImageView) findViewById(R.id.mapView);


        // 버튼이 자꾸 지도 뒤로가서... 앞으로 호출
        reportbutton.bringToFront();
        returnbutton.bringToFront();

        /**
         * 지도 이미지를 드래그 했을 경우 지도 화면을 움직일 수 있다.
         */
        map.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //Toast.makeText(getApplicationContext(),"Map Untouched! From X: " + event.getX() + ", Y: " + event.getY(), Toast.LENGTH_SHORT).show();
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    //Toast.makeText(getApplicationContext(),"Map Draged! From X: " + event.getX() + ", Y: " + event.getY(), Toast.LENGTH_SHORT).show();
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Toast.makeText(getApplicationContext(),"Map Touched! From X: " + event.getX() + ", Y: " + event.getY(), Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

        /*
        신고버튼 구현
         */

        reportbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "신고되었습니다.", Toast.LENGTH_LONG).show();
            }
        });


        /*
        반납버튼 구현
         */

        returnbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // 현재 사용상태 미사용으로 변경 후 commit
                editor = sp.edit();
                editor.putBoolean("isconnected", false);
                editor.commit();

                Intent toMainActivity = new Intent(mContext, MainActivity.class);
                Toast.makeText(mContext, "반납되었습니다.", Toast.LENGTH_LONG).show();
                startActivity(toMainActivity);
                finish();
            }
        });
    }


}
