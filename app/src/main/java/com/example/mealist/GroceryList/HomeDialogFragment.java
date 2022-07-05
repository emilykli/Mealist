package com.example.mealist.GroceryList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mealist.R;

import java.util.ArrayList;

public class HomeDialogFragment extends DialogFragment {

    private TextView mTvMealTime;
    private TextView mTvMealInfo;

    public HomeDialogFragment() {
        // Required empty public constructor
    }

    // TODO: pass around recipes agaiñ :\
    public static HomeDialogFragment newInstance(String mealTime) {
        HomeDialogFragment fragment = new HomeDialogFragment();
        Bundle args = new Bundle();
        args.putString("mealTime", mealTime);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvMealTime = (TextView) view.findViewById(R.id.tvMealTime);
        mTvMealInfo = (TextView) view.findViewById(R.id.tvMealInfo);

        String mealTime = getArguments().getString("mealTime", "No Meal Selected");
        getDialog().setTitle(mealTime);

        mTvMealInfo.requestFocus();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}