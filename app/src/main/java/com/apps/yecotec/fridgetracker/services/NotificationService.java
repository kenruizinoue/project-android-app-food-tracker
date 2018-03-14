package com.apps.yecotec.fridgetracker.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.apps.yecotec.fridgetracker.DetailActivity;
import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.utils.DBUtils;
import com.apps.yecotec.fridgetracker.utils.FoodUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.apps.yecotec.fridgetracker.SectionAdapter.FOOD_ID;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.EXCEPTION_EXTRA;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_DELIVERED_DAY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_DELIVERED_TODAY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_PERIODICITY_KEY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.SEND_NOTIFICATION_KEY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.VIBRATE_WITH_NOTIFICATION_KEY;


public class NotificationService extends IntentService {

    //for logging purpose
    String TAG = this.getClass().getSimpleName();

    public static final String ACTION_CREATE_NOTIFICATION = "com.apps.yecotec.fridgetracker.services.action.create_notification";

    public NotificationService() {
        super("NotificationService");
    }

    public static void startActionCreateNotification(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_CREATE_NOTIFICATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CREATE_NOTIFICATION.equals(action)) {
                handleActionCreateNotification();
            }
        }
    }

    private void handleActionCreateNotification() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        Food[] foods = new DBUtils(this).getAllFoodsArray();


        //create notification for each food that is about to expire
        for (int i = 0; i < foods.length; i++) {

            if(FoodUtils.foodAboutToExpire(foods[i].foodExpireDate, this)) {//get x from settings

                Log.d(TAG, "true for: " + foods[i].foodName);

                NotificationCompat.Builder notificationBuilder;


                if(getVibrationSetting()) {//vibrate at receiving notification
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setColor(ContextCompat.getColor(this, R.color.customPrimary))// - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
                            .setSmallIcon(FoodUtils.getFoodAsset(foods[i].foodCategory))// -  small icon
                            .setLargeIcon(largeIcon(this, foods[i].foodCategory))// - uses icon returned by the largeIcon helper method as the large icon
                            .setContentTitle(this.getResources().getString(R.string.notification_first_text))// - sets the title
                            .setContentText(foods[i].foodName + " " +this.getResources().getString(R.string.notification_second_text))// - sets the text
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(foods[i].foodName + " " +this.getResources().getString(R.string.notification_second_text)))// - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                            .setDefaults(Notification.DEFAULT_VIBRATE)// - sets the notification defaults to vibrate
                            .setContentIntent(contentIntent(this, foods[i].foodId))// - uses the content intent returned by the contentIntent helper method for the contentIntent
                            .setAutoCancel(true);// - automatically cancels the notification when the notification is clicked
                } else {// not vibrating
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setColor(ContextCompat.getColor(this, R.color.customPrimary))// - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
                            .setSmallIcon(FoodUtils.getFoodAsset(foods[i].foodCategory))
                            .setLargeIcon(largeIcon(this, foods[i].foodCategory))// - uses icon returned by the largeIcon helper method as the large icon
                            .setContentTitle(this.getResources().getString(R.string.notification_first_text))// - sets the title
                            .setContentText(foods[i].foodName + " " + this.getResources().getString(R.string.notification_second_text))// - sets the text
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(foods[i].foodName + " " +this.getResources().getString(R.string.notification_second_text)))// - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                            .setContentIntent(contentIntent(this, foods[i].foodId))// - uses the content intent returned by the contentIntent helper method for the contentIntent
                            .setAutoCancel(true);// - automatically cancels the notification when the notification is clicked
                }

                // if the build version is greater than JELLY_BEAN, set the notification's priority
                // to PRIORITY_HIGH.
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
                }

                //get a NotificationManager
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                //trigger the notification by calling notify on the NotificationManager.
                //pass in a unique ID of your choosing for the notification and notificationBuilder.build

                if(sharedPreferences.getBoolean(SEND_NOTIFICATION_KEY, false)) notificationManager.notify(foods[i].foodId, notificationBuilder.build());

            }
        }

        //mark as delivered for preventing multiple instances of alarms
        //Log.d(TAG, "Mark notification as delivered today");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_DELIVERED_TODAY, true);
        editor.putInt(NOTIFICATION_DELIVERED_DAY, new Date().getDate());
        editor.commit();

    }

    private boolean getVibrationSetting() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPreferences.getBoolean(VIBRATE_WITH_NOTIFICATION_KEY, false);
    }

    // helper method called that takes in a Context and foodCategory as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context, int foodCategory) {
        // Get a Resources object from the context.
        Resources res = context.getResources();

        // Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_android
        Bitmap largeIcon = BitmapFactory.decodeResource(res, FoodUtils.getFoodAsset(foodCategory));
        return largeIcon;
    }

    private static PendingIntent contentIntent(Context context, int foodId) {

        //Create an intent that opens up the DetailActivity
        Intent startActivityIntent = new Intent(context, DetailActivity.class);
        startActivityIntent.putExtra(FOOD_ID, foodId);
        startActivityIntent.putExtra(EXCEPTION_EXTRA, true);//handle special intent case at DetailActivity

        //Create a PendingIntent using getActivity that:
        // - Take the context passed in as a parameter
        // - Takes an unique integer ID for the pending intent (you can create a constant for
        //   this integer above
        // - Takes the intent to open the DetailActivity you just created; this is what is triggered
        //   when the notification is triggered
        // - Has the flag FLAG_UPDATE_CURRENT, so that if the intent is created again, keep the
        // intent but update the data
        return PendingIntent.getActivity(
                context,
                foodId,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
