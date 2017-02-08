package com.jonli.fundkeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Li on 2016/11/21.
 **/

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydata.db";  //資料庫名稱
    private final static int DATABASE_VERSION = 1;  //資料庫版本
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DBHelper",DATABASE_NAME);
        db.execSQL("CREATE TABLE Account( id INT PRIMARY KEY NOT NULL, name VARCHAR(10) NOT NULL, remarks VARCHAR(10), cash VARCHAR NOT NULL);");
        db.execSQL("CREATE TABLE Fund( id INTEGER PRIMARY KEY NOT NULL,name VARCHAR(40) NOT NULL,date DATE NOT NULL,url VARCHAR, unit INT NOT NULL,price INT NOT NULL,free INT,interest INT,A_id INT REFERENCES Account (id) ON DELETE CASCADE ON UPDATE CASCADE);");
        db.execSQL("CREATE TABLE Record( id INTEGER PRIMARY KEY NOT NULL ,date DATE NOT NULL,money VARCHAR,class VARCHAR(20),remarks VARCHAR (30),positive BOOLEAN,A_id INT REFERENCES Account (id) ON DELETE CASCADE ON UPDATE CASCADE,F_id INT REFERENCES Fund (id) ON DELETE CASCADE ON UPDATE CASCADE);");
        db.execSQL("CREATE TABLE InstantNews ( id INTEGER PRIMARY KEY, date  DATE, title VARCHAR, src VARCHAR);");
        db.execSQL("CREATE TABLE FXNews (id INTEGER PRIMARY KEY,date DATE,title VARCHAR,src VARCHAR);");
        db.execSQL("CREATE TABLE FundNews (id INTEGER PRIMARY KEY,date DATE,title VARCHAR,src VARCHAR);");
        db.execSQL("CREATE TABLE StockNews (id INTEGER PRIMARY KEY,date DATE,title VARCHAR,src VARCHAR);");
        db.execSQL("CREATE TABLE List (id INTEGER PRIMARY KEY,name VARCHAR,date VARCHAR,url VARCHAR,month1 VARCHAR,month3 VARCHAR,month6 VARCHAR,year VARCHAR,year1 VARCHAR,year3 VARCHAR,year5 VARCHAR,company INT);");
        db.execSQL("CREATE TABLE FundData(_id INTEGER PRIMARY KEY,name VARCHAR,url VARCHAR,company INT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Account");
        db.execSQL("DROP TABLE IF EXISTS Fund");
        db.execSQL("DROP TABLE IF EXISTS Record");
        onCreate(db);
    }
}
