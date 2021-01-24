package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodProfileActivity extends AppCompatActivity {
    private static final String TAG = "FoodProfileActivity";
    String my_id;
    int cantitate;
    double Cantitate;
    String currentDate;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    TextView name, kcal, mcarbohidrati, mgrasimi, mproteine;
    EditText mgramaj;
    SeekBar mcantitate;
    Button button, mBack;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_profile);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mBack = findViewById(R.id.button_back);
        name = findViewById(R.id.text_name);
        kcal = findViewById(R.id.text_kcal);
        mgramaj = findViewById(R.id.text_gramaj);
        image = findViewById(R.id.image);
        mcarbohidrati = findViewById(R.id.text_carbohidrati);
        mgrasimi = findViewById(R.id.text_grasimi);
        mproteine = findViewById(R.id.text_proteine);
        mcantitate = findViewById(R.id.cantitate);
        button = findViewById(R.id.button);
        getFood();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getFood() {
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Intent intent = getIntent();
        final String variabila = intent.getStringExtra("variabila");
        final String food_key = intent.getStringExtra("food_key");

        mReference.child(getString(R.string.food)).child(food_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                final double calorii, carbohidrati, grasimi, proteine;
                final String strCalorii, strName, strCarbohidrati, strGrasimi, strProteine, strImage;
                strName = dataSnapshot.child(getString(R.string.name)).getValue().toString();
                strCalorii = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                strCarbohidrati = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                strGrasimi = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                strProteine = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                strImage = dataSnapshot.child(getString(R.string.image)).getValue().toString();
                name.setText(strName);
                kcal.setText(strCalorii);
                mgramaj.setText("100");
                mcarbohidrati.setText(strCarbohidrati);
                mgrasimi.setText(strGrasimi);
                mproteine.setText(strProteine);
                mcantitate.setProgress(100);
                mgramaj.setSelection(mgramaj.getText().length());
                Picasso.get().load(strImage).placeholder(R.drawable.dinner).into(image);

                mgramaj.setFilters(new InputFilter[]{new FoodProfileActivity.DecimalDigitsInputFilter(3, 2)});

                calorii = Double.parseDouble(strCalorii);
                carbohidrati = Double.parseDouble(strCarbohidrati);
                grasimi = Double.parseDouble(strGrasimi);
                proteine = Double.parseDouble(strProteine);

                mcantitate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        kcal.setText(String.valueOf(round((calorii * Double.parseDouble(String.valueOf(progress))) / 100, 2)));
                        mgramaj.setText(String.valueOf(progress));
                        mcarbohidrati.setText(String.valueOf(round((carbohidrati * Double.parseDouble(String.valueOf(progress))) / 100, 2)));
                        mgrasimi.setText(String.valueOf(round((grasimi * Double.parseDouble(String.valueOf(progress))) / 100, 2)));
                        mproteine.setText(String.valueOf(round((proteine * Double.parseDouble(String.valueOf(progress))) / 100, 2)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                mgramaj.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (TextUtils.isEmpty(mgramaj.getText().toString())) {
                            mcantitate.setProgress(0);
                            kcal.setText("0.0");
                            mcarbohidrati.setText("0.0");
                            mgrasimi.setText("0.0");
                            mproteine.setText("0.0");

                        } else {
                            int i = Integer.parseInt(s.toString());
                            if (i > 0 && i <= 500) {
                                mgramaj.setSelection(mgramaj.getText().length());
                                mcantitate.setProgress(i);
                                kcal.setText(String.valueOf(round((calorii * Double.parseDouble(String.valueOf(i))) / 100, 2)));
                                mcarbohidrati.setText(String.valueOf(round((carbohidrati * Double.parseDouble(String.valueOf(i))) / 100, 2)));
                                mgrasimi.setText(String.valueOf(round((grasimi * Double.parseDouble(String.valueOf(i))) / 100, 2)));
                                mproteine.setText(String.valueOf(round((proteine * Double.parseDouble(String.valueOf(i))) / 100, 2)));
                            } else if (i > 500) {
                                mgramaj.setSelection(mgramaj.getText().length());
                                mcantitate.setProgress(500);
                                kcal.setText(String.valueOf(round((calorii * Double.parseDouble(String.valueOf(500))) / 100, 2)));
                                mcarbohidrati.setText(String.valueOf(round((carbohidrati * Double.parseDouble(String.valueOf(i))) / 100, 2)));
                                mgrasimi.setText(String.valueOf(round((grasimi * Double.parseDouble(String.valueOf(500))) / 100, 2)));
                                mproteine.setText(String.valueOf(round((proteine * Double.parseDouble(String.valueOf(500))) / 100, 2)));
                            }
                        }
                    }
                });


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        final String strTotal, Total, strCarbohidrati, strGrasimi, strProteine, Carbohidrati, Grasimi, Proteine;
                                        double total, t, totalCarbohidrati, c, totalGrasimi, g, totalProteine, p;
                                        strTotal = dataSnapshot.child(getString(R.string.total)).getValue().toString();
                                        strCarbohidrati = dataSnapshot.child(getString(R.string.totalCarbohidrati)).getValue().toString();
                                        strGrasimi = dataSnapshot.child(getString(R.string.totalGrasimi)).getValue().toString();
                                        strProteine = dataSnapshot.child(getString(R.string.totalProteine)).getValue().toString();
                                        t = Double.parseDouble(strTotal);
                                        c = Double.parseDouble(strCarbohidrati);
                                        g = Double.parseDouble(strGrasimi);
                                        p = Double.parseDouble(strProteine);
                                        cantitate = mcantitate.getProgress();

                                        Cantitate = round(cantitate, 2);
                                        total = ((calorii * Cantitate) / 100) + t;
                                        Total = String.valueOf(round(total, 2));
                                        mReference.child("Evidenta").child(my_id).child(currentDate).child(getString(R.string.total)).setValue(Total);

                                        totalCarbohidrati = ((carbohidrati * Cantitate) / 100) + c;
                                        Carbohidrati = String.valueOf(round(totalCarbohidrati, 2));
                                        mReference.child("Evidenta").child(my_id).child(currentDate).child(getString(R.string.totalCarbohidrati)).setValue(Carbohidrati);

                                        totalGrasimi = ((grasimi * Cantitate) / 100) + g;
                                        Grasimi = String.valueOf(round(totalGrasimi, 2));
                                        mReference.child("Evidenta").child(my_id).child(currentDate).child(getString(R.string.totalGrasimi)).setValue(Grasimi);

                                        totalProteine = ((proteine * Cantitate) / 100) + p;
                                        Proteine = String.valueOf(round(totalProteine, 2));
                                        mReference.child("Evidenta").child(my_id).child(currentDate).child(getString(R.string.totalProteine)).setValue(Proteine);

                                        if (cantitate == 0) {

                                        } else if (cantitate == 1) {
                                            Toast.makeText(FoodProfileActivity.this, "Ai adaugat un gram de " + strName, Toast.LENGTH_SHORT).show();
                                        } else if (cantitate == 2) {
                                            Toast.makeText(FoodProfileActivity.this, "Ai adaugat doua grame de " + strName, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(FoodProfileActivity.this, "Ai adaugat " + cantitate + " grame de " + strName, Toast.LENGTH_SHORT).show();
                                        }

                                        if (variabila.equals("1")) {
                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast)).child(strName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                                                    if (dataSnapshot.exists()) {
                                                        if (cantitate != 0) {
                                                            final double dcalorii, dcarbohidrati, dgrasimi, dproteine, dgramaj;
                                                            final String calo, carb, gras, prot, gram;
                                                            calo = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                                                            carb = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                                                            gras = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                                                            prot = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                                                            gram = dataSnapshot.child(getString(R.string.gramaj)).getValue().toString();
                                                            dcalorii = Double.valueOf(calo) + Double.valueOf((String) kcal.getText());
                                                            dgramaj = Double.valueOf(gram) + Double.valueOf(String.valueOf(mgramaj.getText()));
                                                            dcarbohidrati = Double.valueOf(carb) + Double.valueOf(String.valueOf(mcarbohidrati.getText()));
                                                            dgrasimi = Double.valueOf(gras) + Double.valueOf(String.valueOf(mgrasimi.getText()));
                                                            dproteine = Double.valueOf(prot) + Double.valueOf(String.valueOf(mproteine.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(round(dcalorii, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(dgramaj));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(round(dcarbohidrati, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(round(dgrasimi, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(round(dproteine, 2)));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (cantitate != 0) {
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(kcal.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(mgramaj.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(mcarbohidrati.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(mgrasimi.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(mproteine.getText()));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.breakfast)).child(strName).removeEventListener(this);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                        if (variabila.equals("2")) {
                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch)).child(strName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                                                    if (dataSnapshot.exists()) {
                                                        if (cantitate != 0) {
                                                            final double dcalorii, dcarbohidrati, dgrasimi, dproteine, dgramaj;
                                                            final String calo, carb, gras, prot, gram;
                                                            calo = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                                                            carb = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                                                            gras = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                                                            prot = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                                                            gram = dataSnapshot.child(getString(R.string.gramaj)).getValue().toString();
                                                            dcalorii = Double.valueOf(calo) + Double.valueOf((String) kcal.getText());
                                                            dgramaj = Double.valueOf(gram) + Double.valueOf(String.valueOf(mgramaj.getText()));
                                                            dcarbohidrati = Double.valueOf(carb) + Double.valueOf(String.valueOf(mcarbohidrati.getText()));
                                                            dgrasimi = Double.valueOf(gras) + Double.valueOf(String.valueOf(mgrasimi.getText()));
                                                            dproteine = Double.valueOf(prot) + Double.valueOf(String.valueOf(mproteine.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(round(dcalorii, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(dgramaj));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(round(dcarbohidrati, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(round(dgrasimi, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(round(dproteine, 2)));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (cantitate != 0) {
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(kcal.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(mgramaj.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(mcarbohidrati.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(mgrasimi.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(mproteine.getText()));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                        mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.lunch)).child(strName).removeEventListener(this);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                        if (variabila.equals("3")) {
                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner)).child(strName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                                                    if (dataSnapshot.exists()) {
                                                        if (cantitate != 0) {
                                                            final double dcalorii, dcarbohidrati, dgrasimi, dproteine, dgramaj;
                                                            final String calo, carb, gras, prot, gram;
                                                            calo = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                                                            carb = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                                                            gras = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                                                            prot = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                                                            gram = dataSnapshot.child(getString(R.string.gramaj)).getValue().toString();
                                                            dcalorii = Double.valueOf(calo) + Double.valueOf((String) kcal.getText());
                                                            dgramaj = Double.valueOf(gram) + Double.valueOf(String.valueOf(mgramaj.getText()));
                                                            dcarbohidrati = Double.valueOf(carb) + Double.valueOf(String.valueOf(mcarbohidrati.getText()));
                                                            dgrasimi = Double.valueOf(gras) + Double.valueOf(String.valueOf(mgrasimi.getText()));
                                                            dproteine = Double.valueOf(prot) + Double.valueOf(String.valueOf(mproteine.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(round(dcalorii, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(dgramaj));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(round(dcarbohidrati, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(round(dgrasimi, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(round(dproteine, 2)));

                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (cantitate != 0) {
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(kcal.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(mgramaj.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(mcarbohidrati.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(mgrasimi.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(mproteine.getText()));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.dinner)).child(strName).removeEventListener(this);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                        if (variabila.equals("4")) {
                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1)).child(strName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                                                    if (dataSnapshot.exists()) {
                                                        if (cantitate != 0) {
                                                            final double dcalorii, dcarbohidrati, dgrasimi, dproteine, dgramaj;
                                                            final String calo, carb, gras, prot, gram;
                                                            calo = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                                                            carb = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                                                            gras = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                                                            prot = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                                                            gram = dataSnapshot.child(getString(R.string.gramaj)).getValue().toString();
                                                            dcalorii = Double.valueOf(calo) + Double.valueOf((String) kcal.getText());
                                                            dgramaj = Double.valueOf(gram) + Double.valueOf(String.valueOf(mgramaj.getText()));
                                                            dcarbohidrati = Double.valueOf(carb) + Double.valueOf(String.valueOf(mcarbohidrati.getText()));
                                                            dgrasimi = Double.valueOf(gras) + Double.valueOf(String.valueOf(mgrasimi.getText()));
                                                            dproteine = Double.valueOf(prot) + Double.valueOf(String.valueOf(mproteine.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(round(dcalorii, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(dgramaj));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(round(dcarbohidrati, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(round(dgrasimi, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(round(dproteine, 2)));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (cantitate != 0) {
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(kcal.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(mgramaj.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(mcarbohidrati.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(mgrasimi.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(mproteine.getText()));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack1)).child(strName).removeEventListener(this);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                        if (variabila.equals("5")) {
                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2)).child(strName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                                                    if (dataSnapshot.exists()) {
                                                        if (cantitate != 0) {
                                                            final double dcalorii, dcarbohidrati, dgrasimi, dproteine, dgramaj;
                                                            final String calo, carb, gras, prot, gram;
                                                            calo = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                                                            carb = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                                                            gras = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                                                            prot = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                                                            gram = dataSnapshot.child(getString(R.string.gramaj)).getValue().toString();
                                                            dcalorii = Double.valueOf(calo) + Double.valueOf((String) kcal.getText());
                                                            dgramaj = Double.valueOf(gram) + Double.valueOf(String.valueOf(mgramaj.getText()));
                                                            dcarbohidrati = Double.valueOf(carb) + Double.valueOf(String.valueOf(mcarbohidrati.getText()));
                                                            dgrasimi = Double.valueOf(gras) + Double.valueOf(String.valueOf(mgrasimi.getText()));
                                                            dproteine = Double.valueOf(prot) + Double.valueOf(String.valueOf(mproteine.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(round(dcalorii, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(dgramaj));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(round(dcarbohidrati, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(round(dgrasimi, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(round(dproteine, 2)));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (cantitate != 0) {
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(kcal.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(mgramaj.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(mcarbohidrati.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(mgrasimi.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(mproteine.getText()));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack2)).child(strName).removeEventListener(this);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                        if (variabila.equals("6")) {
                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3)).child(strName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                                                    if (dataSnapshot.exists()) {
                                                        if (cantitate != 0) {
                                                            final double dcalorii, dcarbohidrati, dgrasimi, dproteine, dgramaj;
                                                            final String calo, carb, gras, prot, gram;
                                                            calo = dataSnapshot.child(getString(R.string.calorii)).getValue().toString();
                                                            carb = dataSnapshot.child(getString(R.string.carbohidrati)).getValue().toString();
                                                            gras = dataSnapshot.child(getString(R.string.grasimi)).getValue().toString();
                                                            prot = dataSnapshot.child(getString(R.string.proteine)).getValue().toString();
                                                            gram = dataSnapshot.child(getString(R.string.gramaj)).getValue().toString();
                                                            dcalorii = Double.valueOf(calo) + Double.valueOf((String) kcal.getText());
                                                            dgramaj = Double.valueOf(gram) + Double.valueOf(String.valueOf(mgramaj.getText()));
                                                            dcarbohidrati = Double.valueOf(carb) + Double.valueOf(String.valueOf(mcarbohidrati.getText()));
                                                            dgrasimi = Double.valueOf(gras) + Double.valueOf(String.valueOf(mgrasimi.getText()));
                                                            dproteine = Double.valueOf(prot) + Double.valueOf(String.valueOf(mproteine.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(round(dcalorii, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(dgramaj));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(round(dcarbohidrati, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(round(dgrasimi, 2)));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(round(dproteine, 2)));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        if (cantitate != 0) {
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.calorii)).setValue(String.valueOf(kcal.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.gramaj)).setValue(String.valueOf(mgramaj.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.carbohidrati)).setValue(String.valueOf(mcarbohidrati.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.grasimi)).setValue(String.valueOf(mgrasimi.getText()));
                                                            mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3))
                                                                    .child(strName).child(getString(R.string.proteine)).setValue(String.valueOf(mproteine.getText()));
                                                            finish();
                                                        } else if (cantitate == 0) {
                                                            Toast.makeText(FoodProfileActivity.this, "Cantitatea de " + strName + " trebuie sa fie mai mare de 0 ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    mReference.child(getString(R.string.jurnal)).child(my_id).child(currentDate).child(getString(R.string.snack3)).child(strName).removeEventListener(this);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }

                                        mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).removeEventListener(this);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }
}

