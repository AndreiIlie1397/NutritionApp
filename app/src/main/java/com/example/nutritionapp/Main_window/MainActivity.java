package com.example.nutritionapp.Main_window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.example.nutritionapp.Account.AccountSettingActivity;
import com.example.nutritionapp.Account.LoginActivity;
import com.example.nutritionapp.HomeActivity;
import com.example.nutritionapp.ProfileActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.ProgresActivity;
import com.example.nutritionapp.Utility.Firebase_method;
import com.example.nutritionapp.Utility.SectionPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    Context mContext = MainActivity.this;
    //Firebase variables
    Firebase_method firebase_method;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    SectionPagerAdapter sectionPagerAdapter;
    TabLayout mTabLayout;
    Toolbar mToolbar;
    ViewPager mViewPager;
    NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebase_method = new Firebase_method(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        firebaseUser = mAuth.getCurrentUser();

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mViewPager = findViewById(R.id.container);
        mTabLayout = findViewById(R.id.tabs);
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(sectionPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mToggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationmenu);
        navigationView.setNavigationItemSelectedListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name, email, image;
                        if (dataSnapshot.exists()) {
                            View mView = navigationView.getHeaderView(0);
                            TextView mName = mView.findViewById(R.id.nume);
                            TextView mEmail = mView.findViewById(R.id.email);
                            ImageView mProfile = mView.findViewById(R.id.profile);
                            name = dataSnapshot.child("name").getValue().toString();
                            email = dataSnapshot.child("email").getValue().toString();
                            image = dataSnapshot.child(getString(R.string.profile_image)).getValue().toString();
                            mName.setText(name);
                            mEmail.setText(email);
                            Picasso.get().load(image).placeholder(R.drawable.profile).into(mProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                finish();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sigur vrei sa iesi?");
            builder.setCancelable(true);
            builder.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profil:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;

            case R.id.progres:
                startActivity(new Intent(getApplicationContext(), ProgresActivity.class));
                break;

            case R.id.setari:
                startActivity(new Intent(getApplicationContext(), AccountSettingActivity.class));
                break;

            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Vrei sa iesi din cont?");

                builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(mContext, HomeActivity.class));

                    }
                });
                builder.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;

            case R.id.sterge:
                final String cont = FirebaseAuth.getInstance().getCurrentUser().getUid();
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Stergere cont");
                dialog.setMessage("Esti sigur ca vrei sa stergi contul?");
                dialog.setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull final Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Contul a fost sters", Toast.LENGTH_SHORT).show();
                                    Log.d("ceva1", "onDataChange: " + cont);
                                    mReference.child("Evidenta").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                Log.d("ceva2", "onDataChange: " + cont);
                                                if (childSnapshot.getKey().equals(cont)) {
                                                    Log.d("ceva3", "onDataChange: " + cont);
                                                    childSnapshot.getRef().removeValue();
                                                }
                                            }
                                            mReference.child("Evidenta").removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                    mReference.child("Jurnal").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                if (childSnapshot.getKey().equals(cont)) {
                                                    childSnapshot.getRef().removeValue();
                                                }
                                            }
                                            mReference.child("Jurnal").removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                    StorageReference deleteFile = mStorage.child(getString(R.string.users)).child(cont).child("profile.jpg");
                                    deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                    mReference.child("Users").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                if (childSnapshot.getKey().equals(cont)) {
                                                    childSnapshot.getRef().removeValue();
                                                }
                                            }
                                            mReference.child("Users").removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                } else {
                                    Toast.makeText(MainActivity.this, "Pentru siguranta sporita trebuie sa va conectati din nou pentru a putea sterge contul", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}