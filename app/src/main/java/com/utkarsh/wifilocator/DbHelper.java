package com.utkarsh.wifilocator;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_Name = "WIFI_INFO.db";
    public static final String TB_Name = "FINGERPRINT";

    public static final String COL_1 = "RPID";
    public static final String COL_2 = "RSSI_1";
    public static final String COL_3 = "RSSI_2";
    public static final String COL_4 = "RSSI_3";
    public static final String COL_5 = "X_Cod";
    public static final String COL_6 = "Y_Cod";

    private static final int version = 1;

    public DbHelper(Context context) {
        super(context, DB_Name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db = this.getWritableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS FINGERPRINT (" +
                        "    id int," +
                        "    rssi1 int," +
                        "    rssi2 int," +
                        "    rssi3 int," +
                        "    x int," +
                        "    y int" +
                        ");"
        );    }

    public boolean insertData(String RPID, String RSSI_1, String RSSI_2, String RSSI_3, String X, String Y) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, RPID);
        contentValues.put(COL_2, RSSI_1);
        contentValues.put(COL_3, RSSI_2);
        contentValues.put(COL_4, RSSI_3);
        contentValues.put(COL_5, X);
        contentValues.put(COL_6, Y);

        long result = db.insert(TB_Name, null, contentValues);

        if (result == -1)
            return false;
        else return true;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db = this.getWritableDatabase();

    }
}