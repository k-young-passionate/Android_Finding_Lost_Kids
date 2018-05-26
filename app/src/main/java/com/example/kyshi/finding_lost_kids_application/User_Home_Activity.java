package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

public class User_Home_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent Intent_To_Kid_Photo_Upload_Activity = null;
    private EditText Tag_sn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Intent intent = getIntent();
        //final ArrayList<String> tagList = (ArrayList<String>)intent.getSerializableExtra("ArrayList<String>");
        Intent_To_Kid_Photo_Upload_Activity = new Intent(mContext, Kid_Photo_Upload_Activity.class); // 다음 Activity 로 넘어가기 위한 intent 설정

        /**
         * Layout view 들과 연동
         */

        Button btn = (Button)findViewById(R.id.button);
        Tag_sn = (EditText)findViewById(R.id.editText);


        /**
         * 첫 화면에서 버튼을 눌렀을 때 동작
         */

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    /* 서버 연결 시도 추가 필요
                     * Try Catch 문은 서버 연결 지연이 될 경우를 처리하는 용도임
                     * If 문은 session 코드를 안 넣었은 경우를 확인하는 용도임
                     * */
                    if(Tag_sn.getText().length() != 0) {
                        String tag = Tag_sn.getText().toString();
                        /* 서버에서 태그가 이미 존재하는 것인지 확인

                         */
                        // 태그가 새로운 것이면 앞의 Intent 에 tag 정보를 담아서 다음에 인텐트에 같이 넘긴다.
                        Toast.makeText(getApplicationContext(), "태그 코드: " + Tag_sn.getText(), Toast.LENGTH_SHORT).show();
                        startActivityForResult(Intent_To_Kid_Photo_Upload_Activity,0);
                    } else {
                        Toast.makeText(getApplicationContext(), "태그 코드를 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                } //

            }
        });
        //finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
        // 정상 작동
            case 1:
                String name = data.getStringExtra("String");
                Intent intent = new Intent();
                intent.putExtra("String",name);
                intent.putExtra("tag",Tag_sn.getText().toString());
                setResult(1,intent);
                finish();
                break;


            default:
                break;

        }
    }
}
