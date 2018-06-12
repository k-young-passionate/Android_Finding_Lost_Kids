package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import uk.co.senab.photoview.PhotoViewAttacher;


public class Finding_Kid_Location_Activity extends AppCompatActivity {
    private Context mContext;

    /* DB 관련 변수 */
    DBhelp dbhelp;
    LocationDB dbloc;

    /* 서버 통신 관련 변수 */
    private String ANDROID_ID;
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
    private Bitmap map_bitmap;
    private PhotoViewAttacher attacher;
    private String map_url;
    private ImageButton markerButton;
    Bitmap tempBitmap;
    Canvas canvas;
    /* Layout(navigation view) 관련 변수 */
    String tag[];
    String child[];
    String tagpluschildlist[];
    int check = 0;
    boolean checkedItems[]; // child selection 에서 체크 되었는지 여부를 global 변수로 관리
    ArrayList<byte[]> photolist = new ArrayList<>();
    private boolean isFabOpen = false;
    private FloatingActionButton fab, reportbutton, returnbutton;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    ActionBarDrawerToggle drawerToggle;

    /* SharedPreference 관련 변수*/
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /* 기타 변수 */
    private String pickedtag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // DB 설정
        dbhelp = DBhelp.getHelper(mContext);
        dbhelp.onOpen(MainActivity.db_kid);
        dbloc = new LocationDB(getApplicationContext(), "testing123.db", null, 2);

        // 지도 그리기 관련 설정
        Bitmap map1 = BitmapFactory.decodeResource(getResources(), R.drawable.child1);
        Bitmap map2 = BitmapFactory.decodeResource(getResources(), R.drawable.child2);
        //photolist = dbhelp.getPhotoArray();

        // 서버관련 변수 값 지정 (연결할 url)
        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        // 화면 뷰 관련 설정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_finding_kid_location);


