package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AddFoodActivity extends AppCompatActivity {
    private static final String TAG = "AddFoodActivity";
    ImageView mBack;
    Button mBreakfast, mLunch, mDinner, mSnack1, mSnack2, mSnack3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        mBack = findViewById(R.id.back_arrow);
        mBreakfast = findViewById(R.id.breakfast);
        mLunch = findViewById(R.id.lunch);
        mDinner = findViewById(R.id.dinner);
        mSnack1 = findViewById(R.id.snack1);
        mSnack2 = findViewById(R.id.snack2);
        mSnack3 = findViewById(R.id.snack3);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this, SearchActivity.class);
                intent.putExtra("variabila","1");
                startActivity(intent);
            }
        });

        mLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this, SearchActivity.class);
                intent.putExtra("variabila","2");
                startActivity(intent);
            }
        });

        mDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this, SearchActivity.class);
                intent.putExtra("variabila","3");
                startActivity(intent);
            }
        });

        mSnack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this, SearchActivity.class);
                intent.putExtra("variabila","4");
                startActivity(intent);
            }
        });

        mSnack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this, SearchActivity.class);
                intent.putExtra("variabila","5");
                startActivity(intent);
            }
        });

        mSnack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFoodActivity.this, SearchActivity.class);
                intent.putExtra("variabila","6");
                startActivity(intent);
            }
        });
    }
}
