package com.example.mealist.MakeMealPlan;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealist.AddRecipe.AddRecipeFragment;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MakePlanFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    public static final String TAG = "MakePlanFragment";

    private TextView mTvDatePicker;
    private TextView mTvBreakfastMeals;
    private TextView mTvLunchMeals;
    private TextView mTvDinnerMeals;
    private Button mBtnSubmitMealPlan;

    private JSONArray mBreakfast = new JSONArray();
    private JSONArray mLunch = new JSONArray();
    private JSONArray mDinner = new JSONArray();

    private Date mDate;
    private MealPlan mMealPlan;


    public MakePlanFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getParentFragmentManager().setFragmentResultListener("makePlanFragment", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
//                // TODO: get the recipe
//                Recipe result = (Recipe) bundle.getParcelable("recipe");
//            }
//        });
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getParcelable("breakfastRecipe") != null) {
                Recipe breakfast = bundle.getParcelable("breakfastRecipe");
                Log.i(TAG, "breakfast: " + breakfast.getName());
                mBreakfast.put(breakfast);
            }
            if (bundle.getParcelable("lunchRecipe") != null) {
                Recipe lunch = bundle.getParcelable("lunchRecipe");
                Log.i(TAG, "lunch: " + lunch.getName());
                mLunch.put(lunch);
            }
            if (bundle.getParcelable("dinnerRecipe") != null) {
                Recipe dinner = bundle.getParcelable("dinnerRecipe");
                Log.i(TAG, "dinner: " + dinner.getName());
                mDinner.put(dinner);
            }

        }
        else {
            Log.i(TAG, "bundle was null");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_make_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvBreakfastMeals = view.findViewById(R.id.tvBreakfastMeals);
        mTvLunchMeals = view.findViewById(R.id.tvLunchMeals);
        mTvDinnerMeals = view.findViewById(R.id.tvDinnerMeals);

        try {
            populateMeals();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setMealOnClickListeners();

        mDate = null;

        mTvDatePicker = view.findViewById(R.id.tvDatePicker);

        mTvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                getSetMealPlan();
            }
        });

        mBtnSubmitMealPlan = view.findViewById(R.id.btnSubmitMealPlan);

        mBtnSubmitMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMealPlan();
            }
        });
    }

    private void populateMeals() throws JSONException {
        if (mBreakfast.length() > 0) {
            for (int i = 0; i < mBreakfast.length(); i++) {
                Recipe recipe = (Recipe) mBreakfast.get(i);
                mTvBreakfastMeals.setText(recipe.getName());
            }
        }
        if (mLunch.length() > 0) {
            for (int i = 0; i < mLunch.length(); i++) {
                Recipe recipe = (Recipe) mLunch.get(i);
                mTvLunchMeals.setText(recipe.getName());
            }
        }
        if (mDinner.length() > 0) {
            for (int i = 0; i < mDinner.length(); i++) {
                Recipe recipe = (Recipe) mDinner.get(i);
                mTvDinnerMeals.setText(recipe.getName());
            }
        }
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
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

    public void submitMealPlan() {
        if (mDate == null) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        mMealPlan.setOwner(ParseUser.getCurrentUser());
        mMealPlan.setDayOf(mDate);
        mMealPlan.setBreakfast(mBreakfast);
        mMealPlan.setLunch(mLunch);
        mMealPlan.setDinner(mDinner);

        mMealPlan.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error saving meal plan", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error saving meal plan", e);
                    return;
                }
                Toast.makeText(getContext(), "Meal plan saved successfully", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "meal plan saved");
            }
        });
    }

    public void getSetMealPlan() {
        ParseQuery<MealPlan> query = ParseQuery.getQuery(MealPlan.class);
        query.whereEqualTo(MealPlan.KEY_OWNER, ParseUser.getCurrentUser());
        query.whereEqualTo(MealPlan.KEY_DAY_OF, mDate);
        query.setLimit(1);
        query.findInBackground(new FindCallback<MealPlan>() {
            @Override
            public void done(List<MealPlan> plans, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting/setting meal plan", e);
                    return;
                }

                if (plans.size() == 0) {
                    mMealPlan = new MealPlan();
                }

                else {
                    mMealPlan = plans.get(0);
                }

            }
        });
    }

    public void setMealOnClickListeners() {
        mTvBreakfastMeals.setOnClickListener(this);
        mTvLunchMeals.setOnClickListener(this);
        mTvDinnerMeals.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvBreakfastMeals || v.getId() == R.id.tvLunchMeals || v.getId() == R.id.tvDinnerMeals) {
            Fragment fragment = new AddRecipeFragment();
            Bundle meal = new Bundle();
            switch(v.getId()){
                case R.id.tvBreakfastMeals:
                    meal.putString("meal", "breakfast");
                    break;
                case R.id.tvLunchMeals:
                    meal.putString("meal", "lunch");
                    break;
                case R.id.tvDinnerMeals:
                    meal.putString("meal", "dinner");
                    break;
            }
            fragment.setArguments(meal);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    }
}