package com.example.nutritionapp.Account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.nutritionapp.InformationActivity;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    Button mSend, mLogin;
    TextView mName, mEmail, mPassword, mDay, mMonth, mYear, mWeight, mHeight;
    RadioButton mMasculin, mFeminin, mSedentar, mPutinActiv, mModeratActiv, mFoarteActiv, mExtremActiv;
    CircleImageView mImage;
    ImageView mInfo;

    //Variables
    String strName, strEmail, strPassword, strDay, strMonth, strYear, strAge, strDate, strWeight, strHeight, strGender, strActivity;
    private static final int request_code = 5;
    Uri imageUri;
    //Firebase variables
    Firebase_method firebase_method;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebase_method = new Firebase_method(this);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mLogin = findViewById(R.id.bt_login);
        mSend = findViewById(R.id.bt_register);
        mImage = findViewById(R.id.profile_image);
        mName = findViewById(R.id.text_name);
        mEmail = findViewById(R.id.text_email);
        mPassword = findViewById(R.id.text_password);
        mDay = findViewById(R.id.day);
        mMonth = findViewById(R.id.month);
        mYear = findViewById(R.id.year);
        mWeight = findViewById(R.id.text_weight);
        mHeight = findViewById(R.id.text_height);
        mMasculin = findViewById(R.id.bt_male);
        mFeminin = findViewById(R.id.bt_female);
        mSedentar = findViewById(R.id.radio_bt_sedentar);
        mPutinActiv = findViewById(R.id.radio_bt_putin_activ);
        mModeratActiv = findViewById(R.id.radio_bt_moderat_activ);
        mFoarteActiv = findViewById(R.id.radio_bt_foarte_activ);
        mExtremActiv = findViewById(R.id.radio_bt_extrem_activ);
        mInfo = findViewById(R.id.info);

        mDay.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        mMonth.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        mYear.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, InformationActivity.class);
                startActivity(intent);
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_new_user();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, request_code);
            }
        });
        setupFirebaseAuthentication();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            CropImage.activity(imagePath)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(RegisterActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void select_image(final String userId) {
        try {
            if (imageUri != null) {
                final StorageReference imagePath = mStorage.child(getString(R.string.users)).child(userId).child("profile.jpg");
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
        } catch (IllegalArgumentException ignored) {
        }
    }

    private String getAge(String strDay, String strMonth, String strYear) {
        if (strDay.equals("") || strMonth.equals("") || strYear.equals("") || strDay.isEmpty() || strMonth.isEmpty() || strYear.isEmpty() || Integer.parseInt(strDay) < 0 || Integer.parseInt(strDay) > 32 || (Integer.parseInt(strMonth) < 0 || Integer.parseInt(strMonth) > 13) || (Integer.parseInt(strYear) < 1900 || Integer.parseInt(strYear) > 2020) || strDay.equals("0") || strMonth.equals("0") || strYear.equals("0")) {
            return "0";
        } else {
            int day, month, year;
            day = Integer.parseInt(strDay);
            month = Integer.parseInt(strMonth);
            year = Integer.parseInt(strYear);
            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            dob.set(year, month, day);
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            Integer ageInt = new Integer(age);
            String ageS = ageInt.toString();
            return ageS;
        }
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

    private void register_new_user() {
        strName = mName.getText().toString();
        strEmail = mEmail.getText().toString();
        strPassword = mPassword.getText().toString();
        strDay = mDay.getText().toString();
        strMonth = mMonth.getText().toString();
        strYear = mYear.getText().toString();

        strAge = getAge(strDay, strMonth, strYear);
        strDate = strDay + "-" + strMonth + "-" + strYear;

        strWeight = mWeight.getText().toString();
        strHeight = mHeight.getText().toString();
        if (check_imput(strName, strEmail, strPassword, strAge, strWeight, strHeight)) {
            if (isValidEmail(strEmail)) {
                if (check_password(strPassword)) {
                    if (isDateValid(strDate)) {
                        firebase_method.register_new_email(strEmail, strPassword);
                    } else
                        Toast.makeText(RegisterActivity.this, "Data nasterii nu este corecta", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(RegisterActivity.this, "Introduceti o parola mai mare de 8 caractere", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(RegisterActivity.this, "Introduceti o adresa de email corecta", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean check_imput(String name, String email, String password, String age, String weight, String height) {
        if (name.equals("") || email.equals("") || password.equals("") || age.equals("") || weight.equals("") || height.equals("")) {
            Toast.makeText(this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (Double.parseDouble(weight) >= 30 && Double.parseDouble(weight) <= 200 && Double.parseDouble(height) >= 60 && Double.parseDouble(height) <= 220) {
                return true;
            } else {
                if (Double.parseDouble(weight) <= 30 || Double.parseDouble(weight) >= 200) {
                    Toast.makeText(this, "Completati corect campul greutate", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (Double.parseDouble(height) <= 60 || Double.parseDouble(height) >= 220) {
                    Toast.makeText(this, "Completati corect campul inaltime", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean check_password(String password) {
        return password.length() >= 8;
    }

    private void setupFirebaseAuthentication() {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    final String userId = mAuth.getCurrentUser().getUid();
                    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) throws NullPointerException {
                            double A, B, C, D = 0, E = 0, F, calorii, age, weight, height, aportcarbo, aportgrasimi, aportproteine, aportc, aportg, aportp;
                            String Calorii, AportCarbo, AportGrasimi, AportProteine;

                            age = Double.parseDouble(strAge);
                            weight = Double.parseDouble(strWeight);
                            height = Double.parseDouble(strHeight);
                            A = 10 * weight;
                            B = 6.25 * height;
                            C = 5 * age;

                            if (mMasculin.isChecked()) {
                                D = A + B - C + 5;
                                strGender = "Masculin";
                            } else if (mFeminin.isChecked()) {
                                D = A + B - C - 161;
                                strGender = "Feminin";
                            }

                            if (mSedentar.isChecked()) {
                                E = D * 1200;
                                strActivity = "Sedentar";
                            } else if (mPutinActiv.isChecked()) {
                                E = D * 1375;
                                strActivity = "Putin Activ";
                            } else if (mModeratActiv.isChecked()) {
                                E = D * 1550;
                                strActivity = "Moderat Activ";
                            } else if (mFoarteActiv.isChecked()) {
                                E = D * 1725;
                                strActivity = "Foarte Activ";
                            } else if (mExtremActiv.isChecked()) {
                                E = D * 1900;
                                strActivity = "Extrem Activ";
                            }
                            F = E / 1000;
                            calorii = round(F, 2);
                            Calorii = String.valueOf(calorii);

                            aportcarbo = (0.50 * calorii) / 4;
                            aportc = round(aportcarbo, 2);
                            AportCarbo = String.valueOf(aportc);

                            aportgrasimi = (0.35 * calorii) / 9;
                            aportg = round(aportgrasimi, 2);
                            AportGrasimi = String.valueOf(aportg);

                            aportproteine = (0.15 * calorii) / 4;
                            aportp = round(aportproteine, 2);
                            AportProteine = String.valueOf(aportp);

                            select_image(userId);
                            firebase_method.send_new_user_data(strName, strEmail, strDate, strWeight, strHeight, strGender, strActivity, Calorii, AportCarbo, AportGrasimi, AportProteine, "Default", "mentinere");
                            mAuth.signOut();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    finish();
                }
            }
        };
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
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
