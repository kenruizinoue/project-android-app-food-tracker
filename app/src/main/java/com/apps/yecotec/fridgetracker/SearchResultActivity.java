package com.apps.yecotec.fridgetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.apps.yecotec.fridgetracker.utils.DBUtils;

import static android.app.SearchManager.QUERY;

public class SearchResultActivity extends AppCompatActivity {

    SearchFoodAdapter searchFoodAdapter;
    RecyclerView recyclerView;
    String queryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setTitle(R.string.search_activity_text);

        recyclerView = (RecyclerView) findViewById(R.id.rv_search_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        //to designate that the contents of the RecyclerView won't change an item's size
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();

        //populate ui
        if(intent.hasExtra(QUERY)) {
            queryString = intent.getStringExtra(QUERY);
            populateUi(queryString);
        }

    }

    private void populateUi(String queryString) {
        searchFoodAdapter = new SearchFoodAdapter(new DBUtils(this).queryFoods(queryString),this);
        recyclerView.setAdapter(searchFoodAdapter);
    }

}
