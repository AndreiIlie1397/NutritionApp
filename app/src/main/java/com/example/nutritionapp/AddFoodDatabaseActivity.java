package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFoodDatabaseActivity extends AppCompatActivity {
    private static final String TAG = "AddFoodDatabaseActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 2;
    TextView mName, mCalorii, mCarbohidrati, mGrasimi, mProteine;
    Button mSend;
    ImageView mImage, mBack;
    String strName, strCalorii, strCarbohidrati, strGrasimi, strProteine;
    Uri imageUri;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_database);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        mName = findViewById(R.id.text_name);
        mCalorii = findViewById(R.id.text_calorii);
        mCarbohidrati = findViewById(R.id.text_carbohidrati);
        mGrasimi = findViewById(R.id.text_grasimi);
        mProteine = findViewById(R.id.text_proteine);
        mImage = findViewById(R.id.image);
        mSend = findViewById(R.id.button);
        mBack = findViewById(R.id.back_arrow);

        mCalorii.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        mCarbohidrati.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        mGrasimi.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        mProteine.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_food_database();
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddFoodDatabaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                    Toast.makeText(AddFoodDatabaseActivity.this, "Aplicatia trebuie sa aiba acces la memoria telefonului", Toast.LENGTH_SHORT).show();
                } else {
                    selectImage();
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void add_food_database() {
        strName = mName.getText().toString();
        strCalorii = mCalorii.getText().toString();
        strCarbohidrati = mCarbohidrati.getText().toString();
        strGrasimi = mGrasimi.getText().toString();
        strProteine = mProteine.getText().toString();
        final String food_key = mReference.push().getKey();
        if (check_input(strName, strCalorii, strCarbohidrati, strGrasimi, strProteine)) {
            mReference.child(getString(R.string.food)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    select_image(food_key);
                    mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child(getString(R.string.name)).setValue(strName);
                    mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child(getString(R.string.calorii)).setValue(strCalorii);
                    mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child(getString(R.string.carbohidrati)).setValue(strCarbohidrati);
                    mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child(getString(R.string.grasimi)).setValue(strGrasimi);
                    mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child(getString(R.string.proteine)).setValue(strProteine);

                    mReference.child(getString(R.string.food)).removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            finish();
        }
    }

    private void select_image(final String food_key) {
        if (imageUri != null) {
            final StorageReference imagePath = mStorage.child(getString(R.string.food)).child(String.valueOf(food_key)).child(imageUri.getLastPathSegment());
            imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();
                            mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child("image").setValue(downloadUrl);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NotNull Exception exception) {
                        }
                    });
        } else {
            mReference.child(getString(R.string.food)).child(String.valueOf(food_key)).child("image").setValue("https://image.flaticon.com/icons/png/512/84/84072.png");
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Realizeaza o fotografie", "Incarca din galerie", "Anulare"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodDatabaseActivity.this);
        builder.setTitle("Adauga o fotografie");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Realizeaza o fotografie")) {
                    cameraIntent();
                } else if (items[which].equals("Incarca din galerie")) {
                    galleryIntent();
                } else if (items[which].equals("Anulare")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                imageUri = data.getData();
                mImage.setImageURI(imageUri);
            } else if (requestCode == REQUEST_CAMERA) {
                Log.d("ceva", "onActivityResult: " + REQUEST_CAMERA);
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImage.setImageBitmap(imageBitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), imageBitmap, "Title", null);
                imageUri = Uri.parse(path);
            }
        }
    }

    private boolean check_input(String name, String calorii, String carbohidrati, String grasimi, String proteine) {
        if (name.equals("") || calorii.equals("") || carbohidrati.equals("") || grasimi.equals("") || proteine.equals("")) {
            Toast.makeText(this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
            return false;
        } else if (calorii.charAt(0) == '0') {
            Toast.makeText(AddFoodDatabaseActivity.this, "Numarul de calorii nu poate fi 0", Toast.LENGTH_SHORT).show();
            return false;
        } else if (calorii.charAt(0) == '0' && calorii.charAt(1) == '0') {
            Toast.makeText(AddFoodDatabaseActivity.this, "Numarul de calorii nu este corect", Toast.LENGTH_SHORT).show();
            return false;
        } else if (calorii.charAt(0) == '.' || carbohidrati.charAt(0) == '.' || grasimi.charAt(0) == '.' || proteine.charAt(0) == '.') {
            Toast.makeText(AddFoodDatabaseActivity.this, "Cel putin unul dintre campuri este incorect", Toast.LENGTH_SHORT).show();
            return false;
        } else if(calorii.substring(calorii.length() - 1).equals(".") || carbohidrati.substring(carbohidrati.length() - 1).equals(".") || grasimi.substring(grasimi.length() - 1).equals(".") || proteine.substring(proteine.length() - 1).equals(".")){
            Toast.makeText(AddFoodDatabaseActivity.this, "Cel putin unul dintre campuri este incorect", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
