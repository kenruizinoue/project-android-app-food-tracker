package com.apps.yecotec.fridgetracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kenruizinoue on 9/11/17.
 */

public class FoodDBOpenHelper extends SQLiteOpenHelper {
    //specify the database local file name
    private static final String DATABASE_NAME = "foods.db";

    private static final int DATABASE_VERSION = 3;

    public FoodDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TUTORIALS_TABLE = "CREATE TABLE " +
                FoodContract.FoodEntry.TABLE_NAME + " (" +
                FoodContract.FoodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FoodContract.FoodEntry.COLUMN_FOOD_NAME+ " TEXT NOT NULL," +
                FoodContract.FoodEntry.COLUMN_FOOD_UNIT + " INTEGER NOT NULL," +
                FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY + " REAL NOT NULL," +
                FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY + " INTEGER NOT NULL," +
                FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP + " TEXT NOT NULL," +
                FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE + " TEXT NOT NULL," +
                FoodContract.FoodEntry.COLUMN_SECTION_ID + " INTEGER NOT NULL" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TUTORIALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FoodContract.FoodEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
