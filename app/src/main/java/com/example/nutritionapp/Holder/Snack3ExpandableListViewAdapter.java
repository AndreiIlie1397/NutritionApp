package com.example.nutritionapp.Holder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.nutritionapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class Snack3ExpandableListViewAdapter extends BaseExpandableListAdapter {
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mReference = mDatabase.getReference();
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    private Context context;
    private List<String> headerItem;
    private HashMap<String, List<String>> childItem;

    public Snack3ExpandableListViewAdapter(Context context, List<String> headerItem, HashMap<String, List<String>> childItem) {
        this.context = context;
        this.headerItem = headerItem;
        this.childItem = childItem;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childItem.get(headerItem.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_items, null);
        }
        TextView tv = convertView.findViewById(R.id.tvChildItem);
        tv.setText(childText);

        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItem.get(headerItem.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerItem.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return headerItem.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {

        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_items, null);
        }
        TextView tv = convertView.findViewById(R.id.tvItemHeader);
        tv.setText(headerTitle);

        Button btn = convertView.findViewById(R.id.btn_delete);
        btn.setFocusable(false);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Sunteti sigur ca vreti sa stergeti alimentul?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Query query = mReference.child("Jurnal").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("snack3");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                double calorii, carbohidrati, grasimi, proteine;
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    if (childSnapshot.getKey().equals(headerTitle)) {
                                        calorii = Double.valueOf(childSnapshot.child("calorii").getValue().toString());
                                        carbohidrati = Double.valueOf(childSnapshot.child("carbohidrati").getValue().toString());
                                        grasimi = Double.valueOf(childSnapshot.child("grasimi").getValue().toString());
                                        proteine = Double.valueOf(childSnapshot.child("proteine").getValue().toString());
                                        childSnapshot.getRef().removeValue();

                                        final double finalCalorii = calorii;
                                        final double finalCarbohidrati = carbohidrati;
                                        final double finalGrasimi = grasimi;
                                        final double finalProteine = proteine;
                                        mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                double ncalorii, ncarbohidrati, ngrasimi, nproteine;
                                                double progress_total, progress_total_carbohidrati, progress_total_grasimi, progress_total_proteine;
                                                progress_total = Double.valueOf(dataSnapshot.child("total").getValue().toString());
                                                progress_total_carbohidrati = Double.valueOf(dataSnapshot.child("totalCarbohidrati").getValue().toString());
                                                progress_total_grasimi = Double.valueOf(dataSnapshot.child("totalGrasimi").getValue().toString());
                                                progress_total_proteine = Double.valueOf(dataSnapshot.child("totalProteine").getValue().toString());
                                                ncalorii = progress_total - finalCalorii;
                                                ncarbohidrati = progress_total_carbohidrati - finalCarbohidrati;
                                                ngrasimi = progress_total_grasimi - finalGrasimi;
                                                nproteine = progress_total_proteine - finalProteine;

                                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("total").setValue(String.valueOf(round(ncalorii, 2)));
                                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("totalCarbohidrati").setValue(String.valueOf(round(ncarbohidrati, 2)));
                                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("totalGrasimi").setValue(String.valueOf(round(ngrasimi, 2)));
                                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).child("totalProteine").setValue(String.valueOf(round(nproteine, 2)));
                                                mReference.child("Evidenta").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentDate).removeEventListener(this);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                                        });
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Anuleaza", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}