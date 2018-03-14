package com.apps.yecotec.fridgetracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kenruizinoue on 9/10/17.
 */

public class SectionsDBOpenHelper extends SQLiteOpenHelper {

    //specify the database local file name
    private static final String DATABASE_NAME = "sections.db";

    private static final int DATABASE_VERSION = 2;

    public SectionsDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TUTORIALS_TABLE = "CREATE TABLE " +
                SectionContract.SectionEntry.TABLE_NAME + " (" +
                SectionContract.SectionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SectionContract.SectionEntry.COLUMN_SECTION_NAME+ " TEXT NOT NULL," +
                SectionContract.SectionEntry.COLUMN_SECTION_COLOR + " INTEGER NOT NULL" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TUTORIALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SectionContract.SectionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
