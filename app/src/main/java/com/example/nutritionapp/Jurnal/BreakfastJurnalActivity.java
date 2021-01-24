package com.example.nutritionapp.Jurnal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nutritionapp.Holder.BreakfastExpandableListViewAdapter;
import com.example.nutritionapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BreakfastJurnalActivity extends AppCompatActivity {
    private static Context context;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    ExpandableListView expandableListView;
    BreakfastExpandableListViewAdapter customExpandableListViewAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ImageView button;
    TextView mtext;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast_jurnal);
        expandableListView = findViewById(R.id.expandable_listview);
        button = findViewById(R.id.back_arrow);
        mtext = findViewById(R.id.text);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        BreakfastJurnalActivity.context = getApplicationContext();
        initListData();
        customExpandableListViewAdapter = new BreakfastExpandableListViewAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(customExpandableListViewAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        currentDate = getIntent().getStringExtra("data");

        DatabaseReference rootRef = mReference.child(getString(R.string.jurnal)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child(getString(R.string.breakfast));
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    mtext.setText("Nu exista nicio inregistrare!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mReference.child(getString(R.string.jurnal)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child(getString(R.string.breakfast))
                .addChildEventListener(new ChildEventListener() {

                    List<String> childItem;
                    int counter = 0;

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        listDataHeader.add(dataSnapshot.getKey());
                        childItem = new ArrayList<>();

                        if (dataSnapshot.child(getString(R.string.gramaj)).getKey().equals("gramaj")) {
                            childItem.add("gramaj: " + dataSnapshot.child(getString(R.string.gramaj)).getValue());
                        }
                        if (dataSnapshot.child(getString(R.string.calorii)).getKey().equals("calorii")) {
                            childItem.add("calorii: " + dataSnapshot.child(getString(R.string.calorii)).getValue());
                        }
                        if (dataSnapshot.child(getString(R.string.carbohidrati)).getKey().equals("carbohidrati")) {
                            childItem.add("carbohidrati: " + dataSnapshot.child(getString(R.string.carbohidrati)).getValue());
                        }
                        if (dataSnapshot.child(getString(R.string.grasimi)).getKey().equals("grasimi")) {
                            childItem.add("grasimi: " + dataSnapshot.child(getString(R.string.grasimi)).getValue());
                        }
                        if (dataSnapshot.child(getString(R.string.proteine)).getKey().equals("proteine")) {
                            childItem.add("proteine: " + dataSnapshot.child(getString(R.string.proteine)).getValue());
                        }

                        listDataChild.put(listDataHeader.get(counter), childItem);
                        counter++;
                        customExpandableListViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        listDataHeader.remove(dataSnapshot.getKey());
                        counter--;
                        customExpandableListViewAdapter.notifyDataSetChanged();
                        if(counter == 0){
                            mtext.setText("Nu exista nicio inregistrare!");
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}