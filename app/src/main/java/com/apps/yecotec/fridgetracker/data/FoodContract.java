package com.apps.yecotec.fridgetracker.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kenruizinoue on 9/11/17.
 */

public class FoodContract {

    public static final String AUTHORITY = "com.apps.yecotec.fridgetracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FOOD = "food";

    public static final class FoodEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOOD).build();

        public static final String TABLE_NAME = "foodlist";
        public static final String COLUMN_FOOD_NAME = "foodName";
        public static final String COLUMN_FOOD_UNIT= "foodUnit";
        public static final String COLUMN_FOOD_QUANTITY= "foodQuantity";
        public static final String COLUMN_FOOD_CATEGORY= "foodCategory";
        public static final String COLUMN_FOOD_REGISTERED_TIMESTAMP = "foodRegisteredTimestamp";
        public static final String COLUMN_FOOD_EXPIRE_DATE = "foodExpireDate";
        public static final String COLUMN_SECTION_ID = "sectionId";
    }
}
