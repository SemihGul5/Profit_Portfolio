package com.example.profitportfolio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DBNAME="stock.db";
    public static final String TABLENAME="stock";
    public static final int VER=2;
    public DbHelper(@Nullable Context context) {
        super(context, TABLENAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLENAME + "(id integer primary key, name text, pieces text, buyDate text, sellDate text, stockPriceBuy REAL, stockPriceSell REAL, amount REAL, profitAndLoss REAL, komisyon REAL,total REAL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            String sql = "drop table if exists " + TABLENAME + " ";
            db.execSQL(sql);
            onCreate(db);
        }
    }

}
