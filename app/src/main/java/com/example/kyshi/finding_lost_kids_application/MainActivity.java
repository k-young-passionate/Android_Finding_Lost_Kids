package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private Intent intenttocountactivity = null;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this;


        /*** 여기서 서버 연결을 확인하고 서버 연결이 되는 환경이면 User_Home_Activity 로 넘겨주자 ***/
                while(true){

                    if(false){
                        Toast.makeText(mContext, "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();


                    } else{
                        // 현재 사용상태 저장하는 sharedpreference 호출
                        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);

                        // 현재 상태 확인하고 사용중이라면 지도 화면으로, 아니면 정보입력화면으로 이동
                        boolean isconnected = sp.getBoolean("isconnected", false);

                        if(isconnected){
                            intenttocountactivity = new Intent(mContext, Finding_Kid_Location_Activity.class);
                        } else {
                            intenttocountactivity = new Intent(mContext, User_Home_Activity.class);
                        }
                        break;
                    }
                }

                startActivity(intenttocountactivity);
                finish();



    }

}
