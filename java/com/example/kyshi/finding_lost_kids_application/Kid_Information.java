package com.example.kyshi.finding_lost_kids_application;

import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class Kid_Information{

    public static ArrayList<Kid> kids;
    public static int numberofkids;

    public Kid_Information(){
        kids = new ArrayList<Kid>();
    }

    public int getter(Kid kid){
        kids.add(kid);
        return 0;
    }

    public int getter(int num){
        this.numberofkids = num;
        return 0;
    }

    public int initiate(){
        kids.clear();
        numberofkids = 0;
        return 0;
    }


}
