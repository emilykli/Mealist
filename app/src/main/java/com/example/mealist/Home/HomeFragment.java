package com.example.mealist.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mealist.Access.TextGradient;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.MakeMealPlan.MealPlan;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "HomeFragment";

    TextView mTvDateMonth;
    TextView mTvDateDay;

    TextView mTvBreakfastMeals;
    TextView mTvLunchMeals;
    TextView mTvDinnerMeals;

    List<Recipe> mBreakfast;
    List<Recipe> mLunch;
    List<Recipe> mDinner;

    TextView mTvCalorieValue;
    TextView mTvCarbValue;
    TextView mTvFatValue;
    TextView mTvProteinValue;


    Date mDate;

    double mCalories;
    double mCarbs;
    double mFat;
    double mProtein;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvDateMonth = view.findViewById(R.id.tvDateMonth);
        mTvDateDay = view.findViewById(R.id.tvDateDay);
        setTvDates();

        mTvBreakfastMeals = view.findViewById(R.id.tvBreakfastMeals);
        mTvLunchMeals = view.findViewById(R.id.tvLunchMeals);
        mTvDinnerMeals = view.findViewById(R.id.tvDinnerMeals);

        mBreakfast = new ArrayList<>();
        mLunch = new ArrayList<>();
        mDinner = new ArrayList<>();

        setMealOnClickListeners();

        mTvCalorieValue = view.findViewById(R.id.tvCalorieValue);
        mTvCarbValue = view.findViewById(R.id.tvCarbValue);
        mTvFatValue = view.findViewById(R.id.tvFatValue);
        mTvProteinValue = view.findViewById(R.id.tvProteinValue);

        queryMeals();
    }

    private void setTvDates() {
        LocalDate date = LocalDate.now();
        mTvDateMonth.setText(date.getMonth().toString());
        mTvDateDay.setText(""+date.getDayOfMonth());

        mDate = java.sql.Date.valueOf(String.valueOf(date));
    }

    private void queryMeals() {
        ParseQuery<MealPlan> query = ParseQuery.getQuery(MealPlan.class);
        query.whereEqualTo(MealPlan.KEY_OWNER, ParseUser.getCurrentUser());
        query.whereEqualTo(MealPlan.KEY_DAY_OF, mDate);
        query.findInBackground(new FindCallback<MealPlan>() {
            @Override
            public void done(List<MealPlan> plans, ParseException e) {
                String breakfastText = "";
                String lunchText = "";
                String dinnerText = "";

                for (int i = 0; i < plans.size(); i++) {
                    MealPlan plan = plans.get(i);
                    List<Recipe> breakfast = (ArrayList) plan.get(MealPlan.KEY_BREAKFAST);
                    List<Recipe> lunch = (ArrayList) plan.get(MealPlan.KEY_LUNCH);
                    List<Recipe> dinner = (ArrayList) plan.get(MealPlan.KEY_DINNER);

                    if (breakfast != null) {
                        mBreakfast.addAll(breakfast);
                    }
                    if (lunch != null) {
                        mLunch.addAll(lunch);
                    }
                    if (dinner != null) {
                        mDinner.addAll(dinner);
                    }

                    breakfastText += mealPlanToString(breakfast);
                    lunchText += mealPlanToString(lunch);
                    dinnerText += mealPlanToString(dinner);

                }

                if (!breakfastText.isEmpty()) {
                    mTvBreakfastMeals.setText(breakfastText);
                }
                if (!lunchText.isEmpty()) {
                    mTvLunchMeals.setText(lunchText);
                }
                if (!dinnerText.isEmpty()) {
                    mTvDinnerMeals.setText(dinnerText);
                }

                mTvCalorieValue.setText(roundToHundrethsPlace(mCalories) + "");
                mTvCarbValue.setText(roundToHundrethsPlace(mCarbs) + "");
                mTvFatValue.setText(roundToHundrethsPlace(mFat) + "");
                mTvProteinValue.setText(roundToHundrethsPlace(mProtein) + "");
            }
        });
    }

    private double roundToHundrethsPlace(double d) {
        return Math.round(d*100.0)/100.0;
    }

    private String mealPlanToString(List<Recipe> mealPlan) {
        String result = "";

        for (int i = 0; i < mealPlan.size(); i++) {
            Recipe recipe = mealPlan.get(i);
            try {
                result += recipe.fetchIfNeeded().getString(Recipe.KEY_NAME) + "\n";
                mCalories += recipe.getCalories();
                mCarbs += recipe.getCarbs();
                mFat += recipe.getFat();
                mProtein += recipe.getProtein();
            }
            catch (Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }

        return result;
    }

    public void setMealOnClickListeners() {
        mTvBreakfastMeals.setOnClickListener(this);
        mTvLunchMeals.setOnClickListener(this);
        mTvDinnerMeals.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvBreakfastMeals || v.getId() == R.id.tvLunchMeals || v.getId() == R.id.tvDinnerMeals) {
            String mealTime;
            String mealInfo = "";
            switch (v.getId()){
                case R.id.tvBreakfastMeals:
                    mealTime = "Breakfast";
                    for (Recipe meal: mBreakfast) {
                        mealInfo += meal.getName() + "\n";
                        mealInfo += "Recipe Link: " + meal.getInstructions() + "\n";
                    }
                    break;
                case R.id.tvLunchMeals:
                    mealTime = "Lunch";
                    for (Recipe meal: mLunch) {
                        mealInfo += meal.getName() + "\n";
                        mealInfo += "Recipe Link: " + meal.getInstructions() + "\n";
                    }
                    break;
                case R.id.tvDinnerMeals:
                default:
                    mealTime = "Dinner";
                    for (Recipe meal: mDinner) {
                        mealInfo += meal.getName() + "\n";
                        mealInfo += "Recipe Link: " + meal.getInstructions() + "\n";
                    }
                    break;
            }
            if (mealInfo.isEmpty()) {
                mealInfo = "No recipes added";
            }
            launchHomeDialogFragment(mealTime, mealInfo);
            }
    }

    public void launchHomeDialogFragment(String mealTime, String mealInfo) {
        FragmentManager fm = getFragmentManager();
        HomeDialogFragment homeDialogFragment = HomeDialogFragment.newInstance(mealTime, mealInfo);
        homeDialogFragment.setTargetFragment(HomeFragment.this, 300);
        homeDialogFragment.show(fm, "fragment_edit_name");
    }
}