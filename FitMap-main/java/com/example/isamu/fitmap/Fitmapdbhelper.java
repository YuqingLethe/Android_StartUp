package com.example.isamu.fitmap;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



import java.util.Calendar;

public class Fitmapdbhelper extends SQLiteOpenHelper{

    private static final String DB_NAME="fitmapdb";
    private static final int DB_VERSION=1;

    public Fitmapdbhelper(Context context){
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("CREATE TABLE TIMEGAP ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ACTIVITY_ID INTEGER,"
                + "START_TIME INTEGER);");

        Calendar cal = Calendar.getInstance();
        long starttime = cal.getTimeInMillis();



        ContentValues activityValues= new ContentValues();
        activityValues.put("ACTIVITY_ID",99);
        activityValues.put("START_TIME",starttime);


        db.insert("TIMEGAP", null, activityValues);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }





}
