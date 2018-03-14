package com.apps.yecotec.fridgetracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.data.Section;
import com.apps.yecotec.fridgetracker.dialogs.AboutUsDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.AddFoodDialogFragment;
import com.apps.yecotec.fridgetracker.dialogs.EditFoodDialogFragment;
import com.apps.yecotec.fridgetracker.utils.DBUtils;
import com.apps.yecotec.fridgetracker.utils.FoodUtils;
import com.apps.yecotec.fridgetracker.utils.NotifyInterfaceUtils;

import static com.apps.yecotec.fridgetracker.MainActivity.DELETED_FOOD;
import static com.apps.yecotec.fridgetracker.SectionAdapter.FOOD_ID;
import static com.apps.yecotec.fridgetracker.utils.StaticVarsUtils.EXCEPTION_EXTRA;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, EditFoodDialogFragment.DetailActivityNotifyInterface{

    //for logging purpose
    String TAG = this.getClass().getSimpleName();

    private ImageView foodAsset, removeButton, addButton;
    private TextView expirationDateText, sectionText, unitText, categoryText, quantityText, eatAllButton;

    private int foodId = 0;
    private Food food;
    private boolean exception = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        foodAsset = (ImageView) findViewById(R.id.detail_food_asset);
        removeButton = (ImageView) findViewById(R.id.detail_remove_button);
        addButton = (ImageView) findViewById(R.id.detail_add_button);
        expirationDateText = (TextView) findViewById(R.id.detail_expiry_date_text);
        sectionText = (TextView) findViewById(R.id.detail_section_text);
        unitText = (TextView) findViewById(R.id.detail_unit_text);
        categoryText = (TextView) findViewById(R.id.detail_category_text);
        quantityText = (TextView) findViewById(R.id.detail_quantity_text);
        eatAllButton = (TextView) findViewById(R.id.detail_eat_all_button);

        removeButton.setOnClickListener(this);
        addButton.setOnClickListener(this);

        Intent intent = getIntent();

        //populate ui
        if(intent.hasExtra(FOOD_ID)) {
            foodId = intent.getIntExtra(FOOD_ID, 0);

            populateUi(foodId);
        }

        if(intent.hasExtra(EXCEPTION_EXTRA)) exception = intent.getBooleanExtra(EXCEPTION_EXTRA, false);
    }

    //handle each click event
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.detail_eat_all_button:
                new DBUtils(this).deleteFoodById(foodId);

                if (exception) {//handle case if user navigate to this activity from widget or notification
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(DELETED_FOOD, food);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra(DELETED_FOOD, food);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

                break;

            case R.id.detail_remove_button:

                if(food.foodUnit == 0) {
                    if(food.foodQuantity > 0) food.foodQuantity -= 1;
                    else Toast.makeText(this, R.string.warning_negative_case, Toast.LENGTH_SHORT).show();
                } else {
                    if(food.foodQuantity > 0) food.foodQuantity -= 0.1;
                    else Toast.makeText(this, R.string.warning_negative_case, Toast.LENGTH_SHORT).show();
                    food.foodQuantity = Math.round( food.foodQuantity * 10.0 ) / 10.0;//avoid floating point arithmetic problem
                }

                new DBUtils(this).updateFood(food);
                populateUi(food.foodId);

                break;

            case R.id.detail_add_button:

                if(food.foodUnit == 0) {
                    food.foodQuantity += 1;
                } else {
                    food.foodQuantity += 0.1;
                    food.foodQuantity = Math.round( food.foodQuantity * 10.0 ) / 10.0;//avoid floating point arithmetic problem
                }

                new DBUtils(this).updateFood(food);
                populateUi(food.foodId);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit:
                DialogFragment newFragment = new EditFoodDialogFragment();
                Bundle foodIdBundle = new Bundle();
                foodIdBundle.putInt(FOOD_ID, foodId);
                newFragment.setArguments(foodIdBundle);
                newFragment.show(getSupportFragmentManager(), "edit_food");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditFinished(int foodId) {
        populateUi(foodId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToMain();
    }

    @Override
    public boolean onSupportNavigateUp(){
        navigateToMain();
        finish();
        return true;
    }

    public void populateUi(int foodId){
        food = new DBUtils(this).getFoodById(foodId);
        Section section = new DBUtils(this).getSectionById(food.sectionId);

        getSupportActionBar().setTitle(food.foodName);

        foodAsset.setImageResource(FoodUtils.getFoodAsset(food.foodCategory));
        expirationDateText.setText(getResources().getString(R.string.expiration_label) + " " + food.foodExpireDate);
        sectionText.setText(getResources().getString(R.string.section_label) + " " + section.sectionName);
        unitText.setText(getResources().getString(R.string.unit_label)  + " " + new FoodUtils(this).getUnitString(food.foodUnit));
        categoryText.setText(getResources().getString(R.string.category_label)  + " " + new FoodUtils(this).getCategoryName(food.foodCategory));
        quantityText.setText(getResources().getString(R.string.quantity_label)  + " " + food.foodQuantity);
        if(food.foodUnit == 0) quantityText.setText(getResources().getString(R.string.quantity_label) + (int) food.foodQuantity);
        eatAllButton.setOnClickListener(this);
    }

    public void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
