package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    /* Intent 및 Context 변수 */
    public static Context mContext;
    private Intent Intent_To_User_Home_Activity = null;
    private Intent Intent_To_Finding_Kid_Location = null;
    public static SQLiteDatabase db_kid;

    /* Layout 관련 변수 */
    private ListView registered_view;
    private Button register_button;
    private Button delete_button;
    private Button registered_button;
    private ImageButton next;
    RelativeLayout vislay1;
    RelativeLayout invislay1;
    ConstraintLayout cl;

    /* SharedPreference 관련 변수 */
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /* 서버 관련 Asynctask */
    AsyncTask<String, Void, String> httpDeleteTask;

    /* 지역 변수 및 상수 */
    String cur_delivery;
    public static DBhelp dbhelp;
    private String ANDROID_ID;
    public static final String DB_NAME = "CHILD1.db";

    ListView kidListView;
    KidAdapter kidAdapter = new KidAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // 화면 리스트 구현
        dbhelp = new DBhelp(mContext, "CHILD1.db", null, 2);

        // 현재 사용상태 저장하는 sharedpreference 호출
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean("isconnected", false);
        editor.commit();

        /* 반납했으면 아이 정보 삭제 */
        if (!sp.getBoolean("isdeleted", false)) {
            dbhelp.deleteAll();
            kidAdapter.removeAll();
            editor = sp.edit();
            editor.putBoolean("isdeleted", true);
            editor.commit();
        }

        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.child1);
        byte[] photo = getByteArrayFromBitmap(temp);

        // 기본 화면 세팅
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);


        // layout 배경 하얗게
        cl = findViewById(R.id.main_activity);
        cl.setBackgroundColor(Color.rgb(255, 254, 179));

        /* Listview */
        kidListView = findViewById(R.id.kidListView);
        kidListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //예시
        initView(dbhelp);
        kidListView.setAdapter(kidAdapter);

        /* 버튼 등록 */
        register_button = findViewById(R.id.register);
        registered_button = findViewById(R.id.registered);
        registered_view = findViewById(R.id.registered_child_list);
        delete_button = findViewById(R.id.delete);
        next = findViewById(R.id.next);
        vislay1 = findViewById(R.id.visLay1);
        invislay1 = findViewById(R.id.invisLayout1);


        /* findind_kid_location 액티비티로 넘기는 버튼 */
        next.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (dbhelp.findnum() == 0) {
                    Toast.makeText(mContext, "아이를 등록해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    // 현재 사용상태 사용으로 변경 후 commit
                    editor = sp.edit();
                    editor.putBoolean("isconnected", true);
                    editor.commit();

                    Intent_To_Finding_Kid_Location = new Intent(mContext, Finding_Kid_Location_Activity.class);
                    Intent_To_Finding_Kid_Location.putExtra("String", cur_delivery);
                    db_kid = DBhelp.db;
                    startActivityForResult(Intent_To_Finding_Kid_Location, 0);
                }

            }
        });


        register_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent_To_User_Home_Activity = new Intent(mContext, Tag_Check_Activity.class);
                startActivityForResult(Intent_To_User_Home_Activity, 0);

            }
        });


        delete_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SparseBooleanArray check = kidListView.getCheckedItemPositions();
                int count = kidAdapter.getCount();
                for (int i = count - 1; i >= 0; i--) {
                    if (check.get(i)) {
                        httpDeleteTask = new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... strings) {
                                String result = null;
                                ServerConnection sc = new ServerConnection();
                                result = ServerConnection.CONNECTION("users/" + ANDROID_ID + "/" + strings[0], null, ANDROID_ID, ServerConnection.MODE_DELETE);

                                return result;
                            }
                        };
                        Kid kid = (Kid) kidAdapter.getItem(check.keyAt(i));
                        httpDeleteTask.execute(kid.getTag_sn());
                        kidAdapter.removeItem(check.keyAt(i));
                        dbhelp.delete(kid.getTag_sn());
                    }
                }
                kidListView.clearChoices();
                vislay1.setVisibility(View.VISIBLE);
                invislay1.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
                kidAdapter.visMode = false;
                kidAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (!httpDeleteTask.isCancelled()) {
                httpDeleteTask.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (resultCode) {
            // 정상 작동
            case 1:
                Long now = System.currentTimeMillis();
                Date date = new Date(now);
                //날짜 출력 포맷 설정
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                String dateResult = simpleDateFormat.format(date);
                String name = data.getStringExtra("Name");
                String tag = data.getStringExtra("Tag");
                Bitmap photo = data.getParcelableExtra("Photo");
                byte[] photo_byte = getByteArrayFromBitmap(photo);
                dbhelp.insert(dateResult, name, tag, photo_byte);
                Kid kid = new Kid(name, tag, photo);

                kidAdapter.addItem(kid);
                kidAdapter.notifyDataSetChanged();
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "정상적으로 값을 받았습니다.", Toast.LENGTH_SHORT).show();
                cur_delivery = data.getStringExtra("loc_name");
                break;
            case 3:
                String cur_delivered = data.getStringExtra("Name");
            default:
                break;

        }
    }

    public void initView(DBhelp dbhelp) {
        ArrayList<String> names = dbhelp.getNameArray();
        ArrayList<String> tags = dbhelp.getTagArray();
        ArrayList<byte[]> photos = dbhelp.getPhotoArray();

        int count = names.size();
        for (int i = 0; i < count; i++) {
            Kid kid = new Kid(names.get(i), tags.get(i), getBitmapFromByteArray(photos.get(i)));
            kidAdapter.addItem(kid);
        }
        kidAdapter.notifyDataSetChanged();
    }


    public void onEditButtonClicked(View v) {
        vislay1.setVisibility(View.INVISIBLE);
        invislay1.setVisibility(View.VISIBLE);
        next.setVisibility(View.INVISIBLE);
        kidAdapter.visMode = true;
        kidListView.clearChoices();
        kidAdapter.notifyDataSetChanged();
    }

    public void onEscButtonClicked(View v) {
        vislay1.setVisibility(View.VISIBLE);
        invislay1.setVisibility(View.INVISIBLE);
        next.setVisibility(View.VISIBLE);
        kidAdapter.visMode = false;
        kidAdapter.notifyDataSetChanged();
    }

    public byte[] getByteArrayFromDrawable(Drawable photo) {
        if (photo == null)
            return null;
        Bitmap bitmap = ((BitmapDrawable) photo).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }

    public byte[] getByteArrayFromBitmap(Bitmap photo) {
        if (photo == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }

    public Bitmap getBitmapFromByteArray(byte[] bytes) {
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }
}

