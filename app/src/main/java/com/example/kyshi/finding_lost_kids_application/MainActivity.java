package com.example.kyshi.finding_lost_kids_application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity  {
    /* Intent 및 Context 변수 */
    private Context mContext = this;
    private Intent Intent_To_User_Home_Activity = null;
    private Intent Intent_To_Finding_Kid_Location = null;
    private Intent Intent_To_Kid_Photo_Upload_Activity = null;

    /* Layout 관련 변수 */
    private ListView registered_view;
    private Button register_button;
    private Button delete_button;
    private Button registered_button;
    private ImageButton next;
    RelativeLayout vislay1;
    RelativeLayout invislay1;

    /* SharedPreference 관련 변수 */
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /* 서버 관련 Asynctask */
    AsyncTask<String, Void, String> httpPostTask;

    /* 지역 변수 및 상수 */
    String cur_delivery;
    DBhelp dbhelp;
    private String ANDROID_ID;
    private String map_id;


    ListView kidListView;
    KidAdapter kidAdapter = new KidAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mainContext = this;
        dbhelp = new DBhelp(getApplicationContext(),"CHILD3.db",null,1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //SlidingView sv = new SlidingView(this, a);
        //Toast.makeText(getApplicationContext(),""+cur_temp,Toast.LENGTH_LONG).show();
        //View v1 = View.inflate(this, R.layout.activity_loading, null);
        //View v2 = View.inflate(this, R.layout.finding_kid_location, null);
        //sv.addView(v1);
        //sv.addView(v2);
        //setContentView(sv);
        setContentView(R.layout.activity_main);

        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        /* Listview */
        kidListView = (ListView) findViewById(R.id.kidListView);
        kidListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //예시
        initView(dbhelp);
        kidListView.setAdapter(kidAdapter);

        /* 버튼 등록 */
        register_button = (Button) findViewById(R.id.register);
        registered_button = (Button) findViewById(R.id.registered);
        registered_view = (ListView) findViewById(R.id.registered_child_list);
        delete_button = (Button)findViewById(R.id.delete);
        next = (ImageButton)findViewById(R.id.next);
        vislay1 = (RelativeLayout) findViewById(R.id.visLay1);
        invislay1 = (RelativeLayout) findViewById(R.id.invisLayout1);


        httpPostTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {

                ServerConnection sc = new ServerConnection();

                String result = sc.CONNECTION("users/"+ ANDROID_ID, kidAdapter.getKids(), ANDROID_ID, sc.MODE_POST);
//                                                sc.CONNECTION("map/" + map_id, null, ANDROID_ID);
                return result;
            }
        };


        /* findind_kid_location 액티비티로 넘기는 버튼 */
        next.setOnClickListener(new ImageButton.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {

                                        httpPostTask.execute();
                                        try {
                                            Toast.makeText(getApplicationContext(), ""+ httpPostTask.get() , Toast.LENGTH_LONG).show();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }

                                        if(!httpPostTask.isCancelled()){
                                            httpPostTask.cancel(true);
                                        }

                                        // 현재 사용상태 저장하는 sharedpreference 호출
                                        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);

                                        // 현재 사용상태 사용으로 변경 후 commit
                                        editor = sp.edit();
                                        editor.putBoolean("isconnected", true);
                                        editor.commit();

                                        Intent_To_Finding_Kid_Location = new Intent(mContext,Finding_Kid_Location_Activity.class);
                                        Intent_To_Finding_Kid_Location.putExtra("String",cur_delivery);
                                        Toast.makeText(getApplicationContext(),"cur: "+ cur_delivery,Toast.LENGTH_LONG);
                                        startActivityForResult(Intent_To_Finding_Kid_Location,0);
                                        finish();
                                    }
                                });
        register_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent_To_User_Home_Activity = new Intent(mContext, User_Home_Activity.class);
                startActivityForResult(Intent_To_User_Home_Activity,0);

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
                        Kid kid = (Kid) kidAdapter.getItem(check.keyAt(i));
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
                dbhelp.insert(dateResult, name, tag);
                /*
                String result = dbhelp.getResult();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();*/
                Bitmap photo = (Bitmap) data.getParcelableExtra("Photo");
                Kid kid = new Kid(name, tag, photo);
                kidAdapter.addItem(kid);
                kidAdapter.notifyDataSetChanged();
                break;
            case 2:
                Toast.makeText(getApplicationContext(),"정상적으로 값을 받았습니다.",Toast.LENGTH_SHORT).show();
                cur_delivery = data.getStringExtra("loc_name");
                Toast.makeText(getApplicationContext(),cur_delivery,Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getApplicationContext(),data.getStringExtra("Name"),Toast.LENGTH_SHORT).show();
                String cur_delivered = data.getStringExtra("Name");
            default:

                break;

        }
    }

    public void initView(DBhelp dbhelp) {
        ArrayList<String> names = dbhelp.getItemArray();
        ArrayList<String> tags = dbhelp.getTagArray();
        int count = names.size();
        for (int i = 0; i < count; i++) {
            Kid kid = new Kid(names.get(i), tags.get(i));
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
}

