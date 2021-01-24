package com.example.nutritionapp.Utility;

import android.content.Context;
import android.widget.Toast;

import com.example.nutritionapp.Model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

public class Firebase_method {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    Context mContext;
    String userID;

    public Firebase_method(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mContext = context;
    }

    public void register_new_email(final String strEmail, String strPassword) {
        mAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        Toast.makeText(mContext, "Adresa de mail exista in baza de date", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(mContext, "A aparut o eroare la inregistrare", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    userID = mAuth.getCurrentUser().getUid();
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Contul a fost creat cu succes", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public void send_new_user_data(String name, String email, String date, String weight, String height, String gender, String activity, String calorii, String carbohidrati, String grasimi, String proteine, String profile_image, String obiectiv) {
        UserData userData = new UserData(name, email, date, weight, height, gender, activity, calorii, carbohidrati, grasimi, proteine, profile_image, obiectiv);
        try {
            mReference.child("Users").child(userID).setValue(userData);
        } catch (NullPointerException e) {
        }
    }
}
