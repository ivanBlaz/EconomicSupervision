package com.devivan.economicsupervision.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnection extends SQLiteOpenHelper {

    public SQLiteConnection(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteUtil.CREATE_TABLE_ACCOUNTS);
        db.execSQL(SQLiteUtil.CREATE_TABLE_MOVEMENTS);
        db.execSQL(SQLiteUtil.CREATE_TABLE_CONCEPTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQLiteUtil.DROP_TABLE_ACCOUNTS);
        db.execSQL(SQLiteUtil.DROP_TABLE_MOVEMENTS);
        db.execSQL(SQLiteUtil.DROP_TABLE_CONCEPTS);
        //
        db.execSQL(SQLiteUtil.CREATE_TABLE_ACCOUNTS);
        db.execSQL(SQLiteUtil.CREATE_TABLE_MOVEMENTS);
        db.execSQL(SQLiteUtil.CREATE_TABLE_CONCEPTS);
    }
}
