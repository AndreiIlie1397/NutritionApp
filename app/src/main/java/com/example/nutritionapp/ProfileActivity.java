package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.Account.AccountSettingActivity;
import com.example.nutritionapp.Account.ChangePasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    ImageView mProfile, mBack;
    TextView mName, mProcentCalorii, mProcentCarbohidrati, mProcentGrasimi, mProcentProteine, mObiectiv;
    ProgressBar mProgressBar1, mprogressbar2, mprogressbar3, mprogressbar4;
    RadioGroup radioGroup;
    RadioButton mSlabire, mMentinere, mIngrasare;
    Button mActualizare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mProfile = findViewById(R.id.profile);
        mName = findViewById(R.id.name);
        mProcentCalorii = findViewById(R.id.calorii_text);
        mProcentCarbohidrati = findViewById(R.id.carbohidrati_text);
        mProcentGrasimi = findViewById(R.id.grasimi_text);
        mProcentProteine = findViewById(R.id.proteine_text);
        mProgressBar1 = findViewById(R.id.progressBar1);
        mprogressbar2 = findViewById(R.id.progressBar2);
        mprogressbar3 = findViewById(R.id.progressBar3);
        mprogressbar4 = findViewById(R.id.progressBar4);
        mObiectiv = findViewById(R.id.text_obiectiv);
        radioGroup = findViewById(R.id.group);
        mSlabire = findViewById(R.id.bt_slabire);
        mMentinere = findViewById(R.id.bt_mentinere);
        mIngrasare = findViewById(R.id.bt_ingrasare);
        mBack = findViewById(R.id.back_arrow);
        mActualizare = findViewById(R.id.actualizare);
        getInfo();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String obiectiv = dataSnapshot.child("obiectiv").getValue().toString();
                if (obiectiv.equals("slabire")) {
                    mSlabire.setChecked(true);
                } else if(obiectiv.equals("mentinere")){
                    mMentinere.setChecked(true);
                } else if(obiectiv.equals("ingrasare")){
                    mIngrasare.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        mActualizare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String calorii, ncalorii, carbohidrati, grasimi, proteine;
                        calorii = dataSnapshot.child("calorii").getValue().toString();
                        String obiectiv = dataSnapshot.child("obiectiv").getValue().toString();

                        if (obiectiv.equals("slabire")) {
                            if (mMentinere.isChecked()) {
                                ncalorii = String.valueOf(Double.parseDouble(calorii) + 500);
                                carbohidrati = String.valueOf(round(((0.50 * Double.parseDouble(ncalorii))/4), 2));
                                grasimi = String.valueOf(round(((0.35 * Double.parseDouble(ncalorii))/9), 2));
                                proteine = String.valueOf(round(((0.15 * Double.parseDouble(ncalorii))/4), 2));
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(ncalorii);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(grasimi);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(ncalorii);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(grasimi);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("mentinere");
                                Toast.makeText(getBaseContext(), "Obiectivul de mentinere a fost activat", Toast.LENGTH_SHORT).show();
                            } else if (mIngrasare.isChecked()) {
                                ncalorii = String.valueOf(Double.parseDouble(calorii) + 1000);
                                carbohidrati = String.valueOf(round(((0.30 * Double.parseDouble(ncalorii))/4), 2));
                                grasimi = String.valueOf(round(((0.50 * Double.parseDouble(ncalorii))/9), 2));
                                proteine = String.valueOf(round(((0.20 * Double.parseDouble(ncalorii))/4), 2));
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(ncalorii);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(grasimi);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(ncalorii);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(grasimi);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("ingrasare");
                                Toast.makeText(getBaseContext(), "Obiectivul de ingrasare a fost activat", Toast.LENGTH_SHORT).show();

                            }
                        } else if (obiectiv.equals("mentinere")) {
                            if (mSlabire.isChecked()) {
                                ncalorii = String.valueOf(Double.parseDouble(calorii) - 500);
                                carbohidrati = String.valueOf(round(((0.35 * Double.parseDouble(ncalorii))/4), 2));
                                grasimi = String.valueOf(round(((0.25 * Double.parseDouble(ncalorii))/9), 2));
                                proteine = String.valueOf(round(((0.40 * Double.parseDouble(ncalorii))/4), 2));
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(ncalorii);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(grasimi);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(ncalorii);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(grasimi);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("slabire");
                                Toast.makeText(getBaseContext(), "Obiectivul de slabire a fost activat", Toast.LENGTH_SHORT).show();
                            } else if (mIngrasare.isChecked()) {
                                ncalorii = String.valueOf(Double.parseDouble(calorii) + 500);
                                carbohidrati = String.valueOf(round(((0.30 * Double.parseDouble(ncalorii))/4), 2));
                                grasimi = String.valueOf(round(((0.50 * Double.parseDouble(ncalorii))/9), 2));
                                proteine = String.valueOf(round(((0.20 * Double.parseDouble(ncalorii))/4), 2));
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(ncalorii);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(grasimi);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(ncalorii);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(grasimi);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("ingrasare");
                                Toast.makeText(getBaseContext(), "Obiectivul de ingrasare a fost activat", Toast.LENGTH_SHORT).show();
                            }
                        } else if (obiectiv.equals("ingrasare")) {
                            if (mSlabire.isChecked()) {
                                ncalorii = String.valueOf(Double.parseDouble(calorii) - 1000);
                                carbohidrati = String.valueOf(round(((0.35 * Double.parseDouble(ncalorii))/4), 2));
                                grasimi = String.valueOf(round(((0.25 * Double.parseDouble(ncalorii))/9), 2));
                                proteine = String.valueOf(round(((0.40 * Double.parseDouble(ncalorii))/4), 2));
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(ncalorii);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(grasimi);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(ncalorii);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(grasimi);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("slabire");
                                Toast.makeText(getBaseContext(), "Obiectivul de slabire a fost activat", Toast.LENGTH_SHORT).show();
                            } else if (mMentinere.isChecked()) {
                                ncalorii = String.valueOf(Double.parseDouble(calorii) - 500);
                                carbohidrati = String.valueOf(round(((0.50 * Double.parseDouble(ncalorii))/4), 2));
                                grasimi = String.valueOf(round(((0.35 * Double.parseDouble(ncalorii))/9), 2));
                                proteine = String.valueOf(round(((0.15 * Double.parseDouble(ncalorii))/4), 2));
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(ncalorii);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(grasimi);
                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(ncalorii);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(carbohidrati);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(grasimi);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(proteine);
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("mentinere");
                                Toast.makeText(getBaseContext(), "Obiectivul de mentinere a fost activat", Toast.LENGTH_SHORT).show();
                            }
                            mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(this);
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

    private void getInfo() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name, image;
                    name = dataSnapshot.child("name").getValue().toString();
                    image = dataSnapshot.child(getString(R.string.profile_image)).getValue().toString();
                    mName.setText(name);
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(mProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String kcal_t, kcal_c, carbohidrati_t, carbohidrati_c, grasimi_t, grasimi_c, proteine_t, proteine_c;
                    double progress_total, progress_total_carbohidrati, progress_total_grasimi, progress_total_proteine;
                    kcal_t = dataSnapshot.child("calorii").getValue().toString();
                    kcal_c = dataSnapshot.child("total").getValue().toString();
                    carbohidrati_t = dataSnapshot.child("carbohidrati").getValue().toString();
                    carbohidrati_c = dataSnapshot.child("totalCarbohidrati").getValue().toString();
                    grasimi_t = dataSnapshot.child("grasimi").getValue().toString();
                    grasimi_c = dataSnapshot.child("totalGrasimi").getValue().toString();
                    proteine_t = dataSnapshot.child("proteine").getValue().toString();
                    proteine_c = dataSnapshot.child("totalProteine").getValue().toString();
                    progress_total = (Double.parseDouble(kcal_c) * 100) / Double.parseDouble(kcal_t);
                    setProgressBar1((int) progress_total);

                    progress_total_carbohidrati = (Double.parseDouble(carbohidrati_c) * 100) / Double.parseDouble(carbohidrati_t);
                    setProgressBar2((int) progress_total_carbohidrati);

                    progress_total_grasimi = (Double.parseDouble(grasimi_c) * 100) / Double.parseDouble(grasimi_t);
                    setProgressBar3((int) progress_total_grasimi);

                    progress_total_proteine = (Double.parseDouble(proteine_c) * 100) / Double.parseDouble(proteine_t);
                    setProgressBar4((int) progress_total_proteine);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setProgressBar1(int value) {
        mProgressBar1.setMax(100);
        mProgressBar1.setProgress(value);
        mProcentCalorii.setText(String.valueOf(value));
        if (value == 100) {
            mObiectiv.setText("Necesarul caloric a fost atins!");
        } else if (value < 100) {
            mObiectiv.setText("Necesarul caloric nu a fost atins!");
        } else {
            mObiectiv.setText("Necesarul caloric a fost depasit!");
        }
    }

    private void setProgressBar2(int value) {
        mprogressbar2.setMax(100);
        mprogressbar2.setProgress(value);
        mProcentCarbohidrati.setText(String.valueOf(value));
    }

    private void setProgressBar3(int value) {
        mprogressbar3.setMax(100);
        mprogressbar3.setProgress(value);
        mProcentGrasimi.setText(String.valueOf(value));
    }

    private void setProgressBar4(int value) {
        mprogressbar4.setMax(100);
        mprogressbar4.setProgress(value);
        mProcentProteine.setText(String.valueOf(value));
    }
}
