package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Finding_Kid_Location_Activity extends AppCompatActivity {
    private Context mContext;
    private Intent intenttolostchild;

    /* 서버 통신 관련 변수 */
    private String ANDROID_ID;
    final private static String serverconnURL = "http://swp3.gonetis.com:8888/";
    private String URL;
    private URL url;
    private HttpURLConnection httpURL;
    private BufferedReader br;
    private String servervalue;
    AsyncTask<String, Void, String> httpGetTask;

    /* 반복하기 위한 변수 */
    private TimerTask mTask;
    private Timer mTimer;

    /* JSON 변수 */
    JSONObject jObject;
    JSONArray jarray;

    /* Layout(지도) 관련 변수*/
    private ImageView map;
    private Bitmap image1, image2;
    private PhotoViewAttacher attacher;

    /* Layout(navigation view) 관련 변수 */
    private  View view;
    private AppCompatActivity activity;
    static String[] childs = {"건영","찬우","국민","민성","좀비","좀비","좀비","좀비","좀비"};
    boolean[] checkedItems = new boolean[childs.length];
    private boolean isFabOpen = false;
    private FloatingActionButton fab, reportbutton, returnbutton;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    ActionBarDrawerToggle drawerToggle;
    String child = "child";

    /* SharedPreference 관련 변수*/
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // 서버관련 변수 값 지정 (연결할 url)
        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        URL = serverconnURL + "users/asdf";

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.finding_kid_location);


        // 현재 사용상태 저장하는 sharedpreference 호출
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);


        // 지도 관련 변수 값 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        reportbutton = (FloatingActionButton) findViewById(R.id.report_fab);
        returnbutton = (FloatingActionButton) findViewById(R.id.return_fab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        /**
         * OnCreate 에는 기본 지도만 나오게 해주세요.
         *
         * 모종의 사유로 OnStart 에 서버 구현을 해놓았습니다.
         */


        /* 반납 버튼 눌렀을 때 */
        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 사용상태 미사용으로 변경 후 commit
                editor = sp.edit();
                editor.putBoolean("isconnected", false);
                editor.commit();


                mTimer.cancel();

                if(!httpGetTask.isCancelled()){
                    httpGetTask.cancel(true);
                }

                Intent toMainActivity = new Intent(mContext, MainActivity.class);
                Toast.makeText(mContext, "반납되었습니다.", Toast.LENGTH_LONG).show();
                startActivity(toMainActivity);
                finish();
            }
        });

        /* 신고 버튼 눌렀을 때 */
        reportbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "아직 개발중입니다.", Toast.LENGTH_LONG).show();
            }
        });

        /* floating action button 눌렀을 때 */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.fab:
                        animateFAB();
                        break;
                    case R.id.report_fab:
                        Log.d("Raj", "Fab 1");
                        break;
                    case R.id.return_fab:
                        Log.d("Raj", "Fab 2");
                        break;
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            }
        };
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    View v;
                    //Dialog 생성
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                            Finding_Kid_Location_Activity.this);
                    alertBuilder.setIcon(R.drawable.ic_menu_camera);
                    alertBuilder.setTitle("아이를 선택하세요");

                    alertBuilder.setMultiChoiceItems(childs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            Bitmap image1, image2;
                            Bitmap image[] = new Bitmap[5];
                            map = (ImageView) findViewById(R.id.imageView2);
                            image1 = BitmapFactory.decodeResource(getResources(), R.drawable.map);
                            //image2 = BitmapFactory.decodeResource(getResources(), R.drawable.location);
                            Bitmap tempBitmap = Bitmap.createBitmap(image1.getWidth(), image1.getHeight(), Bitmap.Config.RGB_565);
                            Canvas canvas = new Canvas(tempBitmap);
                            canvas.drawBitmap(image1, 0, 0, null);
                            Toast.makeText(getApplicationContext(), childs[which], Toast.LENGTH_SHORT).show();
                            if (isChecked) // 아이 check 활성화
                            {

                                child = "child";
                                child += Integer.toString(which + 1);
                                Toast.makeText(getApplicationContext(), child, Toast.LENGTH_LONG).show();
                                int resId = getResources().getIdentifier(child, "drawable", getPackageName());
                                if (resId == R.drawable.child1)
                                    Toast.makeText(getApplicationContext(), "same", Toast.LENGTH_LONG).show();
                                image[0] = BitmapFactory.decodeResource(getResources(), R.drawable.child1);
                                image[1] = BitmapFactory.decodeResource(getResources(), R.drawable.child2);
                                image[2] = BitmapFactory.decodeResource(getResources(), R.drawable.child3);
                                canvas.drawBitmap(image[0], 150, 150, null);
                                canvas.drawBitmap(image[1], 500, 500, null);
                                canvas.drawBitmap(image[2], 850, 850, null);
                            }
                            map.setImageBitmap(tempBitmap);
                            attacher = new PhotoViewAttacher(map);
                            attacher.setOnViewTapListener(viewTapListener);
                        }
                    });

                    //버튼
                    alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );
                    //adapter setting


                    alertBuilder.show();


                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                //drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }






    @Override
    protected void onStart() {
        super.onStart();
        /* 주기적 호출 */
        mTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /* 서버연결 */
                        httpGetTask = new AsyncTask<String, Void, String>() {


                            @Override
                            protected String doInBackground(String... strings) {
                                String result = null;
                                ServerConnection sc = new ServerConnection();
                                result = sc.CONNECTION("users/asdf", null, ANDROID_ID);

                                return result;
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                            }
                        };

                        httpGetTask.execute();

                        // JSON Parsing
                        try {
                            String jsonvalue = httpGetTask.get();
                            jObject = new JSONObject(jsonvalue);
                            jarray = new JSONArray(jObject.get("children").toString());

                            String tmp = "";    // 임시로 값 확인할 변수
                            for(int i = 0; i < jarray.length(); i++){
                                JSONObject jsonObject = jarray.getJSONObject(i);
                                tmp += jsonObject.get("name") + ", ";
                            }
                            tmp += "exist";
                            Toast.makeText(mContext, "hello: " + tmp, Toast.LENGTH_SHORT).show();    // 서버 값 잘 받아오는지 확인
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(!httpGetTask.isCancelled()){
                            httpGetTask.cancel(true);
                        }

                        /*
                         * 이 안에 지도 그리기 함수를 구현하시오.
                         * 값 받아온 것 사용법
                         *
                         * int x, y;
                         * String location, tag;
                         * for(int i = 0; i < jarray.length(); i++){
                         *      JSONObject jsonObject = jarray.getJSONObject(i);
                         *      tag = jsonObject.get("tag");
                         *      x =jsonObject.get("x");
                         *      y = jsonObject.get("y");
                         *      location = jsonObject.get("location");
                         * }
                         *
                         * 이러한 식으로 사용해서 이 안에서 지도를 매 번 업데이트 시키면 됩니다.
                         *
                         * 여기 안은 UI 상에서 Timer 를 맞춰 돌아가게 한 쓰레드 입니다.
                         */



                    }
                });

            }
        };

        mTimer = new Timer(false);

        mTimer.schedule(mTask,1000, 4000);    // 1초에 한 번 4초마다 호출
    }

    @Override
    protected void onStop() {
        super.onStop();

        mTimer.cancel();
        try {
            if (!httpGetTask.isCancelled()) {
                httpGetTask.cancel(true);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    PhotoViewAttacher.OnViewTapListener viewTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float x, float y) {
            //map.setVisibility(View.VISIBLE);
            Bitmap image1, image2, resize;
            image1 = BitmapFactory.decodeResource(getResources(), R.drawable.map);
            image2 = BitmapFactory.decodeResource(getResources(), R.drawable.location);
            Bitmap tempBitmap = Bitmap.createBitmap(map.getWidth(), map.getWidth(), Bitmap.Config.RGB_565);
            resize = Bitmap.createScaledBitmap(image1, map.getWidth(), map.getHeight(), true);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(resize, 0, 0, null);
            canvas.drawBitmap(image2, x, y, null);

            map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            Toast.makeText(getApplicationContext(), "image_width: " + image1.getWidth() + "image_height: " + image1.getHeight(), Toast.LENGTH_SHORT).show();


        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    } // 화면 우상단 구현

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"go to setting activity",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.contact){
            Toast.makeText(getApplicationContext(),"010-8922-5615/ mschanwoo@naver.com",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    } // setting , contact 구현




    public void animateFAB() {

        if (isFabOpen) {

            //fab.startAnimation(rotate_backward);
            reportbutton.startAnimation(fab_close);
            returnbutton.startAnimation(fab_close);

            reportbutton.setClickable(false);
            returnbutton.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");
        } else {
            //fab.startAnimation(rotate_forward);
            reportbutton.startAnimation(fab_open);
            returnbutton.startAnimation(fab_open);

            reportbutton.setClickable(true);
            returnbutton.setClickable(true);

            isFabOpen = true;
            Log.d("Raj", "open");
        }


    }

}

