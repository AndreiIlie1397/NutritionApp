package com.example.nutritionapp.Account;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.R;
import com.example.nutritionapp.Utility.Firebase_method;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountSettingActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingActivity";
    private static final int Gallery_Request = 5;
    String user_id;
    double carbohidrati, grasimi, proteine, aportcarbohidrati, aportgrasimi, aportproteine;
    int flag;
    Uri imageUri;
    ImageView mBack, mUpdate, mProfile_Image;
    EditText mName, mWeigh, mHeight, mDay, mMonth, mYear;
    RadioButton mSedentar, mPutinActiv, mModeratActiv, mFoarteActiv, mExtremActiv, mMasculin, mFeminin;
    TextView mChange_Image, mChange_Password;

    //Firebase variables
    Firebase_method firebase_method;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        firebase_method = new Firebase_method(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        mBack = findViewById(R.id.back_arrow);
        mUpdate = findViewById(R.id.update_done);
        mProfile_Image = findViewById(R.id.profile_image);
        mChange_Image = findViewById(R.id.change_photo);
        mChange_Password = findViewById(R.id.parola);
        mName = findViewById(R.id.text_name);
        mWeigh = findViewById(R.id.text_weight);
        mHeight = findViewById(R.id.text_height);
        mDay = findViewById(R.id.day);
        mMonth = findViewById(R.id.month);
        mYear = findViewById(R.id.year);
        mSedentar = findViewById(R.id.radio_bt_sedentar);
        mPutinActiv = findViewById(R.id.radio_bt_putin_activ);
        mModeratActiv = findViewById(R.id.radio_bt_moderat_activ);
        mFoarteActiv = findViewById(R.id.radio_bt_foarte_activ);
        mExtremActiv = findViewById(R.id.radio_bt_extrem_activ);
        mMasculin = findViewById(R.id.bt_male);
        mFeminin = findViewById(R.id.bt_female);

        mDay.setFilters(new InputFilter[]{new AccountSettingActivity.DecimalDigitsInputFilter(2, 1)});
        mMonth.setFilters(new InputFilter[]{new AccountSettingActivity.DecimalDigitsInputFilter(2, 1)});
        mYear.setFilters(new InputFilter[]{new AccountSettingActivity.DecimalDigitsInputFilter(4, 1)});

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            getUser_Profile_Data();
        }

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (set_User_Profile_data() == 1) {
                    finish();
                }
            }
        });

        mProfile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Gallery_Request);
            }
        });

        mChange_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Gallery_Request);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClick(View v) {
        Intent intent = new Intent(AccountSettingActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getAge(Date date) {
        LocalDate dataActuala;
        Date currentdate = new Date(System.currentTimeMillis());
        dataActuala = currentdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate datebirth = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return String.valueOf(Period.between(datebirth, dataActuala).getYears());
    }

    public static boolean isDateValid(String date) {
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private int set_User_Profile_data() {
        final String name, day, month, year, weight, height, strDate;
        String activity = "", gender = "";

        name = mName.getText().toString();
        day = mDay.getText().toString();
        month = mMonth.getText().toString();
        year = mYear.getText().toString();

        strDate = day + "-" + month + "-" + year;

        weight = mWeigh.getText().toString();
        height = mHeight.getText().toString();

        if (mMasculin.isChecked()) {
            gender = "Masculin";
        } else if (mFeminin.isChecked()) {
            gender = "Feminin";
        }

        if (mSedentar.isChecked()) {
            activity = "Sedentar";
        } else if (mPutinActiv.isChecked()) {
            activity = "Putin Activ";
        } else if (mModeratActiv.isChecked()) {
            activity = "Moderat Activ";
        } else if (mFoarteActiv.isChecked()) {
            activity = "Foarte Activ";
        } else if (mExtremActiv.isChecked()) {
            activity = "Extrem Activ";
        }

        final String finalActivity = activity;
        final String finalGender = gender;

        if (name.isEmpty() || day.isEmpty() || month.isEmpty() || year.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            Toast.makeText(getBaseContext(), "Completati toate campurile", Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            if (Double.parseDouble(weight) >= 30 && Double.parseDouble(weight) <= 200 && Double.parseDouble(height) >= 60 && Double.parseDouble(height) <= 220 && isDateValid(strDate)) {
                final String finalGender1 = gender;
                final String finalActivity1 = activity;
                mReference.child(getString(R.string.users)).addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String calorii;
                        double A, B, C, D = 0, E = 0, F = 0, Calorii, age;
                        Date date1 = null;

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            date1 = format.parse(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        age = Double.valueOf(getAge(date1));

                        A = 10 * Double.valueOf(weight);
                        B = 6.25 * Double.valueOf(height);
                        C = 5 * age;

                        if (finalGender1.equals("Masculin")) {
                            mMasculin.setChecked(true);
                            D = A + B - C + 5;
                        } else if (finalGender1.equals("Feminin")) {
                            mFeminin.setChecked(true);
                            D = A + B - C - 161;
                        }

                        if (finalActivity1.equals("Sedentar")) {
                            mSedentar.setChecked(true);
                            E = D * 1200;
                        } else if (finalActivity1.equals("Putin Activ")) {
                            mPutinActiv.setChecked(true);
                            E = D * 1375;
                        } else if (finalActivity1.equals("Moderat Activ")) {
                            mModeratActiv.setChecked(true);
                            E = D * 1550;
                        } else if (finalActivity1.equals("Foarte Activ")) {
                            mFoarteActiv.setChecked(true);
                            E = D * 1725;
                        } else if (finalActivity1.equals("Extrem Activ")) {
                            mExtremActiv.setChecked(true);
                            E = D * 1900;
                        }
                        F = E / 1000;
                        Calorii = round(F, 2);
                        calorii = String.valueOf(Calorii);

                        carbohidrati = (0.50 * Calorii) / 4;
                        aportcarbohidrati = round(carbohidrati, 2);
                        grasimi = (0.35 * Calorii) / 9;
                        aportgrasimi = round(grasimi, 2);
                        proteine = (0.15 * Calorii) / 4;
                        aportproteine = round(proteine, 2);

                        mReference.child("Evidenta").child(user_id).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("calorii").setValue(calorii);
                        mReference.child("Evidenta").child(user_id).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("carbohidrati").setValue(String.valueOf(aportcarbohidrati));
                        mReference.child("Evidenta").child(user_id).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("grasimi").setValue(String.valueOf(aportgrasimi));
                        mReference.child("Evidenta").child(user_id).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("proteine").setValue(String.valueOf(aportproteine));
                        mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("obiectiv").setValue("mentinere");
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.calorii)).setValue(calorii);
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.carbohidrati)).setValue(String.valueOf(aportcarbohidrati));
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.grasimi)).setValue(String.valueOf(aportgrasimi));
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.proteine)).setValue(String.valueOf(aportproteine));

                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.name)).setValue(name);
                        mReference.child(getString(R.string.users)).child(user_id).child("date").setValue(strDate);
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.weight)).setValue(weight);
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.height)).setValue(height);
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.activity)).setValue(finalActivity);
                        mReference.child(getString(R.string.users)).child(user_id).child(getString(R.string.gender)).setValue(finalGender);
                        mReference.child(getString(R.string.users)).removeEventListener(this);

                        if (flag == 1) {
                            final String userId = mAuth.getCurrentUser().getUid();
                            final StorageReference imagePath = mStorage.child(getString(R.string.users)).child(user_id).child("profile.jpg");
                            imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String downloadUrl = uri.toString();
                                            mReference.child(getString(R.string.users)).child(userId).child("profile_image").setValue(downloadUrl);
                                        }
                                    });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NotNull Exception exception) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                return 1;
            } else {
                if (Double.parseDouble(weight) <= 30 || Double.parseDouble(weight) >= 200) {
                    Toast.makeText(this, "Completati corect campul greutate", Toast.LENGTH_SHORT).show();
                    return 0;
                }
                if (Double.parseDouble(height) <= 60 || Double.parseDouble(height) >= 220) {
                    Toast.makeText(this, "Completati corect campul inaltime", Toast.LENGTH_SHORT).show();
                    return 0;
                }
                if (!isDateValid(strDate)) {
                    Toast.makeText(this, "Completati corect data nasterii", Toast.LENGTH_SHORT).show();
                    return 0;
                }
            }
        }
        return 1;
    }

    private void getUser_Profile_Data() {
        mReference.child(getString(R.string.users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {
                        String name, weight, height, image, activity, gender, day, month, year, date;
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            name = dataSnapshot.child(getString(R.string.name)).getValue().toString();
                            weight = dataSnapshot.child(getString(R.string.weight)).getValue().toString();
                            height = dataSnapshot.child(getString(R.string.height)).getValue().toString();
                            activity = dataSnapshot.child(getString(R.string.activity)).getValue().toString();
                            image = dataSnapshot.child(getString(R.string.profile_image)).getValue().toString();
                            gender = dataSnapshot.child(getString(R.string.gender)).getValue().toString();
                            date = dataSnapshot.child("date").getValue().toString();

                            String[] dateParts = date.split("-");
                            day = dateParts[0];
                            month = dateParts[1];
                            year = dateParts[2];

                            mName.setText(name);
                            mName.setSelection(mName.getText().length());
                            mDay.setText(day);
                            mMonth.setText(month);
                            mYear.setText(year);
                            mWeigh.setText(weight);
                            mHeight.setText(height);

                            if (gender.equals("Masculin")) {
                                mMasculin.setChecked(true);
                            } else if (gender.equals("Feminin")) {
                                mFeminin.setChecked(true);
                            }
                            if (activity.equals("Sedentar")) {
                                mSedentar.setChecked(true);
                            } else if (activity.equals("Putin Activ")) {
                                mPutinActiv.setChecked(true);
                            } else if (activity.equals("Moderat Activ")) {
                                mModeratActiv.setChecked(true);
                            } else if (activity.equals("Foarte Activ")) {
                                mFoarteActiv.setChecked(true);
                            } else if (activity.equals("Extrem Activ")) {
                                mExtremActiv.setChecked(true);
                            }
                            Picasso.get().load(image).placeholder(R.drawable.profile).into(mProfile_Image);
                            mReference.child(getString(R.string.users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Request && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(AccountSettingActivity.this);
            flag = 1;
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mProfile_Image.setImageURI(imageUri);
                flag = 1;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

