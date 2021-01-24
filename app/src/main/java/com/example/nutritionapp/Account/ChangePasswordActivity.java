package com.example.nutritionapp.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPassword, newPassword;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    ProgressDialog mDialog;
    Button mButton, mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        mButton = findViewById(R.id.change_password);
        mBack = findViewById(R.id.bt_inapoi);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });
    }

    public void change() {
        if (oldPassword.getText().toString().isEmpty() || newPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Cel putin un camp nu este completat", Toast.LENGTH_SHORT).show();
        } else {
            if (newPassword.getText().toString().length() <= 8) {
                Toast.makeText(this, "Parola noua trebuie sa contina cel putin 8 caractere", Toast.LENGTH_SHORT).show();
            }
            if (newPassword.getText().toString().equals(oldPassword.getText().toString())) {
                Toast.makeText(this, "Noua parola trebuie sa fie diferita de cea veche", Toast.LENGTH_SHORT).show();
            } else {
                final FirebaseUser user;
                user = FirebaseAuth.getInstance().getCurrentUser();
                final String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword.getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDialog.setMessage("Se schimba parola...");
                            mDialog.show();
                            user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        mDialog.dismiss();
                                        Toast.makeText(ChangePasswordActivity.this, "Parola nu a fost schimbata", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(ChangePasswordActivity.this, "Parola a fost schimbata cu succes", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        finish();
                                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Parola veche nu este corecta", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        }
    }
}
