package com.example.kyshi.finding_lost_kids_application;

import android.graphics.Bitmap;

public class Kid {
    private String name;
    private String tag_sn;
    private Bitmap photo;
    public static int id = 0;
    private String stat1;
    private String stat2;
    private String location;
    private int x;
    private int y;

    public Kid(String name, String tag_sn) {
        this.name = name;
        this.tag_sn = tag_sn;
        this.photo = null;
        this.stat1 = null;
        this.stat2 = null;
        this.x = 0;
        this.y = 0;
        id = this.id++;
    }

    public Kid(String name, String tag_sn, Bitmap photo) {
        this.name = name;
        this.tag_sn = tag_sn;
        this.photo = photo;
        id = this.id++;
    }

    public Kid() {
        id = this.id++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag_sn() {
        return tag_sn;
    }

    public void setTag_sn(String tag_sn) {
        this.tag_sn = tag_sn;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getStat1() {
        return stat1;
    }

    public void setStat1(String stat1) {
        this.stat1 = stat1;
    }

    public String getStat2() {
        return stat2;
    }

    public void setStat2(String stat2) {
        this.stat2 = stat2;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
