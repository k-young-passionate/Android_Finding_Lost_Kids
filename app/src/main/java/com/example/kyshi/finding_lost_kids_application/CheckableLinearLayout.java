package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.example.kyshi.finding_lost_kid_application.R;

/**
 * Created by android on 2018-06-01.
 */

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    // 만약 CheckBox가 아닌 View를 추가한다면 아래의 변수 사용 가능.
    // private boolean mIsChecked ;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = findViewById(R.id.checkBox);

        return cb.isChecked() ;
    }

    @Override
    public void toggle() {
        CheckBox cb = findViewById(R.id.checkBox);

        setChecked(!cb.isChecked()) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = findViewById(R.id.checkBox);

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }

    }
}
