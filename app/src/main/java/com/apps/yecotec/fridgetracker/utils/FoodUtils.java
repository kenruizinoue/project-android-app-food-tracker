package com.apps.yecotec.fridgetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.data.Food;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_PERIODICITY_KEY;

/**
 * Created by kenruizinoue on 9/12/17.
 */

public class FoodUtils {

    Context context;

    public FoodUtils(Context context) {
        this.context = context;
    }

    public String getUnitString(int unitId) {
        switch (unitId) {
            case 0:
                return context.getResources().getString(R.string.unit_piece);

            case 1:
                return context.getResources().getString(R.string.unit_ounce);

            case 2:
                return context.getResources().getString(R.string.unit_pound);

            default:
                return null;
        }
    }

    public static int getFoodAsset(int categoryId) {
        switch (categoryId) {
            case 0:
                return R.drawable.ic_food_0;

            case 1:
                return R.drawable.ic_food_1;

            case 2:
                return R.drawable.ic_food_2;

            case 3:
                return R.drawable.ic_food_3;

            case 4:
                return R.drawable.ic_food_4;

            case 5:
                return R.drawable.ic_food_5;

            case 6:
                return R.drawable.ic_food_6;

            case 7:
                return R.drawable.ic_food_7;

            case 8:
                return R.drawable.ic_food_8;

            case 9:
                return R.drawable.ic_food_9;

            case 10:
                return R.drawable.ic_food_10;

            default:
                return R.drawable.ic_food_10;
        }
    }

    public String getCategoryName(int categoryId) {
        switch (categoryId) {
            case 0:
                return context.getResources().getString(R.string.food_fruit);

            case 1:
                return context.getResources().getString(R.string.food_vegetable);

            case 2:
                return context.getResources().getString(R.string.food_meat);

            case 3:
                return context.getResources().getString(R.string.food_sausage);

            case 4:
                return context.getResources().getString(R.string.food_beverage);

            case 5:
                return context.getResources().getString(R.string.food_dairy);

            case 6:
                return context.getResources().getString(R.string.food_sauce);

            case 7:
                return context.getResources().getString(R.string.food_frozen);

            case 8:
                return context.getResources().getString(R.string.food_instant);

            case 9:
                return context.getResources().getString(R.string.food_snack);

            case 10:
                return context.getResources().getString(R.string.food_other);

            default:
                return context.getResources().getString(R.string.food_other);
        }
    }

    public static Food[] sortByExpirationDate(Context context) {
        Food[] foods = new DBUtils(context).getAllFoodsArray();

        List<Food> foodList = new ArrayList<>(Arrays.asList(foods));

        Collections.sort(foodList, new Comparator<Food>() {
            public int compare(Food f1, Food f2) {
                return f1.foodExpireDate.compareTo(f2.foodExpireDate);
            }
        });

        foods = foodList.toArray(new Food[foodList.size()]);

        return foods;
    }

    public static Food[] sortByAscending(Context context) {
        Food[] foods = new DBUtils(context).getAllFoodsArray();

        List<Food> foodList = new ArrayList<>(Arrays.asList(foods));

        if (foodList.size() > 0) {
            Collections.sort(foodList, new Comparator<Food>() {
                @Override
                public int compare(final Food food1, final Food food2) {
                    return food1.foodName.compareTo(food2.foodName);
                }
            });
        }

        foods = foodList.toArray(new Food[foodList.size()]);

        return foods;
    }

    public static Food[] sortByDescending(Context context) {
        Food[] foods = new DBUtils(context).getAllFoodsArray();

        List<Food> foodList = new ArrayList<>(Arrays.asList(foods));

        if (foodList.size() > 0) {
            Collections.sort(foodList, new Comparator<Food>() {
                @Override
                public int compare(final Food food1, final Food food2) {
                    return food2.foodName.compareTo(food1.foodName);
                }
            });
        }

        foods = foodList.toArray(new Food[foodList.size()]);

        return foods;
    }

    public static boolean foodAboutToExpire(String expireDateString, Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int periodicity = Integer.parseInt(sharedPreferences.getString(NOTIFICATION_PERIODICITY_KEY, "3"));

        Date currentDate = new java.util.Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Date expireDate = null;

        try {
            expireDate = format.parse(expireDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff;

        try {
            diff = expireDate.getTime() - currentDate.getTime();
        } catch (Exception e) {
            return false;//if the date has not been placed, returned false (without date case)
        }

        int daysLeft =  (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        //compare if there are more than x days with food expiration date, where x is periodicity defined at settings (default is 3 days)
        if(daysLeft < periodicity && daysLeft >= 0) {
            return true;
        } else {
            return false;
        }

    }


}
