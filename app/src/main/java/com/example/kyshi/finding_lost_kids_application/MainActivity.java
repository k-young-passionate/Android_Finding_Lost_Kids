package com.example.kyshi.finding_lost_kids_application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
    private ListView mListView;
    private ListView registered_view;
    private Button register_button;
    private Button registered_button;
    private Button exit_button;
    private Button search_button;
    private EditText loc_text;
    private TextView cur_text;
    private ImageButton next;

    /* Listview 관련 변수 */
    static final ArrayList<String> registered_child = new ArrayList<>();
    static final ArrayList<String> regi_item = new ArrayList<>();
    static final ArrayList<String> list = new ArrayList<>();

    /* SharedPreference 관련 변수 */
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /* 지역 변수 및 상수 */
    String cur_delivery;
    DBhelp dbhelp;
    ArrayList<Kid> kids;
    private String ANDROID_ID;
    private String map_id;

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

        /* Listview 구현 */
        mListView = (ListView) findViewById(R.id.recent_list);
        //Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_LONG).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        mListView.setAdapter(adapter);
       /* list.add("롯데 월드 잠실점");
        list.add("고양 킨텍스 전시회장");
        list.add("이마트 가양점");
        list.add("홈플러스 잠실점");
        list.add("롯데 백화점 부산서면점");
        list.add("현대백화점 목동점");
*/

        /* 버튼 등록 */
        register_button = (Button) findViewById(R.id.register);
        registered_button = (Button) findViewById(R.id.registered);
        exit_button = (Button) findViewById(R.id.exit);
        search_button = (Button) findViewById(R.id.Search);
        registered_view = (ListView) findViewById(R.id.registered_child_list);
        loc_text = (EditText)findViewById(R.id.loc_text);
        cur_text = (TextView)findViewById(R.id.cur_text);
        next = (ImageButton)findViewById(R.id.next);


        /* findind_kid_location 액티비티로 넘기는 버튼 */
        next.setOnClickListener(new ImageButton.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {
                                        AsyncTask<String, Void, String> httpGetTask = new AsyncTask<String, Void, String>() {
                                            @Override
                                            protected String doInBackground(String... strings) {
                                                ServerConnection sc = new ServerConnection();
/*                                                kids = new ArrayList<Kid>();
                                                kids.add(new Kid("김", "abcde"));
                                                kids.add(new Kid("심", "abcdf"));
                                                kids.add(new Kid("안", "abcdg"));
                                                kids.add(new Kid("전", "abcdh"));*/  // 예시 데이터
                                                String result = sc.CONNECTION("register/"+ANDROID_ID, kids, ANDROID_ID);

//                                                sc.CONNECTION("map/" + map_id, null, ANDROID_ID);
                                                return result;
                                            }
                                        };

                                        httpGetTask.execute();

                                        /*
                                        try {
                                            Toast.makeText(getApplicationContext(), ""+ httpGetTask.get(), Toast.LENGTH_LONG).show();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }*/ // 확인용

                                        if(!httpGetTask.isCancelled()){
                                            httpGetTask.cancel(true);
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
                                    }
                                });
        register_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent_To_User_Home_Activity = new Intent(mContext, User_Home_Activity.class);
                startActivityForResult(Intent_To_User_Home_Activity,0);

            }
        });


        /* 등록된 아이 정보 보기 및 편집 버튼 */
        registered_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Dialog 생성 Dialog 는 팝업창이라고 생각하면됨
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                        MainActivity.this);
                alertBuilder.setIcon(R.drawable.ic_menu_share); // 아이콘 설정
                alertBuilder.setTitle("등록된 아이"); // 타이틀 설정
                ArrayList<String> temp = dbhelp.getResultArray();
                ArrayList<String>ItemTemp = dbhelp.getItemArray();
                registered_child.clear();

                for(int i=0; i<temp.size(); i++)
                {
                    registered_child.add(temp.get(i));
                }
                for(int i=0; i<ItemTemp.size(); i++)
                {
                    regi_item.add(ItemTemp.get(i));
                }
                final ArrayAdapter<String> registered_adapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1, registered_child);
                alertBuilder.setAdapter(registered_adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                            }
                        });

                //버튼 설정 부분
                alertBuilder.setNegativeButton("나가기",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                alertBuilder.setPositiveButton("편집", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder innBuilder = new AlertDialog.Builder(
                                MainActivity.this);
                        innBuilder.setIcon(R.drawable.ic_menu_share); // 아이콘 설정
                        innBuilder.setTitle("삭제 할 항목을 선택하세요"); // 타이틀 설정


                        String[] child= (String[])registered_child.toArray(new String[registered_child.size()]);

                        final boolean[] checkedItem = new boolean[child.length];
                        innBuilder.setMultiChoiceItems(child,checkedItem,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        Toast.makeText(getBaseContext(),""+checkedItem[which],Toast.LENGTH_SHORT).show();
                                        if(isChecked)
                                            checkedItem[which] = true;
                                    }
                                });
                        innBuilder.setNegativeButton("나가기",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        );
                        innBuilder.setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        for(int i=0; i<checkedItem.length; i++)
                                        {
                                            if(checkedItem[i] == true)
                                            {
                                                Toast.makeText(getBaseContext(),""+checkedItem[i],Toast.LENGTH_SHORT).show();
                                                Toast.makeText(getBaseContext(),""+regi_item.get(i).toString(),Toast.LENGTH_SHORT).show();
                                                dbhelp.delete(regi_item.get(i).toString());
                                            }
                                            else
                                                Toast.makeText(getBaseContext(),""+checkedItem[i],Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                }
                        );

                        innBuilder.show();
                        //adapter setting

                    }
                });
                alertBuilder.show();
            }
        });


        /* 어플리케이션 종료 버튼 */
        exit_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });


        /* 구글 맵 위치 적고 확인 버튼 */
        search_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                //if(cur_text.getText().length()!= 0) {
                if(loc_text.getText().length() != 0) {
                    Intent MAP_INTENT = new Intent(mContext, mapFind.class);
                    MAP_INTENT.putExtra("String", loc_text.getText().toString());
                    startActivityForResult(MAP_INTENT, 0);
                }
                else {
                    Toast.makeText(getApplicationContext(),"위치를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                //}
            }
        });


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
                String name = data.getStringExtra("String");
                String tag = data.getStringExtra("tag");
                dbhelp.insert(dateResult,name,tag);
                String result = dbhelp.getResult();
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(getApplicationContext(),"정상적으로 값을 받았습니다.",Toast.LENGTH_SHORT).show();
                cur_text.setText(data.getStringExtra("loc_name"));
                cur_delivery = data.getStringExtra("loc_name");
                Toast.makeText(getApplicationContext(),cur_delivery,Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getApplicationContext(),data.getStringExtra("String"),Toast.LENGTH_SHORT).show();
                String cur_delivered = data.getStringExtra("String");
                cur_text.setText(cur_delivered);

            default:

                break;

        }
    }
}

