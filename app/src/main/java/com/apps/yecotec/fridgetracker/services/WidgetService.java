package com.apps.yecotec.fridgetracker.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import com.apps.yecotec.fridgetracker.FoodWidget;
import com.apps.yecotec.fridgetracker.R;


public class WidgetService extends IntentService {

    public static final String ACTION_UPDATE_WIDGETS = "com.example.android.mygarden.action.update_widgets";

    public WidgetService() {
        super("WidgetService");
    }

    /**
     * Starts this service to perform UpdateWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            }
        }
    }

    /**
     * Handle action UpdateWidgets in the provided background thread
     */
    private void handleActionUpdateWidgets() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FoodWidget.class));

        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);

        //Now update all widgets
        FoodWidget.manualUpdate(this, appWidgetManager, appWidgetIds);
    }

}
