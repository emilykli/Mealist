package com.example.mealist.Home;

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
import java.util.Date;


public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    TextView mTvDateMonth;
    TextView mTvDateDay;

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvDateMonth = view.findViewById(R.id.tvDateMonth);
        mTvDateDay = view.findViewById(R.id.tvDateDay);
        setTvDates();
    }

    private void setTvDates() {
        LocalDate mDate = LocalDate.now();
        mTvDateMonth.setText(mDate.getMonth().toString());
        mTvDateDay.setText(""+mDate.getDayOfMonth());
    }


}