        // 현재 사용상태 저장하는 sharedpreference 호출
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);


        // 지도 관련 변수 값 설정
        Bitmap defaultMap;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = findViewById(R.id.fab);
        reportbutton = findViewById(R.id.report_fab);
        returnbutton = findViewById(R.id.return_fab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        //markerButton = findViewById(R.id.marker);

        /* http Asynctask 선언 */

        httpDeleteTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String result = null;
                ServerConnection sc = new ServerConnection();
                result = ServerConnection.CONNECTION("users/" + ANDROID_ID, null, ANDROID_ID, ServerConnection.MODE_DELETE);
                return result;
            }
        };      // 여기까지 반납하는 친구


        httpGetReportTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String result = null;
                ServerConnection sc = new ServerConnection();
                result = ServerConnection.CONNECTION("emerg/" + ANDROID_ID + "/" + strings[0], null, ANDROID_ID, ServerConnection.MODE_DELETE);

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

                try {
                    if (!httpGetTask.isCancelled()) {
                        httpGetTask.cancel(true);
                    }

                    httpDeleteTask.execute();

                    for (int i = 0; i < 100; i++) {
                        Log.d("time delay", i + "delayed");
                    }

                    /*
                    if (!httpDeleteTask.isCancelled()) {
                        httpDeleteTask.cancel(true);
                    }*/

                    Intent toMainActivity = new Intent(mContext, MainActivity.class);
                    Toast.makeText(mContext, "반납되었습니다.", Toast.LENGTH_LONG).show();
                    dbloc.deleteall();
                    dbhelp.deleteAll();
                    startActivity(toMainActivity);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(mContext, "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        /* 신고 버튼 눌렀을 때 */
        reportbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    httpGetReportTask.execute(pickedtag);
                    Toast.makeText(getBaseContext(), "신고되었습니다.", Toast.LENGTH_LONG).show();
                    if (!httpGetReportTask.isCancelled()) {
                        httpGetReportTask.cancel(false);
                    }
                } catch (Exception e) {
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);

            }
        };
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ///////*  처음 어플을 켰을 때  기본 지도를 그려주는 부분입니다 *////////////////////////////////////////////
        map = findViewById(R.id.map); // map Imageview 선언 및 초기화
        Bitmap base = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        Bitmap fromDB = getBitmapFromByteArray(dbloc.getmapResult("2", getByteArrayFromBitmap(base)));
        //defaultMap = BitmapFactory.decodeResource(getResources(), R.drawable.asd); // R.drawable.map 기본 지도(Bitmap 형식)

        Bitmap tempBitmap = Bitmap.createBitmap(fromDB.getWidth() + 500, fromDB.getWidth() + 200, Bitmap.Config.RGB_565);
        //Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        Bitmap resize = Bitmap.createScaledBitmap(fromDB, fromDB.getWidth() + 500, fromDB.getWidth() + 200, true); // 크기 재조정
        Canvas canvas = new Canvas(tempBitmap); // 그림을 그리는 캔버스 변수 선언 및 초기화
        canvas.drawBitmap(resize, 0, 0, null); // 캔버스 위에 resize bitmap 을 그림


        map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap)); // map 에 방금까지 그린 canvas 적용
        attacher = new PhotoViewAttacher(map); // 확대 및 축소 기능
        attacher.setOnViewTapListener(viewTapListener);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // 네비게이션 바에서 아이 선택하는 함수
        NavigationView navigationView = findViewById(R.id.nav_view);
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

                    ArrayList<String> temp = dbloc.getTagResult();
                    ArrayList<String> childtemp = dbloc.getResultArray();

                    child = new String[temp.size()];
                    tag = new String[temp.size()];
                    tagpluschildlist = new String[temp.size()];
                    //check = new boolean[temp.size()];
                    for (int i = 0; i < temp.size(); i++) {
                        tagpluschildlist[i] = temp.get(i);
                        child[i] = childtemp.get(i);
                        tag[i] = temp.get(i);
                        tagpluschildlist[i] += "(";
                        tagpluschildlist[i] += child[i];
                        tagpluschildlist[i] += ")";

                    }

                    alertBuilder.setSingleChoiceItems(tagpluschildlist, check, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            check = which;
                            pickedtag = tag[which];
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
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                                result = ServerConnection.CONNECTION("users/" + ANDROID_ID, null, ANDROID_ID, ServerConnection.MODE_GET);

                                return result;
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                            }
                        };      // 여기까지 아이 정보 받아오는 친구


                        // JSON Parsing
                        try {
                            /* 아이 정보 값 받아올 변수들 */
                            String jsonvalue;
                            String name = "";
                            String x, y;
                            x = "";
                            y = "";
                            String location, tag;
                            tag = "";
                            location = "";
                            /* 아이 정보 값 받아올 변수들 */

                            /*** 아이 정보 받아오기 ***/
                            httpGetTask.execute();

                            jsonvalue = httpGetTask.get();
                            jObject = new JSONObject(jsonvalue);
                            jarray = new JSONArray(jObject.get("children").toString());

                            for (int i = 0; i < jarray.length(); i++) {

                                JSONObject jsonObject = jarray.getJSONObject(i);

                                name = "";
                                x = "";
                                y = "";
                                tag = "";
                                location = "";
                                tag += jsonObject.get("tag"); // 아이 태그
                                name += jsonObject.get("name"); // 아이 이름
                                x = jsonObject.get("x").toString(); // 아이 x 위치
                                y = jsonObject.get("y").toString(); // 아이 y위치
                                location = jsonObject.get("location").toString(); // 아이 위치 (e.g 현대백화점... 등등)

                                map_url = jarray.getJSONObject(i).getString("location"); //
                                if (dbloc.search(tag) && dbloc.getlocResult(tag).equals(map_url)) // tag도 같고 층도 안변했다면
                                {

                                    dbloc.updateCor(tag, x, y);  // 좌표만 변경
                                } else {
                                    /////////////////////////////////////////////////////////////
                                    httpGetMapTask = new AsyncTask<String, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(String... strings) {
                                            Bitmap result = null;
                                            ServerConnection sc = new ServerConnection();
                                            result = ServerConnection.CONNECTION_map("map/" + strings[0], null, ANDROID_ID, ServerConnection.MODE_GET);

                                            return result;
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap result) {
                                            super.onPostExecute(result);
                                        }
                                    };      // 여기까지 지도 정보 받아오는 친구
                                    ////// map 을 받기 위해서 httptask 다시 선언/////////


                                    httpGetMapTask.execute(map_url);
                                    if (map_url.equals("none")) {
                                        map_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
                                    } else {
                                        map_bitmap = httpGetMapTask.get();    //  tag에 해당하는 bit map 저장 완료
                                    }
                                    if (!dbloc.search(tag)) { // 기존 tag가 없으면 insert
                                        dbloc.insert(name, tag, x, y, location, "false", getByteArrayFromBitmap(map_bitmap));
                                    } else if (!dbloc.getlocResult(tag).equals(location)) { // map 이름이 다르다면
                                        dbloc.delete(tag);
                                        dbloc.insert(name,tag,x,y,location,"false",getByteArrayFromBitmap(map_bitmap));
                                    } // 이부분은 update 함수가 잘 안먹어서 이렇게 씀.
                                }
                                //!!이게지도받아오는 코드입니다!!!/ /
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!httpGetTask.isCancelled()) {
                            httpGetTask.cancel(true);
                        }

                        update();
                    } //// run 끝
                });

            }
        };

        mTimer = new Timer(false);

        mTimer.schedule(mTask, 1000, 7000);    // 1초에 한 번 4초마다 호출
    }

    public void update() {

        if (pickedtag.length() == 0)
            return;
        else {
            final ArrayList<String> child = dbloc.getResultArray();  // 밑의 listview 리스너에서 쓸수있도록 final 선언
            final ArrayList<String> pickedX;
            final ArrayList<String> pickedY;
            final ArrayList<byte[]> photo_list;
            String loc = dbloc.getlocResult(pickedtag);
            pickedX = dbloc.getXResult(loc);
            pickedY = dbloc.getYResult(loc);
            ArrayList<String> child_loc_name = dbloc.getlocResultArray(loc);
            ArrayList<String> child_tag_loc = dbloc.getTagLocationResult(loc);
            final String[] Listchild = new String[child_loc_name.size()]; // 지도 오른쪽 아이 리스트뷰에 들어가는 리스트

            photo_list = new ArrayList<>();
            //**//photo_list = dbhelp.getLocPhotoArray(loc); // 해당 location 에 해당하는 애들 사진 array 받는다. 사진넣고 주석풀어서 사용
            //*// 테스트용

            for (int i = 0; i < child_tag_loc.size(); i++) {
                Bitmap tempp = BitmapFactory.decodeByteArray(dbhelp.getTagPhoto(child_tag_loc.get(i)), 0, dbhelp.getTagPhoto(child_tag_loc.get(i)).length);
                photo_list.add(getByteArrayFromBitmap(tempp));
            }


            Bitmap base = BitmapFactory.decodeResource(getResources(), R.drawable.map);

            Bitmap fromDB = getBitmapFromByteArray(dbloc.getmapResult(pickedtag, getByteArrayFromBitmap(base)));
            //http://swp3.gonetis.com:8888/location/aed46c541c320973/5555/bb2

            tempBitmap = Bitmap.createBitmap(fromDB.getWidth() + 500, fromDB.getWidth() + 200, Bitmap.Config.RGB_565);
            //Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            Bitmap resize = Bitmap.createScaledBitmap(fromDB, fromDB.getWidth() + 500, fromDB.getWidth() + 200, true); // 크기 재조정
            canvas = new Canvas(tempBitmap); // 그림을 그리는 캔버스 변수 선언 및 초기화
            canvas.drawBitmap(resize, 0, 0, null); // 캔버스 위에 resize bitmap 을 그림
            for (int i = 0; i < pickedX.size(); i++) {

                Bitmap drawchild = getBitmapFromByteArray(photo_list.get(i));
                Bitmap texted = writeOnDrawable(drawchild, child.get(i), 133);
                Bitmap rounded = getCircularBitmap(texted);
                Listchild[i] = child_loc_name.get(i);
                canvas.drawBitmap(rounded, Integer.parseInt(pickedX.get(i)), Integer.parseInt(pickedY.get(i)), null);

            } // 해당 loc 으로 받은 x,y 값, 아이 사진을 캔버스에 각각 그린다.

            map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap)); // map 에 방금까지 그린 canvas 적용
            attacher = new PhotoViewAttacher(map); // 확대 및 축소 기능
            attacher.setOnViewTapListener(viewTapListener);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Listchild);
            ListView listview = findViewById(R.id.present_child_list);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bitmap drawchild = getBitmapFromByteArray(photo_list.get(position));
                    Bitmap texted = writeOnDrawable(drawchild, child.get(position), 60);
                    Bitmap rounded = getCircularBitmap(texted);
                    canvas.drawBitmap(rounded, Integer.parseInt(pickedX.get(position)), Integer.parseInt(pickedY.get(position)), null);

                    map.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap)); // map 에 방금까지 그린 canvas 적용
                    attacher = new PhotoViewAttacher(map); // 확대 및 축소 기능
                    attacher.setOnViewTapListener(viewTapListener);
                }
            });

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mTimer.cancel();
        try {
            if (!httpGetTask.isCancelled()) {
                httpGetTask.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    PhotoViewAttacher.OnViewTapListener viewTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float x, float y) {


        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            Toast.makeText(getApplicationContext(), "아직은 이용하실 수 없는 기능입니다.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.contact) {
            Toast.makeText(getApplicationContext(), "010-1234-5678/ Software5@skku.edu", Toast.LENGTH_SHORT).show();
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

    public byte[] getByteArrayFromBitmap(Bitmap mapbitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mapbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }

    public Bitmap getBitmap(byte[] b) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    public Bitmap getBitmapFromByteArray(byte[] bytes) {
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public Bitmap writeOnDrawable(Bitmap origin, String text, int TextSize) {

        Bitmap bm = origin.copy(Bitmap.Config.ARGB_8888, true);


        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(TextSize);
        paint.setTextAlign(Paint.Align.CENTER);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth() / 2, bm.getHeight() / 2, paint);

        return bm;
    }


}

