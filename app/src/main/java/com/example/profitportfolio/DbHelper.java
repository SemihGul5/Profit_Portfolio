package com.example.profitportfolio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DBNAME="stock.db";
    public static final String TABLENAME="stock";
    public static final int VER=11;
    public DbHelper(@Nullable Context context) {
        super(context, TABLENAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLENAME + "(id integer primary key, name text, pieces REAL, buyDate text, sellDate text, stockPriceBuy REAL, stockPriceSell text, amount REAL, profitAndLoss REAL, komisyon REAL,total REAL, yuzde REAL,ortMaliyet REAL,sellPieces REAL,kalanAdet REAL,satisTutari text,maliyetKomisyon REAL)";
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
