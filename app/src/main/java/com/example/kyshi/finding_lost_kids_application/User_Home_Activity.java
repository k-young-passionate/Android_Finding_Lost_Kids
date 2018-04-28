package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

public class User_Home_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttokidphotouploadactivity = null;
    private EditText Tag_sn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        intenttokidphotouploadactivity = new Intent(mContext, Kid_Photo_Upload_Activity.class);
        Button btn = (Button)findViewById(R.id.button);
        Tag_sn = (EditText)findViewById(R.id.editText);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    /* 서버 연결 시도 */
                    if(Tag_sn.getText().length() != 0) {
                        startActivity(intenttokidphotouploadactivity);
                        Toast.makeText(getApplicationContext(), "태그 코드: " + Tag_sn.getText(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "태그 코드를 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
