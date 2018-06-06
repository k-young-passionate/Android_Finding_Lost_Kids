package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    AsyncTask<String, Void, String> httpGetReportTask;
    AsyncTask<String, Void, Bitmap> httpGetMapTask;
    AsyncTask<String, Void, String> httpDeleteTask;

    /* 반복하기 위한 변수 */
    private TimerTask mTask;
    private Timer mTimer;

    /* JSON 변수 */
    JSONObject jObject;
    JSONArray jarray;

    /* Layout(지도) 관련 변수*/
    private ImageView map;
    private Bitmap image1, image2, map_bitmap;
    private Canvas canvas;
    private Paint paint = new Paint();
    private PhotoViewAttacher attacher;
    private String map_url;
    private ImageButton markerButton;

    /* Layout(navigation view) 관련 변수 */
    private  View view;
    private AppCompatActivity activity;
    List<String> childs = new ArrayList<>(); // child 이름을 보관하는 List 배열
    List<String> tags = new ArrayList<>(); // 각 child 에 해당하는 tag 정보를 보관하는 배열
    List<Boolean> checkedItems = new ArrayList<>(); // child selection 에서 체크 되었는지 여부를 global 변수로 관리
    List<String> childImagename = new ArrayList<>(); // 각각의 child 에 해당하는 이미지 비트맵 어레이를 보관하는 배열
    List<Integer> xloc = new ArrayList<>();
    List<Integer> yloc = new ArrayList<>();
    private boolean isFabOpen = false;
    private FloatingActionButton fab, reportbutton, returnbutton;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    ActionBarDrawerToggle drawerToggle;
    String child = "child";

    /* SharedPreference 관련 변수*/
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /* 기타 변수 */
    private String pickedtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // 서버관련 변수 값 지정 (연결할 url)
        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.finding_kid_location);


        // 현재 사용상태 저장하는 sharedpreference 호출
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);


        // 지도 관련 변수 값 설정
        Bitmap defaultMap;
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
        markerButton = (ImageButton) findViewById(R.id.marker);

        /* http Asynctask 선언 */

        httpDeleteTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String result = null;
                ServerConnection sc = new ServerConnection();
                result = sc.CONNECTION("users/" + ANDROID_ID, null, ANDROID_ID, sc.MODE_DELETE);

                return result;
            }
        };      // 여기까지 반납하는 친구


        httpGetReportTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String result = null;
                ServerConnection sc = new ServerConnection();
                result = sc.CONNECTION("emerg/" + ANDROID_ID + "/" +strings[0], null, ANDROID_ID, sc.MODE_DELETE);

                return result;
            }
        };      // 여기까지 신고하는 친구


        /* 반납 버튼 눌렀을 때 */
        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 사용상태 미사용으로 변경 후 commit
                editor = sp.edit();
                editor.putBoolean("isconnected", false);
                editor.putBoolean("isdeleted", false);
                editor.commit();

                mTimer.cancel();

                if(!httpGetTask.isCancelled()){
                    httpGetTask.cancel(true);
                }

                httpDeleteTask.execute();

                try {
                    Toast.makeText(mContext, httpDeleteTask.get(), Toast.LENGTH_LONG);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(!httpDeleteTask.isCancelled()){
                    httpDeleteTask.cancel(true);
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
                try{
                    httpGetReportTask.execute(pickedtag);

                    if(!httpGetReportTask.isCancelled()){
                        httpGetReportTask.cancel(true);
                    }

                    Toast.makeText(getBaseContext(), "신고되었습니다.", Toast.LENGTH_LONG).show();
                } catch(Exception e){
                    Toast.makeText(getBaseContext(), "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
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

        ///////*  처음 어플을 켰을 때  기본 지도를 그려주는 부분입니다 *////////////////////////////////////////////
        defaultMap = BitmapFactory.decodeResource(getResources(), R.drawable.map); // R.drawable.map 기본 지도(Bitmap 형식)
        Bitmap tempBitmap = Bitmap.createBitmap(defaultMap.getWidth(), defaultMap.getWidth(), Bitmap.Config.RGB_565);
        Bitmap marker = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
        Bitmap resize = Bitmap.createScaledBitmap(defaultMap, defaultMap.getWidth(), defaultMap.getHeight(), true); // 크기 재조정
        Canvas canvas = new Canvas(tempBitmap); // 그림을 그리는 캔버스 변수 선언 및 초기화
        canvas.drawBitmap(resize, 0, 0, null); // 캔버스 위에 resize bitmap 을 그림

        map = (ImageView)findViewById(R.id.map); // map Imageview 선언 및 초기화
        map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap)); // map 에 방금까지 그린 canvas 적용
        attacher = new PhotoViewAttacher(map); // 확대 및 축소 기능
        attacher.setOnViewTapListener(viewTapListener);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    //Dialog 생성
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Finding_Kid_Location_Activity.this);
                    alertBuilder.setIcon(R.drawable.ic_menu_camera);
                    alertBuilder.setTitle("아이를 선택하세요");
                    final String child[] = new String[childs.size()];
                    boolean checked[] = new boolean[checkedItems.size()];

                    for(int i=0; i< childs.size(); i++) {
                        child[i] = childs.get(i);
                    }
                    for(int j=0; j<checkedItems.size(); j++) {
                        checked[j] = checkedItems.get(j);
                    }

                    alertBuilder.setMultiChoiceItems(child, checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked) {
                                checkedItems.remove(which);
                                checkedItems.add(which,true);
                            } else {
                                Toast.makeText(mContext, "bool: " + isChecked, Toast.LENGTH_SHORT).show();
                                checkedItems.remove(which);
                                checkedItems.add(which, false);
                            }
                        }


                    });

                    //버튼
                    alertBuilder.setNegativeButton("Exit",
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
                        /* http Asynctask 선언 - 이렇게 매번 선언해줘야 execute 오류 안 남*/
                        httpGetTask = new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... strings) {
                                String result = null;
                                ServerConnection sc = new ServerConnection();
                                result = sc.CONNECTION("users/" + ANDROID_ID, null, ANDROID_ID, sc.MODE_GET);

                                return result;
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                            }
                        };      // 여기까지 아이 정보 받아오는 친구

                        httpGetMapTask = new AsyncTask<String, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(String... strings) {
                                Bitmap result = null;
                                ServerConnection sc = new ServerConnection();
                                result = sc.CONNECTION_map("map/" + strings[0], null, ANDROID_ID, sc.MODE_GET);

                                return result;
                            }

                            @Override
                            protected void onPostExecute(Bitmap result) {
                                super.onPostExecute(result);
                            }
                        };      // 여기까지 지도 정보 받아오는 친구


                        // JSON Parsing
                        try {
                            /* 아이 정보 값 받아올 변수들 */
                            String jsonvalue;
                            String name = "";
                            String x, y;
                            x= "";
                            y ="";
                            String location, tag;
                            tag = "";
                            location = "";
                            /* 아이 정보 값 받아올 변수들 */

                            /*** 아이 정보 받아오기 ***/
                            httpGetTask.execute();

                            jsonvalue = httpGetTask.get();
                            jObject = new JSONObject(jsonvalue);
                            jarray = new JSONArray(jObject.get("children").toString());

                            for(int i = 0; i < jarray.length(); i++){
                                JSONObject jsonObject = jarray.getJSONObject(i);

                                name = "";
                                x = "";
                                y= "";
                                tag="";
                                location ="";
                                int resId;
                                String resName= "@drawable/child_";
                                name += jsonObject.get("name"); // 아이 이름
                                tag += jsonObject.get("tag");
                                x =jsonObject.get("x").toString(); // 아이 x 위치
                                y =jsonObject.get("y").toString(); // 아이 y위치
                                location = jsonObject.get("location").toString(); // 아이 위치 (e.g 현대백화점... 등등)

                                if(childs.contains(name))
                                    continue;
                                else {
                                    childs.add(name);
                                    tags.add(tag);
                                    checkedItems.add(true);
                                    resName += tag;
                                    childImagename.add(resName);
                                    xloc.add(10); // 나중에 integer 로 변경
                                    yloc.add(10);
                                }
                            }
                            update();
                            /*** 아이 정보 받아오기 ***/


                            /* !!!!!!!!!!!!!!!!!!!!!이게지도받아오는 코드입니다!!!!!!!!!!!!!!!!!!!!! */
/*                            try {
                                for(int i = 0; i < jarray.length(); i ++){
                                    if(jarray.getJSONObject(i).getString("tag").equals(pickedtag)){
                                        map_url = jarray.getJSONObject(i).getString("location");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
*/
//                            httpGetMapTask.execute(map_url);


//                            map_bitmap = httpGetMapTask.get();
                            /* !!!!!!!!!!!!!!!!!!!!!이게지도받아오는 코드입니다!!!!!!!!!!!!!!!!!!!!! */


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
/*            Bitmap image1, image2, resize;
            image1 = BitmapFactory.decodeResource(getResources(), R.drawable.map);
            image2 = BitmapFactory.decodeResource(getResources(), R.drawable.location);
            Bitmap tempBitmap = Bitmap.createBitmap(map.getWidth(), map.getWidth(), Bitmap.Config.RGB_565);
            resize = Bitmap.createScaledBitmap(image1, map.getWidth(), map.getHeight(), true);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(resize, 0, 0, null);
            canvas.drawBitmap(image2, x, y, null);

            map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            Toast.makeText(getApplicationContext(), "image_width: " + image1.getWidth() + "image_height: " + image1.getHeight(), Toast.LENGTH_SHORT).show();
         */

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


    public void update()
    {
        Bitmap defaultMap = BitmapFactory.decodeResource(getResources(), R.drawable.map); // R.drawable.map 기본 지도(Bitmap 형식)
        Bitmap tempBitmap = Bitmap.createBitmap(defaultMap.getWidth(), defaultMap.getWidth(), Bitmap.Config.RGB_565);
        Bitmap resize;// 크기 재조정
        Bitmap markernotSelected = BitmapFactory.decodeResource(getResources(),R.drawable.markerselect);
        Bitmap markerSelected = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
        int t,i;
        Canvas canvas = new Canvas(tempBitmap); // 그림을 그리는 캔버스 변수 선언 및 초기화
        canvas.drawBitmap(defaultMap,0,0,paint);

        for(i=0; i< checkedItems.size(); i++)
        {
            markerButton.setVisibility(View.INVISIBLE);
            Bitmap temp;
            int resId;
            int count=0;
            for(t=0; t< checkedItems.size(); t++)
            {
                if(checkedItems.get(t) && (xloc.get(t) == xloc.get(i))) count++;
            }
            Toast.makeText(getBaseContext(), "count : "+ count, Toast.LENGTH_SHORT).show();
            if(count >= 2)
            {
                final String sameLoc[] = new String[count];
                final boolean checktemp[] = new boolean[count];
                int index = 0;
                for(t =0; t<checkedItems.size(); t++) {
                    if (xloc.get(t) == xloc.get(i))
                        sameLoc[index] = childs.get(t);
                    index++;

                }
                markerButton.setVisibility(View.VISIBLE);
                //  마커 눌렀을때
                markerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Dialog 생성
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                                Finding_Kid_Location_Activity.this);
                        alertBuilder.setIcon(R.drawable.ic_menu_camera);
                        alertBuilder.setTitle("클릭한 마커 위치의 아이 리스트");
                        alertBuilder.setItems(sameLoc, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertBuilder.show();
                    }

                });

            }

            else {
                if(checkedItems.get(i)) {
                    resId = getResources().getIdentifier(childImagename.get(i), "drawable", getPackageName());
                    Toast.makeText(getBaseContext(), "ID : " + resId, Toast.LENGTH_SHORT).show();
                    temp = BitmapFactory.decodeResource(getResources(), resId);
                    Toast.makeText(getBaseContext(), "xloc: " + xloc.get(i) + "yloc: " + yloc.get(i), Toast.LENGTH_SHORT).show();
                    canvas.drawBitmap(temp, xloc.get(i), yloc.get(i), null);
                }
            }
        }
        map = (ImageView)findViewById(R.id.map); // map Imageview 선언 및 초기화
        map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap)); // map 에 방금까지 그린 canvas 적용
        attacher = new PhotoViewAttacher(map); // 확대 및 축소 기능
        attacher.setOnViewTapListener(viewTapListener);
    }





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

