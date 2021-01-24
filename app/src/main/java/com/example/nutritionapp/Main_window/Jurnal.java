package com.example.nutritionapp.Main_window;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import com.example.nutritionapp.Jurnal.DinnerJurnalActivity;
import com.example.nutritionapp.Jurnal.LunchJurnalActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.Jurnal.BreakfastJurnalActivity;
import com.example.nutritionapp.Jurnal.Snack1JurnalActivity;
import com.example.nutritionapp.Jurnal.Snack2JurnalActivity;
import com.example.nutritionapp.Jurnal.Snack3JurnalActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Jurnal extends Fragment implements DatePickerDialog.OnDateSetListener {
    Button mBreakfast, mLunch, mDinner, mSnack1, mSnack2, mSnack3, mData;
    boolean clicked = false;
    String data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_jurnal, container, false);

        mBreakfast = mView.findViewById(R.id.breakfast);
        mLunch = mView.findViewById(R.id.lunch);
        mDinner = mView.findViewById(R.id.dinner);
        mSnack1 = mView.findViewById(R.id.snack1);
        mSnack2 = mView.findViewById(R.id.snack2);
        mSnack3 = mView.findViewById(R.id.snack3);
        mData = mView.findViewById(R.id.data);

        mData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
                clicked = true;
            }
        });
        mBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = mData.getText().toString();
                if (clicked == true &&  isValidDate(data)) {
                    Intent intent = new Intent(getActivity(), BreakfastJurnalActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Nu a fost selectata data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = mData.getText().toString();
                if (clicked == true &&  isValidDate(data)) {
                    Intent intent = new Intent(getActivity(), LunchJurnalActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Nu a fost selectata data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = mData.getText().toString();
                if (clicked == true &&  isValidDate(data)) {
                    Intent intent = new Intent(getActivity(), DinnerJurnalActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Nu a fost selectata data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSnack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = mData.getText().toString();
                if (clicked == true &&  isValidDate(data)) {
                    Intent intent = new Intent(getActivity(), Snack1JurnalActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Nu a fost selectata data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSnack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = mData.getText().toString();
                if (clicked == true &&  isValidDate(data)) {
                    Intent intent = new Intent(getActivity(), Snack2JurnalActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Nu a fost selectata data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSnack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = mData.getText().toString();
                if (clicked == true &&  isValidDate(data)) {
                    Intent intent = new Intent(getActivity(), Snack3JurnalActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Nu a fost selectata data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return mView;
    }

    private void showCalendar() {
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String zi = "", luna = "";
        if(dayOfMonth <= 9){
            zi = "0" + dayOfMonth;
        } else {
            zi = "" + dayOfMonth;
        }
        if(month < 9){
            luna = "0" + (month + 1);
        } else {
            luna = "" + (month + 1);
        }
        String date = zi + "-" + luna + "-" + year;
        mData.setText(date);
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}
