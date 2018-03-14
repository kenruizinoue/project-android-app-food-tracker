package com.apps.yecotec.fridgetracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by kenruizinoue on 9/11/17.
 */

public class FridgeContentProvider extends ContentProvider {

    private SectionsDBOpenHelper sectionsDBOpenHelper;
    private FoodDBOpenHelper foodDBOpenHelper;

    public static final int SECTIONS = 100;
    public static final int SECTION_WITH_ID = 101;

    public static final int FOODS = 200;
    public static final int FOOD_WITH_ID = 201;

    public static final UriMatcher sUriMatcher = buildUriMatcher();


    @Override
    public boolean onCreate() {
        Context context = getContext();
        sectionsDBOpenHelper = new SectionsDBOpenHelper(context);
        foodDBOpenHelper = new FoodDBOpenHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get access to the databases
        final SQLiteDatabase sectionDB = sectionsDBOpenHelper.getWritableDatabase();
        final SQLiteDatabase foodDB = foodDBOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        String id;
        String mSelection;
        String[] mSelectionArgs;

        Cursor retCursor;

        switch (match) {

            //query all sections
            case SECTIONS:
                retCursor = sectionDB.query(SectionContract.SectionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            //case to query for a single row of data by id
            case SECTION_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection ="_id=?";
                mSelectionArgs = new String[]{id};

                retCursor = sectionDB.query(SectionContract.SectionEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            //query all foods
            case FOODS:
                retCursor = foodDB.query(FoodContract.FoodEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            //case to query for a single row of data by id
            case FOOD_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection ="_id=?";
                mSelectionArgs = new String[]{id};

                retCursor = foodDB.query(FoodContract.FoodEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unkwon uri" + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //Get access to the databases
        final SQLiteDatabase sectionDB = sectionsDBOpenHelper.getWritableDatabase();
        final SQLiteDatabase foodDB = foodDBOpenHelper.getWritableDatabase();

        //Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);

        //Insert new values into the database
        //Set the value for the returnedUri and write the default case for unknown URI's
        Uri returnUri;

        long id;

        switch (match) {

            case SECTIONS:
                id = sectionDB.insert(SectionContract.SectionEntry.TABLE_NAME, null, contentValues);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(SectionContract.SectionEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case FOODS:
                id = foodDB.insert(FoodContract.FoodEntry.TABLE_NAME, null, contentValues);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(FoodContract.FoodEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);

        }

        //Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        //Get access to the databases
        final SQLiteDatabase sectionDB = sectionsDBOpenHelper.getWritableDatabase();
        final SQLiteDatabase foodDB = foodDBOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks

        int itemsDeleted; // starts as 0
        String id;

        switch (match) {

            // Handle the single item case, recognized by the ID included in the URI path
            case SECTION_WITH_ID://delete single food item
                // Get the task ID from the URI path
                id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                itemsDeleted = sectionDB.delete(SectionContract.SectionEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;

            case FOOD_WITH_ID://delete single food item
                id = uri.getPathSegments().get(1);
                itemsDeleted = foodDB.delete(FoodContract.FoodEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;

            case FOODS://delete all food
            /*
            * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
            * deleted. However, if we do pass null and delete all of the rows in the table, we won't
            * know how many rows were deleted. According to the documentation for SQLiteDatabase,
            * passing "1" for the selection will delete all rows and return the number of rows
            * deleted, which is what the caller of this method expects.
            */
                if (null == selection) selection = "1";
                itemsDeleted = foodDB.delete(FoodContract.FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Notify the resolver of a change and return the number of items deleted
        if (itemsDeleted != 0) {
            //food was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of foods deleted
        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase foodDB = foodDBOpenHelper.getWritableDatabase();
        final SQLiteDatabase sectionDB = sectionsDBOpenHelper.getWritableDatabase();

        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sUriMatcher.match(uri);

        String id;

        switch (match) {

            case FOOD_WITH_ID:
                //update a single food by getting the id
                id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = foodDB.update(FoodContract.FoodEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;

            case SECTION_WITH_ID:
                //update a single section by getting the id
                id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = sectionDB.update(SectionContract.SectionEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(SectionContract.AUTHORITY, SectionContract.PATH_SECTION, SECTIONS);
        uriMatcher.addURI(SectionContract.AUTHORITY, SectionContract.PATH_SECTION + "/#", SECTION_WITH_ID);

        uriMatcher.addURI(FoodContract.AUTHORITY, FoodContract.PATH_FOOD, FOODS);
        uriMatcher.addURI(FoodContract.AUTHORITY, FoodContract.PATH_FOOD + "/#", FOOD_WITH_ID);

        return uriMatcher;
    }
}
