package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class LocationDB extends SQLiteOpenHelper {
// DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음

    public LocationDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성


        db.execSQL("CREATE TABLE LOCATION (name TEXT, tag TEXT, locationX TEXT, locationY TEXT, locname TEXT, checked TEXT, BYTE map);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        switch (oldVersion) {
            case 1:
                try {
                    db.beginTransaction();
                    db.execSQL("ALTER TABLE LOCATIONX ADD COLUMN BYTE map default NULL");
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                ;
                break;
        }
*/
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public int findnum(String x, String y, String loc) {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION;", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(4).equals("true") && cursor.getString(1).equals(x) && cursor.getString(2).equals(y) && cursor.getString(3).equals(loc))
                result++;
        }  // checked , loc name , x,y 쌍이 일치하면 result 를 1증가.
        db.close();

        return result;
    }

    public int count() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM LOCATION;", null);

        db.close();
        return cursor.getCount();
    }

    public void insert(String name, String tag, String locationX, String locationY, String locname, String checked, byte[] tmap) {
        //SQLiteDatabase db = getWritableDatabase();
        if (search(tag))
            return;  // 이미 tag 존재

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement("INSERT INTO LOCATION VALUES(?,?,?,?,?,?,?);");
        p.bindString(1, name);
        p.bindString(2, tag);
        p.bindString(3, locationX);
        p.bindString(4, locationY);
        p.bindString(5, locname);
        p.bindString(6, checked);
        p.bindBlob(7, tmap);
        p.execute();
        db.close();
    }

    public ArrayList<Boolean> getCheckedArray() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Boolean> checked_List = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            if(cursor.getString(1).equals("true"))
                checked_List.add(true);
            else
                checked_List.add(false);
        }

        return checked_List;
    }

    public ArrayList<String> getResultArray() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> child_List = new ArrayList<>();
        String temp = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            temp += cursor.getString(0);
            child_List.add(temp);
            temp = "";
        }

        return child_List;
    }

    public void updateChecked(String name, String checkedd) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE LOCATION  SET checked='" + checkedd + "'" + " WHERE NAME='" + name + "';");

        db.close();
    }

    public void update(String name, String tag, String locationX, String locationY, String locname, String checked, byte[] tmap) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE LOCATION SET NAME=" + name + " SET map=" + tmap + "  SET LOCATIONX=" + locationX + " SET LOCATIONY=" + locationY + " SET LOCNAME=" + locname + " SET BOOL=" + checked + " WHERE ITEM='" + tag + "';");
        db.close();
    }

    public boolean search(String tag) {
        SQLiteDatabase db = getReadableDatabase();
        String result;
        result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            result = "";
            result += cursor.getString(1);
            if (result.equals(tag)) {
                db.close();
                return true;
            }
        }
        db.close();
        return false;
    }

    public String ssearch() {
        SQLiteDatabase db = getReadableDatabase();
        String result;
        result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(1);


        }
        return result;
    }


    public void delete(String tag) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM LOCATION WHERE tag='" + tag + "';");
        db.close();
    }
    /*public ArrayList<String> getResultArray(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> child_List = new ArrayList<>();
        String temp = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            temp += "이름 :" + cursor.getString(0)+"   "+ "태그 번호 :" + cursor.getString(1);
            child_List.add(temp);
            temp="";
        }

        return child_List;
    }*/

    public ArrayList<String> getTagResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            result.add(cursor.getString(1));
        }

        return result;
    }
    public ArrayList<String> getlocResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            result.add(cursor.getString(4));
        }

        return result;
    }
    public byte[] getmapResult(String tag, byte[] base) {
        SQLiteDatabase db = getReadableDatabase();
        byte[] temp;
        String temptag = "";
        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION", null);
        while (cursor.moveToNext()) {
            temp = cursor.getBlob(6);
            if(cursor.getString(1).equals(tag))
                return temp;
        }
        cursor.moveToNext();
        return base;
    }


    public void deleteall() {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM LOCATION WHERE TAG IS NOT NULL;");
        db.close();
    }

}