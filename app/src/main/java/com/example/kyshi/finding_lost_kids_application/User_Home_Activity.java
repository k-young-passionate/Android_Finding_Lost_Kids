package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import org.json.JSONException;
import org.json.JSONObject;

public class User_Home_Activity extends AppCompatActivity {

    // Intent, context, activity 관련 변수
    private Context mContext = this;
    private Intent intenttokidphotouploadactivity = null;
    public static User_Home_Activity AActivity;

    // layout 관련 변수
    private EditText Tag_sn = null;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        AActivity = User_Home_Activity.this;    // Kid_Photo_Upload_Activity 에서 여기 Activity 종료용

        // layout 배경 하얗게
        cl = findViewById(R.id.user_home_activity_constraintlayout);
        cl.setBackgroundColor(Color.WHITE);

        intenttokidphotouploadactivity = new Intent(mContext, Kid_Photo_Upload_Activity.class);     // 다음 Activity 로 넘어가기 위한 intent 설정

        /**
         * Layout view 들과 연동
         */

        ImageButton btn = (ImageButton) findViewById(R.id.button);
        Tag_sn = (EditText)findViewById(R.id.editText);


        /**
         * 첫 화면에서 버튼을 눌렀을 때 동작
         */

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tag_sn = Tag_sn.getText().toString();
                try {
                    /* 서버 연결 시도 추가 필요
                     * Try Catch 문은 서버 연결 지연이 될 경우를 처리하는 용도임
                     * If 문은 session 코드를 안 넣었은 경우를 확인하는 용도임
                     * */


                    if(tag_sn.length() != 0) {
                        startActivity(intenttokidphotouploadactivity);
                        Toast.makeText(getApplicationContext(), "태그 코드: " + tag_sn, Toast.LENGTH_LONG).show();
                        intenttokidphotouploadactivity.putExtra("tagnum", tag_sn);

                    } else {
                        Toast.makeText(getApplicationContext(), "태그 코드를 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }

    private void sendObject(int num){
        JSONObject jo = new JSONObject();
        try{
            jo.put("tag_sn", num);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void receiveObject(JSONObject data){
        try{
            data.getInt("err");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
