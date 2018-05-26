package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LoadingActivity extends AppCompatActivity {
    private Context mContext;
    private Intent intenttocountactivity = null;
    private SharedPreferences sp;
    private String ANDROID_ID;
    private Handler handler;

    final private static String serverconnURL = "http://swp3.gonetis.com:8888/";
    private URL url;
    private HttpURLConnection httpURL;
    private BufferedReader br;
    private String servervalue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        mContext = this;
        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        HttpGet getrequest = new HttpGet();
        try {
            getrequest.setURI(new URI("swp3.gonetis.com:8888/"));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {


        AsyncTask<String, Void, String> httpGetTask = new AsyncTask<String, Void, String>() {


            @Override
            protected String doInBackground(String... strings) {
                ServerConnection sc = new ServerConnection();
                String result = sc.CONNECTION(null, null, ANDROID_ID);
                if(result.contains("Hello World!")){
                    result = "0";
                } else if (result == null){
                    return "-1";
                } else {
                    return "1";
                }

                sc.CONNECTION("register/" + ANDROID_ID, null, ANDROID_ID);

                return result;
            }

            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
            }
        };

        httpGetTask.execute();
        /*** 여기서 서버 연결을 확인하고 서버 연결이 되는 환경이면 User_Home_Activity 로 넘겨주자 ***/
                while(true){

                    try {
                        if(!httpGetTask.get().contains("0")){
                            Toast.makeText(mContext, "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                        } else{

                            // AsyncTask 종료 안됐으면 종료
                            if(!httpGetTask.isCancelled())
                                httpGetTask.cancel(true);

                            // 현재 사용상태 저장하는 sharedpreference 호출
                            sp = getSharedPreferences("sp", Context.MODE_PRIVATE);

                            // 현재 상태 확인하고 사용중이라면 지도 화면으로, 아니면 정보입력화면으로 이동
                            boolean isusing = sp.getBoolean("isconnected", false);

                            if(isusing){
                                intenttocountactivity = new Intent(mContext, Finding_Kid_Location_Activity.class);
                            } else {

                                intenttocountactivity = new Intent(mContext, MainActivity.class);
                            }
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                startActivity(intenttocountactivity);
                finish();


            }
        };
        handler.postDelayed(r, 1000);

    }


}
