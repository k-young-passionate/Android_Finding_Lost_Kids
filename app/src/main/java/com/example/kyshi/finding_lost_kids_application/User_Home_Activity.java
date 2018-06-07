package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class User_Home_Activity extends AppCompatActivity {
    private final static int REQUEST_CODE_KID_PHOTO = 400;

    // Intent, context, activity 관련 변수
    private Context mContext = this;
    private Intent intenttokidphotouploadactivity = null;
    public static User_Home_Activity AActivity;
    private Intent getIntent;
    private Intent turnoverIntent;

    // layout 관련 변수
    private EditText Tag_sn = null;
    private ConstraintLayout cl;

    //  일반 지역 변수
    private String tag_sn;

    // http 통신 관련 변수
    AsyncTask<String, Void, String> httpPostTask;
    private String ANDROID_ID;
    Kid kid;

    // SharedPreference
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        AActivity = User_Home_Activity.this;    // Kid_Photo_Upload_Activity 에서 여기 Activity 종료용

        // 지난 것에서 가져온 intent
        getIntent = getIntent();

        // layout 배경 하얗게
        cl = findViewById(R.id.user_home_activity_constraintlayout);
        cl.setBackgroundColor(Color.WHITE);

        intenttokidphotouploadactivity = new Intent(mContext, Kid_Photo_Upload_Activity.class);     // 다음 Activity 로 넘어가기 위한 intent 설정

        kid = new Kid();
        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        httpPostTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {

                ServerConnection sc = new ServerConnection();

                String result = sc.CONNECTION("users/"+ ANDROID_ID, kid, ANDROID_ID, sc.MODE_POST);
//                                                sc.CONNECTION("map/" + map_id, null, ANDROID_ID);
                return result;
            }
        };

        /**
         * Layout view 들과 연동
         */

        ImageButton btn = (ImageButton) findViewById(R.id.button);
        Tag_sn = (EditText) findViewById(R.id.editText);


        /**
         * 첫 화면에서 버튼을 눌렀을 때 동작
         */

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag_sn = Tag_sn.getText().toString();
                try {
                    /* 서버 연결 시도 추가 필요
                     * Try Catch 문은 서버 연결 지연이 될 경우를 처리하는 용도임
                     * If 문은 session 코드를 안 넣었은 경우를 확인하는 용도임
                     * */


                    if (tag_sn.length() != 0) {

                        kid.setTag_sn(tag_sn);
                        sp = getSharedPreferences("sp", MODE_PRIVATE);
                        editor = sp.edit();
                        editor.putString("tag_sn", tag_sn);
                        editor.commit();
                        //httpPostTask.execute(tag_sn);
                        startActivityForResult(intenttokidphotouploadactivity, REQUEST_CODE_KID_PHOTO);
                    } else {
                        Toast.makeText(getApplicationContext(), "태그 코드를 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_KID_PHOTO&&resultCode==RESULT_OK){
            data.putExtra("Tag", tag_sn);
            setResult(1,data);
            finish();
        }

    }
}
