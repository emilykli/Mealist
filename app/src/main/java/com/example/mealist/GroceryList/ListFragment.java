package com.example.mealist.GroceryList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mealist.R;

import java.time.LocalDate;

public class ListFragment extends Fragment {
    public static final String TAG = "ListFragment";

    private TextView mTvStartDate;
    private TextView mTvEndDate;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvStartDate = view.findViewById(R.id.tvStartDate);
        mTvEndDate = view.findViewById(R.id.tvEndDate);
        setStartEndDates();
    }

    private void setStartEndDates() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        while (!start.getDayOfWeek().toString().equals("SUNDAY")) {
            start = start.minusDays(1);
        }

        while (!end.getDayOfWeek().toString().equals("SATURDAY")) {
            end = end.plusDays(1);
        }

        mTvStartDate.setText(start.getMonth().toString() + " " + start.getDayOfMonth());
        mTvEndDate.setText(end.getMonth().toString() + " " + end.getDayOfMonth());
    }
}