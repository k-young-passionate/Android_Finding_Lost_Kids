package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DBhelp extends SQLiteOpenHelper {
// DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음

    public DBhelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 CHILD이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item(이름) 문자열 컬럼, tag 문자열 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE CHILD (name TEXT, tag TEXT primary key, create_at TEXT, photo BLOB);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                try{
                    db.beginTransaction();
                    db.execSQL("ALTER TABLE CHILD ADD COLUMN photo BLOB default NULL");
                    db.setTransactionSuccessful();
                } catch(Exception e){
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                };
                break;
        }

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

/*
    public void insert(String create_at, String name, String tag, byte[] photo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO CHILD VALUES('" +
                name + "', '" +
                tag + "', '" +
                create_at + "','" +
                photo +");");
        db.close();
    }
    */
    public void insert(String create_at, String name, String tag, byte[] photo){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement("INSERT INTO CHILD VALUES(?,?,?,?);");
        p.bindString(1,name);
        p.bindString(2,tag);
        p.bindString(3,create_at);
        p.bindBlob(4,photo);
        p.execute();
    }

    public int findnum() {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CHILD;", null);
        while(cursor.moveToNext()){
            result += cursor.getInt(0);
        }
        db.close();

        return result;
    }


    public void update(String name, String tag) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE CHILD SET TAG=" + tag + " WHERE name='" + name + "';");
        db.close();
    }

    public boolean search(String tag    ) {
        SQLiteDatabase db = getReadableDatabase();
        String result;
        result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(2); // 2번째행 tag 정보
            if (result.equals(tag)) {
                db.close();
                return true;
            }
        }
        db.close();
        return false;
    }

    public void delete(String tag) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CHILD WHERE TAG='" + tag + "';");
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CHILD WHERE TAG IS NOT NULL;");
        db.close();
    }

    public ArrayList<String> getResultArray() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> child_List = new ArrayList<>();
        String temp = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            temp += "이름 :" + cursor.getString(0) + "   " + "태그 번호 :" + cursor.getString(1);
            child_List.add(temp);
            temp = "";
        }

        return child_List;
    }

    public ArrayList<String> getNameArray() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> child_List = new ArrayList<>();
        String temp = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            temp += cursor.getString(0);
            child_List.add(temp);
            temp = "";
        }

        return child_List;
    }
    public ArrayList<String> getTagArray() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> child_List = new ArrayList<>();
        String temp = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            temp += cursor.getString(1);
            child_List.add(temp);
            temp = "";
        }

        return child_List;
    }
    public ArrayList<byte[]> getPhotoArray() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<byte[]> photo_list = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            photo_list.add(cursor.getBlob(3));
        }
        return photo_list;
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + " : "
                    + cursor.getString(1)
                    + " | "
                    + cursor.getString(2)
                    + "\n";
        }

        return result;
    }
}



