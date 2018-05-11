package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

public class CountNumber extends AppCompatActivity {

    private Button button;
    private TextView numberview;
    private int numberofkids;
    private Context mContext = this;
    private Intent intenttoHomeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intenttoHomeActivity = new Intent(mContext, User_Home_Activity.class);
        setContentView(R.layout.activity_count_number);

        numberview = (EditText) findViewById(R.id.kidsnumber);
        button = (Button) findViewById(R.id.kidsnumberbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_nv = numberview.getText().toString();
                numberofkids = Integer.parseInt(string_nv);
                Kid_Information ki = new Kid_Information();
                ki.getter(numberofkids);

                startActivity(intenttoHomeActivity);
            }
        });
    }
}
