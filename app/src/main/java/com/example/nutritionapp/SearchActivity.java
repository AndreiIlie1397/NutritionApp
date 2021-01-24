package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nutritionapp.Holder.SearchHolder;
import com.example.nutritionapp.Model.Search_food;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    ImageView mBack, Search, mAdd;
    RecyclerView search_list;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    EditText search_text;
    String mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        mBack = findViewById(R.id.back_arrow);
        mAdd = findViewById(R.id.add_food);
        Search = findViewById(R.id.search_image);
        search_text = findViewById(R.id.food_search);
        search_list = findViewById(R.id.search_list);
        search_list.setHasFixedSize(true);
        search_list.setLayoutManager(new LinearLayoutManager(this));

        search_food();

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch = search_text.getText().toString();
                search_food();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, AddFoodDatabaseActivity.class);
                startActivity(intent);
            }
        });

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Intent intent = getIntent();
                    final String variabila = intent.getStringExtra("variabila");
                    mSearch = search_text.getText().toString();
                    Query search_query = mReference.child(getString(R.string.food)).orderByChild(getString(R.string.name)).startAt(mSearch).endAt(mSearch + "\uf8ff");

                    FirebaseRecyclerAdapter<Search_food, SearchHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search_food, SearchHolder>(
                            Search_food.class, R.layout.show_food, SearchHolder.class, search_query) {
                        @Override
                        protected void populateViewHolder(SearchHolder viewHolder, Search_food model, int position) {
                            final String food_key = getRef(position).getKey();
                            viewHolder.setName(model.getName());
                            viewHolder.setCalorii(model.getCalorii());
                            viewHolder.setImage(model.getImage());

                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchActivity.this, FoodProfileActivity.class);
                                    intent.putExtra("variabila", variabila);
                                    intent.putExtra("food_key", food_key);
                                    startActivity(intent);
                                }
                            });
                        }
                    };
                    search_list.setAdapter(firebaseRecyclerAdapter);
                }
                return false;
            }
        });
    }

    private void search_food() {
        Intent intent = getIntent();
        final String variabila = intent.getStringExtra("variabila");

        Query search_query = mReference.child(getString(R.string.food)).orderByChild(getString(R.string.name)).startAt(mSearch).endAt(mSearch + "\uf8ff");

        FirebaseRecyclerAdapter<Search_food, SearchHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search_food, SearchHolder>(
                Search_food.class, R.layout.show_food, SearchHolder.class, search_query) {
            @Override
            protected void populateViewHolder(SearchHolder viewHolder, Search_food model, int position) {
                final String food_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setCalorii(model.getCalorii());
                viewHolder.setImage(model.getImage());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this, FoodProfileActivity.class);
                        intent.putExtra("variabila", variabila);
                        intent.putExtra("food_key", food_key);
                        startActivity(intent);
                    }
                });
            }
        };
        search_list.setAdapter(firebaseRecyclerAdapter);
    }
}