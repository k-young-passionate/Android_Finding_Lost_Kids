package com.example.kyshi.finding_lost_kids_application;

import java.io.File;

public class Kid {
    String name;
    File image;

    public Kid() {

    }

    public Kid(String name, File image) {
        this.name = name;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}
