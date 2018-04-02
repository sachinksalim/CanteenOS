package com.example.sachin.canteenos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FoodMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        Intent intent = getIntent();
    }

    public void addFoodItem(View v) {
        Intent intent = new Intent(this, FoodItem.class);
        startActivity(intent);
    }
}
