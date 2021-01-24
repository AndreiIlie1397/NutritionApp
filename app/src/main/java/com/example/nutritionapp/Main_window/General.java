package com.example.nutritionapp.Main_window;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.nutritionapp.AddFoodActivity;
import com.example.nutritionapp.HomeActivity;
import com.example.nutritionapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class General extends Fragment {
    ProgressBar mProgressBar, progressbar1, progressbar2, progressbar3;
    TextView calorii_necesare, calorii_totale, kCal, carbo, gras, prot, totalkcal, totalcarbohidrati, totalgrasimi, totalproteine;
    Button mButton;
    String currentDate;
    boolean dialogShow1 = false, dialogShow2 = false, dialogShow3 = false, dialogShow4 = false;
    //Firebase variables
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    StorageReference mStorage;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_general, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressBar = mView.findViewById(R.id.progressBar);
        progressbar1 = mView.findViewById(R.id.progressBar1);
        progressbar2 = mView.findViewById(R.id.progressBar2);
        progressbar3 = mView.findViewById(R.id.progressBar3);
        mButton = mView.findViewById(R.id.button);
        calorii_necesare = mView.findViewById(R.id.calorii_necesare);
        calorii_totale = mView.findViewById(R.id.calorii_consumate);
        kCal = mView.findViewById(R.id.kcal);
        carbo = mView.findViewById(R.id.carbohidrati);
        gras = mView.findViewById(R.id.grasimi);
        prot = mView.findViewById(R.id.proteine);
        totalcarbohidrati = mView.findViewById(R.id.totalcarbohidrati);
        totalgrasimi = mView.findViewById(R.id.totalgrasimi);
        totalproteine = mView.findViewById(R.id.totalproteine);
        totalkcal = mView.findViewById(R.id.totalkcal);

        get_calorii();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mView.getContext(), AddFoodActivity.class);
                startActivity(intent);
            }
        });

        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getAge(Date date) {
        LocalDate dataActuala;
        Date currentdate = new Date(System.currentTimeMillis());
        dataActuala = currentdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate datebirth = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return String.valueOf(Period.between(datebirth, dataActuala).getYears());
    }

    private void get_calorii() {
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("total").exists() && dataSnapshot.child("totalCarbohidrati").exists() && dataSnapshot.child("totalGrasimi").exists() && dataSnapshot.child("totalProteine").exists()) {
                            final String total, totalCarbohidrati, totalGrasimi, totalProteine, calorii, carbohidrati, grasimi, proteine;
                            total = dataSnapshot.child("total").getValue().toString();
                            totalCarbohidrati = dataSnapshot.child("totalCarbohidrati").getValue().toString();
                            totalGrasimi = dataSnapshot.child("totalGrasimi").getValue().toString();
                            totalProteine = dataSnapshot.child("totalProteine").getValue().toString();
                            calorii = dataSnapshot.child("calorii").getValue().toString();
                            carbohidrati = dataSnapshot.child("carbohidrati").getValue().toString();
                            grasimi = dataSnapshot.child("grasimi").getValue().toString();
                            proteine = dataSnapshot.child("proteine").getValue().toString();
                            double progress_total, progress_total_carbohidrati, progress_total_grasimi, progress_total_proteine;
                            calorii_necesare.setText(calorii);
                            calorii_totale.setText(total);
                            kCal.setText(total);
                            totalkcal.setText(calorii);
                            progress_total = (Double.parseDouble(total) * 100) / Double.parseDouble(calorii);
                            setProgressBar((int) progress_total);
                            totalcarbohidrati.setText(carbohidrati);
                            carbo.setText(totalCarbohidrati);
                            progress_total_carbohidrati = (Double.parseDouble(totalCarbohidrati) * 100) / Double.parseDouble(carbohidrati);
                            setProgressBar1((int) progress_total_carbohidrati);

                            totalgrasimi.setText(grasimi);
                            gras.setText(totalGrasimi);
                            progress_total_grasimi = (Double.parseDouble(totalGrasimi) * 100) / Double.parseDouble(grasimi);
                            setProgressBar2((int) progress_total_grasimi);

                            totalproteine.setText(proteine);
                            prot.setText(totalProteine);
                            progress_total_proteine = (Double.parseDouble(totalProteine) * 100) / Double.parseDouble(proteine);
                            setProgressBar3((int) progress_total_proteine);

                            if (Double.parseDouble(calorii) < Double.parseDouble(total)) {
                                if (dialogShow1 == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    dialogShow1 = true;
                                    builder.setTitle("Ati depasit necesarul caloric!");
                                    builder.show();
                                }
                            }
                            if (Double.parseDouble(calorii) > Double.parseDouble(total)) {
                                dialogShow1 = false;
                            }
                            if (Double.parseDouble(carbohidrati) < Double.parseDouble(totalCarbohidrati)) {
                                if (dialogShow2 == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    dialogShow2 = true;
                                    builder.setTitle("Ati depasit necesarul de carbohidrati!");
                                    builder.show();
                                }
                            }
                            if (Double.parseDouble(carbohidrati) > Double.parseDouble(totalCarbohidrati)) {
                                dialogShow2 = false;
                            }
                            if (Double.parseDouble(grasimi) < Double.parseDouble(totalGrasimi)) {
                                if (dialogShow3 == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    dialogShow3 = true;
                                    builder.setTitle("Ati depasit necesarul de grasimi!");
                                    builder.show();
                                }
                            }
                            if (Double.parseDouble(grasimi) > Double.parseDouble(totalGrasimi)) {
                                dialogShow3 = false;
                            }
                            if (Double.parseDouble(proteine) < Double.parseDouble(totalProteine)) {
                                if (dialogShow4 == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    dialogShow4 = true;
                                    builder.setTitle("Ati depasit necesarul de proteine!");
                                    builder.show();
                                }
                            }
                            if (Double.parseDouble(proteine) > Double.parseDouble(totalProteine)) {
                                dialogShow4 = false;
                            }

                        } else {
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                getActivity().finish();
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                startActivity(intent);
                            } else {
                                mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Date date1 = null;
                                            String weight, height, date, gender, activity, age, Calorii, Carbohidrati, Grasimi, Proteine;
                                            double A, B, C, D = 0, E = 0, calorii, carbohidrati, grasimi, proteine;
                                            weight = dataSnapshot.child("weight").getValue().toString();
                                            height = dataSnapshot.child("height").getValue().toString();
                                            date = dataSnapshot.child("date").getValue().toString();
                                            gender = dataSnapshot.child("gender").getValue().toString();
                                            activity = dataSnapshot.child("activity").getValue().toString();

                                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                            try {
                                                date1 = format.parse(date);
                                                System.out.println(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            age = getAge(date1);


                                            A = 10 * Double.valueOf(weight);
                                            B = 6.25 * Double.valueOf(height);
                                            C = 5 * Double.valueOf(age);

                                            if (gender.equals("Masculin")) {
                                                if (activity.equals("Sedentar")) {
                                                    D = (A + B - C + 5) * 1200;
                                                } else if (activity.equals("Putin Activ")) {
                                                    D = (A + B - C + 5) * 1375;
                                                } else if (activity.equals("Moderat Activ")) {
                                                    D = (A + B - C + 5) * 1550;
                                                } else if (activity.equals("Foarte Activ")) {
                                                    D = (A + B - C + 5) * 1725;
                                                } else if (activity.equals("Extrem Activ")) {
                                                    D = (A + B - C + 5) * 1900;
                                                }
                                            } else if (gender.equals("Feminin")) {
                                                if (activity.equals("Sedentar")) {
                                                    D = (A + B - C - 161) * 1200;
                                                } else if (activity.equals("Putin Activ")) {
                                                    D = (A + B - C - 161) * 1375;
                                                } else if (activity.equals("Moderat Activ")) {
                                                    D = (A + B - C - 161) * 1550;
                                                } else if (activity.equals("Foarte Activ")) {
                                                    D = (A + B - C - 161) * 1725;
                                                } else if (activity.equals("Extrem Activ")) {
                                                    D = (A + B - C - 161) * 1900;
                                                }
                                            }
                                            E = D / 1000;
                                            calorii = round(E, 2);
                                            Calorii = String.valueOf(calorii);

                                            carbohidrati = (0.50 * calorii) / 4;
                                            Carbohidrati = String.valueOf(round(carbohidrati, 2));


                                            grasimi = (0.35 * calorii) / 9;
                                            Grasimi = String.valueOf(round(grasimi, 2));

                                            proteine = (0.15 * calorii) / 4;
                                            Proteine = String.valueOf(round(proteine, 2));

                                            mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("calorii").setValue(Calorii);
                                            mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carbohidrati").setValue(Carbohidrati);
                                            mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grasimi").setValue(Grasimi);
                                            mReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("proteine").setValue(Proteine);

                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("calorii").setValue(Calorii);
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("total").setValue("0.0");
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("carbohidrati").setValue(Carbohidrati);
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("totalCarbohidrati").setValue("0.0");
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("grasimi").setValue(Grasimi);
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("totalGrasimi").setValue("0.0");
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("proteine").setValue(Proteine);
                                            mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("totalProteine").setValue("0.0");
                                            calorii_necesare.setText(Calorii);
                                            totalkcal.setText(Calorii);
                                            totalcarbohidrati.setText(Carbohidrati);
                                            totalgrasimi.setText(Grasimi);
                                            totalproteine.setText(Proteine);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
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

    private void setProgressBar(int value) {
        mProgressBar.setMax(100);
        mProgressBar.setProgress(value);
    }

    private void setProgressBar1(int value) {
        progressbar1.setMax(100);
        progressbar1.setProgress(value);
    }

    private void setProgressBar2(int value) {
        progressbar2.setMax(100);
        progressbar2.setProgress(value);
    }

    private void setProgressBar3(int value) {
        progressbar3.setMax(100);
        progressbar3.setProgress(value);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
        get_calorii();
    }
}