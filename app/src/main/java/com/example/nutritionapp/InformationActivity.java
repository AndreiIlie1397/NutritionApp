package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.nutritionapp.Utility.CustomListView;

public class InformationActivity extends AppCompatActivity {
    ListView mListView;
    ImageView mBack;

    String[] activity = {"Sedentar", "Putin Activ", "Moderat Activ", "Foarte Activ", "Extrem Activ"};
    String[] description = {"mai puțin de 5.000 de pași / zi",
    "între 5.000 și 7.499 de pași / zi",
    "între 7.500 și 9.999 de pași / zi",
    "între 10.000  si 12.499 de pași / zi",
    "mai mult de 12.500 pași / zi"};
    Integer[] image = {R.drawable.sedentar, R.drawable.putin_activ, R.drawable.moderat_activ, R.drawable.foarte_activ, R.drawable.extrem_activ};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mListView = findViewById(R.id.listView);
        mBack = findViewById(R.id.back_arrow);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      CustomListView arrayAdapter = new CustomListView(this, activity, description, image);
      mListView.setAdapter(arrayAdapter);
    }
}
