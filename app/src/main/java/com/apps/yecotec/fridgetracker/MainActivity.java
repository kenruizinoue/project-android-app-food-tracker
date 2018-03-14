package com.apps.yecotec.fridgetracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.data.Section;
import com.apps.yecotec.fridgetracker.dialogs.AboutUsDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.AddFoodDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.AddSectionDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.ConfirmDeleteSectionDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.EditSectionDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.MoveFoodDialogFragment;
import com.apps.yecotec.fridgetracker.services.NotificationService;
import com.apps.yecotec.fridgetracker.services.WidgetService;
import com.apps.yecotec.fridgetracker.settings.SettingsActivity;
import com.apps.yecotec.fridgetracker.utils.DBUtils;
import com.apps.yecotec.fridgetracker.utils.FoodUtils;
import com.apps.yecotec.fridgetracker.utils.NotifyInterfaceUtils;
import com.apps.yecotec.fridgetracker.utils.SectionUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Date;

import static android.app.SearchManager.QUERY;
import static com.apps.yecotec.fridgetracker.SectionAdapter.FOOD_ID;
import static com.apps.yecotec.fridgetracker.SectionAdapter.REQUEST_FOR_ACTIVITY_CODE;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_DELIVERED_DAY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_DELIVERED_TODAY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.NOTIFICATION_PERIODICITY_KEY;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.TOUR_ACTIVITY_VISITED_KEY;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        NotifyInterfaceUtils,
        EditSectionDialogFragment.MainActivityNotifyInterface,
        ConfirmDeleteSectionDialogFragment.MainActivityNotifyInterface,
        MoveFoodDialogFragment.MainActivityNotifyInterface {

    //for logging purpose
    String TAG = this.getClass().getSimpleName();
    Snackbar mySnackbar;

    //unique ids for creating itemMenu
    private static final int MENU_ITEM_ID = 208;
    private static final int MENU_ITEM_ID_REMOVE = 205;
    private static final int MENU_ITEM_GROUP_ID = 109;

    public static final String DELETED_FOOD = "deletedFood";

    private Boolean isMainFabOpen = false;
    private FloatingActionButton mainFab, addFoodFab,addSectionFab;
    private Animation appear,dissolve, appear_text, dissolve_text, rotate_forward,rotate_backward;
    private TextView mainFabLabel, addFoodFabLabel, addSectionFabLabel;
    private View clickableScreen;

    private int sortOption = 0;//track the state of selected option
    private int sectionFilterId = 0;//track the state of selected sectionId
    private boolean filterOptionActivated = false;//flag that track state of filter

    private String urlAsString = "https://yeco-tec.com/";

    private Window window;

    private NavigationView navigationView;

    private SubMenu filterSectionMenuOption;

    //member variables for recycler view
    SectionAdapter sectionAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        window = this.getWindow();

        //find fabs and set up listeners
        mainFab = (FloatingActionButton) findViewById(R.id.main_fab);
        addFoodFab = (FloatingActionButton) findViewById(R.id.add_food_fab);
        addSectionFab = (FloatingActionButton) findViewById(R.id.add_section_fab);
        mainFab.setOnClickListener(this);
        addFoodFab.setOnClickListener(this);
        addSectionFab.setOnClickListener(this);

        //find labels and set up listeners
        mainFabLabel = (TextView) findViewById(R.id.label_main_fab);
        addFoodFabLabel = (TextView) findViewById(R.id.label_add_food_fab);
        addSectionFabLabel = (TextView) findViewById(R.id.label_add_section_fab);
        mainFabLabel.setOnClickListener(this);
        addFoodFabLabel.setOnClickListener(this);
        addSectionFab.setOnClickListener(this);

        //find clickable screen and set up listener
        clickableScreen = findViewById(R.id.clickable_screen);
        clickableScreen.setOnClickListener(this);

        //bind animation from resources
        //animation for FABs
        appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appear);
        dissolve = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.dissolve);

        //animation for text labels
        appear_text = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appear_text);
        dissolve_text = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.dissolve_text);

        //animation for main fab
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mySnackbar = Snackbar.make(findViewById(R.id.my_coordinator_layout), "", Snackbar.LENGTH_LONG);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //to store a reference to the RecyclerView in mNumbersList
        recyclerView = (RecyclerView) findViewById(R.id.rv_section);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        //to designate that the contents of the RecyclerView won't change an item's size
        recyclerView.setHasFixedSize(true);

        sortOption = 0;//by default has no sort

        sectionAdapter = new SectionAdapter(new DBUtils(this).getAllSectionsArray(), this, sortOption, this, getSupportFragmentManager());

        recyclerView.setAdapter(sectionAdapter);

        updateWidget();

        checkIfSetAlarm();

        //set up banner
        AdView adView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        Intent intent = getIntent();

        //set snackBar if user deleted item
        if(intent.hasExtra(DELETED_FOOD)) {
            final Food deletedFood = intent.getParcelableExtra(DELETED_FOOD);

            final NotifyInterfaceUtils notifyInterfaceUtils = this;
            final Context context = this;

            mySnackbar.setText(this.getResources().getString(R.string.snack_bar_deleted)+ deletedFood.foodName);

            //insert the food again
            mySnackbar.setAction(this.getResources().getString(R.string.snack_bar_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DBUtils(context, notifyInterfaceUtils).insertFoodIntoDB(deletedFood);
                }
            }).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWidget();
        updateUi();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUi();

        if (requestCode == REQUEST_FOR_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK){//user delete the data
                final Food deletedFood = data.getParcelableExtra(DELETED_FOOD);

                final NotifyInterfaceUtils notifyInterfaceUtils = this;
                final Context context = this;

                mySnackbar.setText(this.getResources().getString(R.string.snack_bar_deleted) + deletedFood.foodName);

                //insert the food again
                mySnackbar.setAction(this.getResources().getString(R.string.snack_bar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DBUtils(context, notifyInterfaceUtils).insertFoodIntoDB(deletedFood);
                    }
                }).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        final Context context = this;

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Intent intent = new Intent(context, SearchResultActivity.class);
                        intent.putExtra(QUERY, query);//pass query string
                        context.startActivity(intent);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );

        MenuItem filterMenuItem = menu.getItem(1);//get filter_collection menuItem

        //Menu.NONE -> Value to use for group and item identifier integers when you don't care about them.

        //get subMenu for adding itemMenus dynamically
        filterSectionMenuOption = filterMenuItem.getSubMenu();

        updateFilterSectionOptions();

        return true;
    }

    public void updateFilterSectionOptions() {
        //clear content
        filterSectionMenuOption.clear();

        //get sections array from db
        Section[] sections = new DBUtils(this).getAllSectionsArray();

        //iterate for adding itemMenu
        for (int i = 0; i < sections.length; i++) {
            filterSectionMenuOption.add(MENU_ITEM_GROUP_ID /*group id*/, MENU_ITEM_ID + i/*itemId*/, Menu.NONE /*order*/, sections[i].sectionName /*text*/);
        }

        filterSectionMenuOption.add(MENU_ITEM_GROUP_ID, MENU_ITEM_ID_REMOVE, Menu.NONE, R.string.dynamic_menu_remove_filter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_sort_remove:
                sortOption = 0;
                updateUi(sortOption, sectionFilterId, filterOptionActivated);
                return true;

            case R.id.action_sort_date:
                sortOption = 1;
                updateUi(sortOption, sectionFilterId, filterOptionActivated);
                return true;

            case R.id.action_sort_ascending:
                sortOption = 2;
                updateUi(sortOption, sectionFilterId, filterOptionActivated);
                return true;

            case R.id.action_sort_descending:
                sortOption = 3;
                updateUi(sortOption, sectionFilterId, filterOptionActivated);
                return true;

            case MENU_ITEM_ID_REMOVE:
                filterOptionActivated = false;
                updateUi(sortOption, sectionFilterId, filterOptionActivated);
                return true;
        }

        //get sections array from db
        Section[] sections = new DBUtils(this).getAllSectionsArray();

        for (int i = 0; i < sections.length; i++) {
            if(id == (MENU_ITEM_ID + i)) {
                filterOptionActivated = true;
                sectionFilterId = sections[i].sectionId;
                //update ui with filtered section array
                updateUi(sortOption, sectionFilterId, filterOptionActivated);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_create_samples) {
            //instantiate the class with notifyInterfaceUtils for updating the ui from DBUtils class
            new DBUtils(this, this).insertSampleDataSet();
            //update menu options
            updateFilterSectionOptions();
        } else if (id == R.id.nav_go_to_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_about) {
            DialogFragment newFragment = new AboutUsDialogFragment();
            newFragment.show(getSupportFragmentManager(), "about_us");
        } else if(id == R.id.nav_visit_our_page) {
            Uri webPageUri = Uri.parse(urlAsString);

            Intent intent = new Intent(Intent.ACTION_VIEW, webPageUri);

            //Verify that this Intent can be launched and then call startActivity
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if(id == R.id.nav_tutorial) {
            Intent intent = new Intent(this, TourActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //set false for visiting tour activity
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TOUR_ACTIVITY_VISITED_KEY, false);
            editor.commit();

            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){

            case R.id.main_fab:
                animateMainFAB();
                break;

            case R.id.add_food_fab:
                if(new DBUtils(this).thereAreRecords()) {
                    DialogFragment addFoodDialogFragment = new AddFoodDialogFragment();
                    addFoodDialogFragment.show(getSupportFragmentManager(), "add_food");
                } else {
                    Toast.makeText(this, R.string.toast_warning_add_food, Toast.LENGTH_SHORT).show();
                }

                animateMainFAB();
                break;

            case R.id.add_section_fab:
                DialogFragment addSectionDialogFragment = new AddSectionDialogFragment();
                addSectionDialogFragment.show(getSupportFragmentManager(), "add_section");

                animateMainFAB();
                break;

            case R.id.label_main_fab:
                animateMainFAB();//cancel action with this label
                break;

            case R.id.label_add_food_fab:
                break;

            case R.id.label_add_section_fab:
                break;

            case R.id.clickable_screen:
                animateMainFAB();//cancellation action when touching the screen
                break;

        }
    }

    //here update the ui
    @Override
    public void onAddFood() {
        //update lists with selected sort and filter option
        updateUi(sortOption, sectionFilterId, filterOptionActivated);
    }

    @Override
    public void onAddSection() {
        //update menu options
        updateFilterSectionOptions();
        //update lists with selected sort and filter option
        updateUi(sortOption, sectionFilterId, filterOptionActivated);
    }

    @Override
    public void updateUi() {
        updateUi(sortOption, sectionFilterId, filterOptionActivated);
    }

    @Override
    public void deleteFood(Food deletedFood) {

        final NotifyInterfaceUtils notifyInterfaceUtils = this;
        final Context context = this;
        final Food finalDeletedFood = deletedFood;

        mySnackbar.setText(this.getResources().getString(R.string.snack_bar_deleted) + finalDeletedFood.foodName);

        //insert the food again
        mySnackbar.setAction(this.getResources().getString(R.string.snack_bar_undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DBUtils(context, notifyInterfaceUtils).insertFoodIntoDB(finalDeletedFood);
            }
        }).show();
    }

    //dialog callbacks

    //at editing section
    @Override
    public void onEditFinished() {
        updateFilterSectionOptions();
        updateUi(sortOption, sectionFilterId, filterOptionActivated);
    }

    @Override
    public void onConfirmedDeletion(int sectionId) {
        new DBUtils(this, this).deleteAllFoodsInsideSection(sectionId);
        new DBUtils(this, this).deleteSectionById(sectionId);

        //disable filter by section at deleting any section
        filterOptionActivated = false;
        updateUi(sortOption, sectionFilterId, filterOptionActivated);

        updateFilterSectionOptions();//update section filter options
    }

    @Override
    public void onMoved() {
        updateUi();
    }

    private void updateUi(int sortOption, int sectionFilterId, boolean filterOptionActivated) {

        Section[] sections = new DBUtils(this).getAllSectionsArray();

        sectionAdapter = new SectionAdapter(sections, this, sortOption, this, getSupportFragmentManager());

        Section[] filteredSections = null;

        if(filterOptionActivated) {
            for (int i = 0; i < sections.length; i++) {
                if(sectionFilterId == sections[i].sectionId) {
                    filteredSections = SectionUtils.filterSectionById(sections[i].sectionId, this);
                    i = sections.length;
                }
            }

            //update the adapter and uses this
            sectionAdapter = new SectionAdapter(filteredSections, this, sortOption, this, getSupportFragmentManager());
        }

        recyclerView.setAdapter(sectionAdapter);
        updateWidget();
    }

    private void animateMainFAB(){

        if(isMainFabOpen){

            //fabs
            mainFab.startAnimation(rotate_backward);
            addFoodFab.startAnimation(dissolve);
            addSectionFab.startAnimation(dissolve);

            //textView
            mainFabLabel.startAnimation(dissolve_text);
            addFoodFabLabel.startAnimation(dissolve_text);
            addSectionFabLabel.startAnimation(dissolve_text);

            clickableScreen.setVisibility(View.INVISIBLE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.customPrimaryDark));

            addFoodFab.setClickable(false);
            addSectionFab.setClickable(false);
            mainFabLabel.setClickable(false);
            addFoodFabLabel.setClickable(false);
            addSectionFabLabel.setClickable(false);
            clickableScreen.setClickable(false);

            isMainFabOpen = false;

        } else {

            mainFab.startAnimation(rotate_forward);

            //fabs
            addFoodFab.startAnimation(appear);
            addSectionFab.startAnimation(appear);

            //textView
            mainFabLabel.startAnimation(appear_text);
            addFoodFabLabel.startAnimation(appear_text);
            addSectionFabLabel.startAnimation(appear_text);

            clickableScreen.setVisibility(View.VISIBLE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mainFabOpened));

            addFoodFab.setClickable(true);
            addSectionFab.setClickable(true);
            mainFabLabel.setClickable(true);
            addFoodFabLabel.setClickable(true);
            addSectionFabLabel.setClickable(true);
            clickableScreen.setClickable(true);

            isMainFabOpen = true;

        }
    }

    public void checkIfSetAlarm() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notificationDeliveredToday = sharedPreferences.getBoolean(NOTIFICATION_DELIVERED_TODAY, false);//false for default

        if(notificationDeliveredToday) {
            //Log.d(TAG, "Notification already delivered today");
            int deliveredDay = sharedPreferences.getInt(NOTIFICATION_DELIVERED_DAY, 32);
            //Log.d(TAG, "Delivered Day: " + deliveredDay);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int day = cal.get(Calendar.DAY_OF_MONTH);

            //Log.d(TAG, "Today: " + day);
            if(deliveredDay != day) {
                //Log.d(TAG, "It's another day, let's schedule an alarm again");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIFICATION_DELIVERED_TODAY, true);
                editor.commit();

                setAlarm();
            }

        } else {
            //Log.d(TAG, "Notification not delivered yet today, let's schedule an alarm");
            setAlarm();
        }

    }

    public void setAlarm() {
        //schedule an alarm at 8:00 a.m. and every day therefore
        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.setAction(NotificationService.ACTION_CREATE_NOTIFICATION);

        //wrap intent inside the pendingIntent
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to start at approximately 8:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),//set alarm at 8:00 p.m.
                AlarmManager.INTERVAL_DAY,//you must use AlarmManager interval constants
                pendingIntent);
    }

    public void updateWidget() {
        WidgetService.startActionUpdateWidgets(this);
    }

}
