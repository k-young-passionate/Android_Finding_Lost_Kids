package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyshi.finding_lost_kid_application.R;

import java.util.ArrayList;

/**
 * Created by android on 2018-06-01.
 */

public class KidAdapter extends BaseAdapter {
    ArrayList<Kid> kids = new ArrayList<>();
    boolean visMode = false;

    @Override
    public int getCount() {
        return kids.size();
    }

    public void addItem(Kid item) {
        kids.add(item);
    }

    public void removeItem(int i) {
        kids.remove(i);
    }

    public void removeAll() {
        for (int i = 0; i < kids.size(); i++)
            kids.remove(i);
    }

    @Override
    public Object getItem(int position) {
        return kids.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Kid> getKids() {
        return kids;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.kid, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);


        Kid kid = kids.get(position);
        textView.setText(kid.getName());
        textView2.setText(kid.getTag_sn());
        imageView.setImageBitmap(kid.getPhoto());


        if (visMode) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }
}