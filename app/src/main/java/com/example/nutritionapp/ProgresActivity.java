package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ProgresActivity extends AppCompatActivity {
    GraphView graph;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    LineGraphSeries<DataPoint> series1;
    BarGraphSeries<DataPoint> series;
    ImageView mBack;
    TextView mZiua1, mZiua2, mZiua3, mZiua4, mZiua5, mZiua6, mZiua7, mzi1, mzi2, mzi3, mzi4, mzi5, mzi6, mzi7;
    View line1, line2, line3, line4, line5, line6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progres);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        graph = findViewById(R.id.graph);
        mBack = findViewById(R.id.back_arrow);
        mZiua1 = findViewById(R.id.ziua1);
        mZiua2 = findViewById(R.id.ziua2);
        mZiua3 = findViewById(R.id.ziua3);
        mZiua4 = findViewById(R.id.ziua4);
        mZiua5 = findViewById(R.id.ziua5);
        mZiua6 = findViewById(R.id.ziua6);
        mZiua7 = findViewById(R.id.ziua7);

        mzi1 = findViewById(R.id.zi1);
        mzi2 = findViewById(R.id.zi2);
        mzi3 = findViewById(R.id.zi3);
        mzi4 = findViewById(R.id.zi4);
        mzi5 = findViewById(R.id.zi5);
        mzi6 = findViewById(R.id.zi6);
        mzi7 = findViewById(R.id.zi7);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        line5 = findViewById(R.id.line5);
        line6 = findViewById(R.id.line6);

        series = new BarGraphSeries<>();
        series1 = new LineGraphSeries<>();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mReference.child("Evidenta").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                DataPoint[] dp1 = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                final String[] data = new String[(int) dataSnapshot.getChildrenCount()];
                String total, calorii;
                int i = 0;
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    data[i] = datasnapshot.getKey();
                    i = i + 1;
                }


                Collections.sort(Arrays.asList(data), new Comparator<String>() {
                    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                    @Override
                    public int compare(String o1, String o2) {
                        try {
                            return format.parse(o1).compareTo(format.parse(o2));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                for (int j = 0; j < i; j++) {
                    Log.d("ceva", "onDataChange: " + data[j]);
                    total = dataSnapshot.child(String.valueOf(data[j])).child("total").getValue().toString();
                    calorii = dataSnapshot.child(String.valueOf(data[j])).child("calorii").getValue().toString();
                    dp[j] = new DataPoint(j, Double.parseDouble(total));
                    dp1[j] = new DataPoint(j, Double.parseDouble(calorii));
                    series.appendData(dp[j], true, (int) dataSnapshot.getChildrenCount());
                    series1.appendData(dp1[j], true, (int) dataSnapshot.getChildrenCount());
                }


                if (i == 1) {
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 1);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                }

                if (i == 2) {
                    line1.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 2);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mzi2.setText(dp[i - 2].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                    mZiua2.setText("ziua" + (i - 1) + ": " + data[i - 2]);
                }

                if (i == 3) {
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 3);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mzi2.setText(dp[i - 2].getY() + " kCal");
                    mzi3.setText(dp[i - 3].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                    mZiua2.setText("ziua" + (i - 1) + ": " + data[i - 2]);
                    mZiua3.setText("ziua" + (i - 2) + ": " + data[i - 3]);
                }

                if (i == 4) {
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 4);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mzi2.setText(dp[i - 2].getY() + " kCal");
                    mzi3.setText(dp[i - 3].getY() + " kCal");
                    mzi4.setText(dp[i - 4].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                    mZiua2.setText("ziua" + (i - 1) + ": " + data[i - 2]);
                    mZiua3.setText("ziua" + (i - 2) + ": " + data[i - 3]);
                    mZiua4.setText("ziua" + (i - 3) + ": " + data[i - 4]);
                }

                if (i == 5) {
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                    line4.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 5);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mzi2.setText(dp[i - 2].getY() + " kCal");
                    mzi3.setText(dp[i - 3].getY() + " kCal");
                    mzi4.setText(dp[i - 4].getY() + " kCal");
                    mzi5.setText(dp[i - 5].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                    mZiua2.setText("ziua" + (i - 1) + ": " + data[i - 2]);
                    mZiua3.setText("ziua" + (i - 2) + ": " + data[i - 3]);
                    mZiua4.setText("ziua" + (i - 3) + ": " + data[i - 4]);
                    mZiua5.setText("ziua" + (i - 4) + ": " + data[i - 5]);
                }

                if (i == 6) {
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                    line4.setVisibility(View.VISIBLE);
                    line5.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 6);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mzi2.setText(dp[i - 2].getY() + " kCal");
                    mzi3.setText(dp[i - 3].getY() + " kCal");
                    mzi4.setText(dp[i - 4].getY() + " kCal");
                    mzi5.setText(dp[i - 5].getY() + " kCal");
                    mzi6.setText(dp[i - 6].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                    mZiua2.setText("ziua" + (i - 1) + ": " + data[i - 2]);
                    mZiua3.setText("ziua" + (i - 2) + ": " + data[i - 3]);
                    mZiua4.setText("ziua" + (i - 3) + ": " + data[i - 4]);
                    mZiua5.setText("ziua" + (i - 4) + ": " + data[i - 5]);
                    mZiua6.setText("ziua" + (i - 5) + ": " + data[i - 6]);
                }

                if (i >= 7) {
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                    line4.setVisibility(View.VISIBLE);
                    line5.setVisibility(View.VISIBLE);
                    line6.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX((int) dataSnapshot.getChildrenCount() - 7);
                    graph.getViewport().setMaxX((int) dataSnapshot.getChildrenCount());
                    mzi1.setText(dp[i - 1].getY() + " kCal");
                    mzi2.setText(dp[i - 2].getY() + " kCal");
                    mzi3.setText(dp[i - 3].getY() + " kCal");
                    mzi4.setText(dp[i - 4].getY() + " kCal");
                    mzi5.setText(dp[i - 5].getY() + " kCal");
                    mzi6.setText(dp[i - 6].getY() + " kCal");
                    mzi7.setText(dp[i - 7].getY() + " kCal");
                    mZiua1.setText("ziua" + i + ": " + data[i - 1]);
                    mZiua2.setText("ziua" + (i - 1) + ": " + data[i - 2]);
                    mZiua3.setText("ziua" + (i - 2) + ": " + data[i - 3]);
                    mZiua4.setText("ziua" + (i - 3) + ": " + data[i - 4]);
                    mZiua5.setText("ziua" + (i - 4) + ": " + data[i - 5]);
                    mZiua6.setText("ziua" + (i - 5) + ": " + data[i - 6]);
                    mZiua7.setText("ziua" + (i - 6) + ": " + data[i - 7]);
                }

                GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
                gridLabel.setVerticalAxisTitle("Calorii");

                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            return "ziua " + super.formatLabel(value + 1, isValueX);
                        }
                        return super.formatLabel(value, isValueX);
                    }
                });

                series.setColor(Color.GREEN);
                series.setTitle("calorii consumate");
                series.setSpacing(20);

                series1.setColor(Color.RED);
                series1.setTitle("necesar caloric");

                graph.getLegendRenderer().setVisible(true);
                graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

                graph.getViewport().setScrollable(true);
                graph.getViewport().setXAxisBoundsManual(true);

                graph.addSeries(series);
                graph.addSeries(series1);

                mReference.child("Evidenta").child(mAuth.getCurrentUser().getUid()).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
