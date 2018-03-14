package com.apps.yecotec.fridgetracker.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.data.FoodContract;
import com.apps.yecotec.fridgetracker.data.FoodDBOpenHelper;
import com.apps.yecotec.fridgetracker.data.Section;
import com.apps.yecotec.fridgetracker.data.SectionContract;
import com.apps.yecotec.fridgetracker.data.SectionsDBOpenHelper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kenruizinoue on 9/11/17.
 */

public class DBUtils {

    //for logging purpose
    String TAG = this.getClass().getSimpleName();

    private NotifyInterfaceUtils notifyInterfaceUtils;//for updating the ui properly through this interface
    private Context context;

    public DBUtils(Context context) {
        //get the context for using the content resolver
        this.context = context;
    }

    //constructor for updating the ui from this class
    public DBUtils(Context context, NotifyInterfaceUtils notifyInterfaceUtils) {
        this.context = context;
        this.notifyInterfaceUtils = notifyInterfaceUtils;
    }

    public boolean thereAreRecords(){
        Cursor cursor = getAllSections();

        if(cursor.getCount() == 0) {
            return false;
        }

        return true;
    }

    public Cursor getAllSections() {

        return context.getContentResolver().query(SectionContract.SectionEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

    }

    public Section[] getAllSectionsArray(){

        List<Section> sectionList = new ArrayList<>();

        Cursor sectionsCursor = getAllSections();

        int idCol = sectionsCursor.getColumnIndex(SectionContract.SectionEntry._ID);
        int nameCol = sectionsCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_NAME);
        int colorCol = sectionsCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_COLOR);

        while (sectionsCursor.moveToNext()) {
            sectionList.add(new Section(
                    sectionsCursor.getInt(idCol),
                    sectionsCursor.getString(nameCol),
                    sectionsCursor.getInt(colorCol)
            ));
        }

