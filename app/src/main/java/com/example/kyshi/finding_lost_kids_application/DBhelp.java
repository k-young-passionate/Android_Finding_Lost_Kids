package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBhelp extends SQLiteOpenHelper{
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
        db.execSQL("CREATE TABLE CHILD (item TEXT, tag TEXT, create_at TEXT);");
        }

// DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    public void insert(String create_at, String item, String tag) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO CHILD VALUES('" + item + "', '" + tag + "', '" + create_at + "');");
        db.close();
        }

public void update(String item, String tag) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE CHILD SET TAG=" + tag+ " WHERE item='" + item + "';");
        db.close();
        }
public boolean search(String tag){
    SQLiteDatabase db = getReadableDatabase();
    String result;
    result = "";
    Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
    while (cursor.moveToNext()) {
       result += cursor.getString(2); // 2번째행 tag 정보
       if(result.equals(tag)) {
           db.close();
           return true;
       }
    }
    db.close();
    return false;
}

public void delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CHILD WHERE item='" + item + "';");
        db.close();
        }
public ArrayList<String> getResultArray(){
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
}
    public ArrayList<String> getItemArray(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> child_List = new ArrayList<>();
        String temp = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CHILD", null);
        while (cursor.moveToNext()) {
            temp +=cursor.getString(0);
            child_List.add(temp);
            temp="";
        }

        return child_List;
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



