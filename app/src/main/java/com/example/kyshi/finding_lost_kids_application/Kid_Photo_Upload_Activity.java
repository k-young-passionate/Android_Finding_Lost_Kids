package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

public class Kid_Photo_Upload_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttofindinglocationactivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intenttofindinglocationactivity = new Intent(mContext, Finding_Kid_Location_Activity.class);
        setContentView(R.layout.kid_photo_upload);

        Button btn = (Button)findViewById(R.id.upload_button);
        ImageView img = (ImageView)findViewById(R.id.kidView);

        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int id_view = v.getId();

                Toast.makeText(getApplicationContext(), "Image Clicked", Toast.LENGTH_LONG).show();

            }


        });

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    /* 이미지 업로드 */
                    if(true) {
                        startActivity(intenttofindinglocationactivity);
                    } else {
                        Toast.makeText(getApplicationContext(), "사진을 업로드해주세요.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