        return sectionList.toArray(new Section[sectionList.size()]);
    }

    public Cursor getAllFoods() {

        return context.getContentResolver().query(FoodContract.FoodEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    public Food[] getAllFoodsArray(){

        List<Food> foodList = new ArrayList<>();

        Cursor foodsCursor = getAllFoods();

        int idCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry._ID);
        int nameCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_NAME);
        int unitCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_UNIT);
        int quantityCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY);
        int categoryCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY);
        int registeredTimestampCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP);
        int expireDateCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE);
        int sectionIdCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_SECTION_ID);

        while (foodsCursor.moveToNext()) {
            foodList.add(new Food(
                    foodsCursor.getInt(idCol),
                    foodsCursor.getString(nameCol),
                    foodsCursor.getInt(unitCol),
                    foodsCursor.getDouble(quantityCol),
                    foodsCursor.getInt(categoryCol),
                    foodsCursor.getString(registeredTimestampCol),
                    foodsCursor.getString(expireDateCol),
                    foodsCursor.getInt(sectionIdCol)
            ));
        }

        return foodList.toArray(new Food[foodList.size()]);
    }

    public Food[] getFoodsOfSection(int sectionId) {
        List<Food> foodList = new ArrayList<>();
        Food[] foods = getAllFoodsArray();

        for (Food food: foods) {
            if(food.sectionId == sectionId) foodList.add(food);
        }

        return foodList.toArray(new Food[foodList.size()]);
    }

    public Food[] queryFoods(String queryString) {

        List<Food> foodList = new ArrayList<>();

        Cursor foodsCursor = context.getContentResolver().query(FoodContract.FoodEntry.CONTENT_URI,
                null,
                "foodname like ?",
                new String[]{"%" + queryString + "%"},
                null);

        int idCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry._ID);
        int nameCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_NAME);
        int unitCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_UNIT);
        int quantityCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY);
        int categoryCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY);
        int registeredTimestampCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP);
        int expireDateCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE);
        int sectionIdCol = foodsCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_SECTION_ID);

        while (foodsCursor.moveToNext()) {
            foodList.add(new Food(
                    foodsCursor.getInt(idCol),
                    foodsCursor.getString(nameCol),
                    foodsCursor.getInt(unitCol),
                    foodsCursor.getDouble(quantityCol),
                    foodsCursor.getInt(categoryCol),
                    foodsCursor.getString(registeredTimestampCol),
                    foodsCursor.getString(expireDateCol),
                    foodsCursor.getInt(sectionIdCol)
            ));
        }

        return foodList.toArray(new Food[foodList.size()]);
    }

    public void insertSectionIntoDB(String name, int color) {
        //create a ContentValues instance to pass the values into the insert query
        ContentValues cv = new ContentValues();
        cv.put(SectionContract.SectionEntry.COLUMN_SECTION_NAME, name);
        cv.put(SectionContract.SectionEntry.COLUMN_SECTION_COLOR, color);

        //call insert to run an insert query on TABLE_NAME with the ContentValues created
        context.getContentResolver().insert(SectionContract.SectionEntry.CONTENT_URI, cv);
    }

    public void insertFoodIntoDB(String foodName, int foodUnit, double foodQuantity, int foodCategory, String foodRegisteredTimestamp, String foodExpireDate, int sectionId) {
        //create a ContentValues instance to pass the values onto the insert query
        ContentValues cv = new ContentValues();
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, foodName);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, foodUnit);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, foodQuantity);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, foodCategory);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP, foodRegisteredTimestamp);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE, foodExpireDate);
        cv.put(FoodContract.FoodEntry.COLUMN_SECTION_ID, sectionId);

        //call insert to run an insert query on TABLE_NAME with the ContentValues created
        context.getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, cv);
    }

    public void insertFoodIntoDB(Food food) {
        //create a ContentValues instance to pass the values onto the insert query
        ContentValues cv = new ContentValues();
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, food.foodName);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, food.foodUnit);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, food.foodQuantity);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, food.foodCategory);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP, food.foodRegisteredTimestamp);
        cv.put(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE, food.foodExpireDate);
        cv.put(FoodContract.FoodEntry.COLUMN_SECTION_ID, food.sectionId);

        //call insert to run an insert query on TABLE_NAME with the ContentValues created
        context.getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, cv);

        //notify to MainActivity to update the ui if there are a instance of NotifyInterfaceUtils
        if(notifyInterfaceUtils != null) notifyInterfaceUtils.updateUi();
    }

    public void insertSampleDataSet() {
        //create a ContentValues instance to pass the sample section
        ContentValues cvSection = new ContentValues();
        cvSection.put(SectionContract.SectionEntry.COLUMN_SECTION_NAME, context.getResources().getString(R.string.sample_section));
        cvSection.put(SectionContract.SectionEntry.COLUMN_SECTION_COLOR, ContextCompat.getColor(context, R.color.colorOption1));

        Uri uri = context.getContentResolver().insert(SectionContract.SectionEntry.CONTENT_URI, cvSection);

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

        String foodRegisteredTimestamp = timeStamp + "";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);//add 7 days
        date = c.getTime();

        ContentValues cvFood = new ContentValues();

        //sample food 1
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, context.getResources().getString(R.string.sample_food_1));
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, 0);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, 2);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, 1);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP, foodRegisteredTimestamp);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE, dateFormat.format(date));
        cvFood.put(FoodContract.FoodEntry.COLUMN_SECTION_ID, uri.getPathSegments().get(1));//get the id of the added section

        context.getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, cvFood);

        //sample food 2, only change the food related attributes
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, context.getResources().getString(R.string.sample_food_2));
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, 0);//piece
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, 1);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, 0);//fruit

        context.getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, cvFood);

        //sample food 3, only change the food related attributes
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, context.getResources().getString(R.string.sample_food_3));
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, 0);//piece
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, 1);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, 7);//frozen

        context.getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, cvFood);

        //sample food 4, only change the food related attributes
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, context.getResources().getString(R.string.sample_food_4));
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, 2);//pound
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, 0.5);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, 2);//meat

        context.getContentResolver().insert(FoodContract.FoodEntry.CONTENT_URI, cvFood);

        //notify to MainActivity to update the ui
        notifyInterfaceUtils.updateUi();
    }

    public Food getFoodById(int foodId) {

        String stringFoodId = Integer.toString(foodId);

        Uri uriSingleQuery = FoodContract.FoodEntry.CONTENT_URI;
        uriSingleQuery = uriSingleQuery.buildUpon().appendPath(stringFoodId).build();

        Cursor foodCursor = context.getContentResolver().query(uriSingleQuery, null, null, null, null);
        foodCursor.moveToFirst();

        int idCol = foodCursor.getColumnIndex(FoodContract.FoodEntry._ID);
        int nameCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_NAME);
        int unitCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_UNIT);
        int quantityCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY);
        int categoryCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY);
        int registeredTimestampCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP);
        int expireDateCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE);
        int sectionIdCol = foodCursor.getColumnIndex(FoodContract.FoodEntry.COLUMN_SECTION_ID);

        Food food = new Food(
                foodCursor.getInt(idCol),
                foodCursor.getString(nameCol),
                foodCursor.getInt(unitCol),
                foodCursor.getDouble(quantityCol),
                foodCursor.getInt(categoryCol),
                foodCursor.getString(registeredTimestampCol),
                foodCursor.getString(expireDateCol),
                foodCursor.getInt(sectionIdCol)
        );

        return food;
    }

    public Section getSectionById(int sectionId) {

        String stringSectionId = Integer.toString(sectionId);

        Uri uriSingleQuery = SectionContract.SectionEntry.CONTENT_URI;
        uriSingleQuery = uriSingleQuery.buildUpon().appendPath(stringSectionId).build();

        Cursor sectionCursor = context.getContentResolver().query(uriSingleQuery, null, null, null, null);
        sectionCursor.moveToFirst();

        int idCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry._ID);
        int nameCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_NAME);
        int colorCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_COLOR);

        Section section = new Section(
                sectionCursor.getInt(idCol),
                sectionCursor.getString(nameCol),
                sectionCursor.getInt(colorCol)
        );

        return section;
    }

    public void deleteSectionById(int sectionId) {
        String stringId = Integer.toString(sectionId);
        Uri uri = SectionContract.SectionEntry.CONTENT_URI; //create uri with id reference
        uri = uri.buildUpon().appendPath(stringId).build();

        //delete a single row of data using a ContentResolver
        context.getContentResolver().delete(uri, null, null);

        //notify to MainActivity to update the ui if there are a instance of NotifyInterfaceUtils
        if(notifyInterfaceUtils != null) notifyInterfaceUtils.updateUi();
    }

    public void deleteAllFoodsInsideSection(int sectionId) {

        Food[] foods = getAllFoodsArray();

        //delete all food that correspond to passed section id
        for (int i = 0; i < foods.length; i++) {
            if(foods[i].sectionId == sectionId) {
                deleteFoodById(foods[i].foodId);
            }
        }

        //notify to MainActivity to update the ui if there are a instance of NotifyInterfaceUtils
        if(notifyInterfaceUtils != null) notifyInterfaceUtils.updateUi();
    }

    public void deleteFoodById(int foodId) {
        String stringId = Integer.toString(foodId);
        Uri uri = FoodContract.FoodEntry.CONTENT_URI; //create uri with id reference
        uri = uri.buildUpon().appendPath(stringId).build();

        //delete a single row of data using a ContentResolver
        context.getContentResolver().delete(uri, null, null);

        //notify to MainActivity to update the ui if there are a instance of NotifyInterfaceUtils
        if(notifyInterfaceUtils != null) notifyInterfaceUtils.updateUi();
    }

    public void updateFood(Food foodToUpdate) {

        String stringFoodId = Integer.toString(foodToUpdate.foodId);

        Uri uriUpdate = FoodContract.FoodEntry.CONTENT_URI;
        uriUpdate = uriUpdate.buildUpon().appendPath(stringFoodId).build();

        ContentValues cvFood = new ContentValues();

        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_NAME, foodToUpdate.foodName);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_UNIT, foodToUpdate.foodUnit);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_QUANTITY, foodToUpdate.foodQuantity);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_CATEGORY, foodToUpdate.foodCategory);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_REGISTERED_TIMESTAMP, foodToUpdate.foodRegisteredTimestamp);
        cvFood.put(FoodContract.FoodEntry.COLUMN_FOOD_EXPIRE_DATE, foodToUpdate.foodExpireDate);
        cvFood.put(FoodContract.FoodEntry.COLUMN_SECTION_ID, foodToUpdate.sectionId);//get the id of the added section

        context.getContentResolver().update(uriUpdate, cvFood, null, null);
    }

    public void updateSection(Section sectionToUpdate) {

        String stringSectionId = Integer.toString(sectionToUpdate.sectionId);

        Uri uriUpdate = SectionContract.SectionEntry.CONTENT_URI;
        uriUpdate = uriUpdate.buildUpon().appendPath(stringSectionId).build();

        ContentValues cvSection = new ContentValues();

        cvSection.put(SectionContract.SectionEntry.COLUMN_SECTION_NAME, sectionToUpdate.sectionName);
        cvSection.put(SectionContract.SectionEntry.COLUMN_SECTION_COLOR, sectionToUpdate.sectionColor);

        context.getContentResolver().update(uriUpdate, cvSection, null, null);
    }


}
