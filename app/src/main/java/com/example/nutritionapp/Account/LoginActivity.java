package com.example.nutritionapp.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.Main_window.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.Utility.Firebase_method;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    TextView mEmail, mPassword;
    Button mSend, mResetPassword, mRegister;
    ProgressDialog pd;

    //Firebase Variable
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;
    Firebase_method firebase_method;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebase_method = new Firebase_method(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);
        mSend = findViewById(R.id.bt_login);
        mEmail = findViewById(R.id.text_email);
        mPassword = findViewById(R.id.text_password);
        mResetPassword = findViewById(R.id.bt_password_reset);
        mRegister = findViewById(R.id.bt_register);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailSignIn();
            }
        });

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in");
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    private void emailSignIn() {
        String strEmail, strPassword;
        strEmail = mEmail.getText().toString();
        strPassword = mPassword.getText().toString();
        pd.setMessage("Asteptati...");
        pd.show();
        if (strEmail.equals("") || strPassword.equals("")) {
            Toast.makeText(this, "Introduceti email-ul si parola", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        } else {
            mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, "Logarea a avut succes", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Contul nu este activat", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Parola sau email incorect", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}