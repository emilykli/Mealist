package com.example.mealist.Add;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.mealist.R;
import com.parse.Parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    public static final String TAG = "AddFragment";

    private TextView mTvDatePicker;

    private Date mDate;


    public AddFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvDatePicker = view.findViewById(R.id.tvDatePicker);

        mTvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String monthString;
        String dayString;
        if (month < 10) { monthString = "0" + month; }
        else { monthString = "" + month; }
        if (dayOfMonth < 10) { dayString = "0" + dayOfMonth; }
        else { dayString = "" + dayOfMonth; }
        String date = monthString + "/" + dayString + "/" + year;
        mTvDatePicker.setText(date);

        try {
            mDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
            Log.i(TAG, mDate.toString());
        } catch (ParseException e) {
            return;
        }
    }
}