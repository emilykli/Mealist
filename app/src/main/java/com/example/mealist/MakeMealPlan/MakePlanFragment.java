package com.example.mealist.MakeMealPlan;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.AddRecipe.AddRecipeFragment;
import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.Backend.SpoonacularClient;
import com.example.mealist.Access.User;
import com.example.mealist.GroceryList.GroceryList;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

public class MakePlanFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    public static final String TAG = "MakePlanFragment";

    private SpoonacularClient mClient;

    private TextView mTvDatePicker;
    private String mDateString;

    private TextView mTvBreakfastMeals;
    private TextView mTvLunchMeals;
    private TextView mTvDinnerMeals;

    private Button mBtnGenerateMealPlan;
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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("dateString") != null) {
                mDateString = bundle.getString("dateString");
            }

            try {
                mBreakfast = new JSONArray(bundle.getString("breakfastArray"));
                mBreakfast = jsonObjectToRecipeArray(mBreakfast);
                mLunch = new JSONArray(bundle.getString("lunchArray"));
                mLunch = jsonObjectToRecipeArray(mLunch);
                mDinner = new JSONArray(bundle.getString("dinnerArray"));
                mDinner = jsonObjectToRecipeArray(mDinner);

            } catch (JSONException e) {
                e.printStackTrace();
            }

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

        mClient = new SpoonacularClient();

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

        if (mDateString != null) {
            mTvDatePicker.setText(mDateString);
        }

        mTvDatePicker.setOnClickListener(v -> {
            showDatePickerDialog();
            getSetMealPlan();
        });

        mBtnSubmitMealPlan = view.findViewById(R.id.btnSubmitMealPlan);

        mBtnSubmitMealPlan.setOnClickListener(v -> {
            try {
                submitMealPlan();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mBtnGenerateMealPlan = view.findViewById(R.id.btnGenerateMealPlan);

        mBtnGenerateMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateMealPlan();
            }
        });
    }

    private void generateMealPlan(){
        mClient.generateMealPlan(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, json.toString());
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray meals = jsonObject.getJSONArray("meals");

                    for(int i = 0; i < meals.length(); i++) {

                        JSONObject jsonMeal = meals.getJSONObject(i);
                        Recipe recipe = new Recipe();
                        String name = jsonMeal.getString("title");
                        recipe.setName(name);
                        int id = jsonMeal.getInt("id");
                        recipe.setSpoonacularId(id);
                        recipe.setInstructions(jsonMeal.getString("sourceUrl"));
                        mClient.getRecipeInformation(id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject jsonObject = json.jsonObject;
                                List<Ingredient> ingredients = new ArrayList<>();

                                try {
                                    JSONArray extendedIngredients = jsonObject.getJSONArray("extendedIngredients");
                                    for(int i = 0; i < extendedIngredients.length(); i++) {
                                        JSONObject extendedIngredient = (JSONObject) extendedIngredients.get(i);
                                        Ingredient ingredient = new Ingredient();
                                        ingredient.setName(extendedIngredient.getString("name"));
                                        ingredient.setQuantity((Number) extendedIngredient.get("amount"));
                                        String unit = extendedIngredient.getString("unit");
                                        if (unit.isEmpty()) {
                                            unit = "(whole)";
                                        }
                                        ingredient.setUnit(unit);
                                        ingredient.setAisle(extendedIngredient.getString("aisle"));

                                        ingredients.add(ingredient);
                                    }
                                    Ingredient.saveAllInBackground(ingredients, e -> {
                                        if (e != null) {
                                            Log.e(TAG, "saving all ingredients failed");
                                        }
                                    });

                                    recipe.addAll(Recipe.KEY_INGREDIENTS, ingredients);
                                    recipe.setInstructions(jsonObject.getString("sourceUrl"));
                                    JSONArray nutrients = (JSONArray) ((JSONObject) jsonObject.getJSONObject("nutrition")).getJSONArray("nutrients");
                                    for(int nutrientIndex = 0; nutrientIndex < nutrients.length(); nutrientIndex++) {
                                        JSONObject nutrient = (JSONObject) nutrients.get(nutrientIndex);
                                        String name = nutrient.getString("name");
                                        switch(name) {
                                            case "Calories":
                                                recipe.setCalories(nutrient.getDouble("amount"));
                                                break;
                                            case "Fat":
                                                recipe.setFat(nutrient.getDouble("amount"));
                                                break;
                                            case "Carbohydrates":
                                                recipe.setCarbs(nutrient.getDouble("amount"));
                                                break;
                                            case "Protein":
                                            default:
                                                recipe.setProtein(nutrient.getDouble("amount"));
                                                break;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                recipe.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(com.parse.ParseException e) {
                                        if (e != null) {
                                            Toast.makeText(getContext(), "error while saving recipe", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });

                        switch(i) {
                            case 0:
                                mBreakfast.put(recipe);
                                mTvBreakfastMeals.append(name + "\n");
                                break;
                            case 1:
                                mLunch.put(recipe);
                                mTvLunchMeals.append(name + "\n");
                                break;
                            case 2:
                                mDinner.put(recipe);
                                mTvDinnerMeals.append(name + "\n");
                                break;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }

    private void populateMeals() throws JSONException {
        if (mBreakfast.length() > 0) {
            String breakfastString = "";
            for (int i = 0; i < mBreakfast.length(); i++) {
                Log.i(TAG, mBreakfast.toString());
                Recipe recipe = (Recipe) mBreakfast.get(i);
                breakfastString += recipe.getName() + "\n";
            }
            mTvBreakfastMeals.setText(breakfastString);
        }
        if (mLunch.length() > 0) {
            String lunchString = "";
            for (int i = 0; i < mLunch.length(); i++) {
                Recipe recipe = (Recipe) mLunch.get(i);
                lunchString += recipe.getName() + "\n";
            }
            mTvLunchMeals.setText(lunchString);
        }
        if (mDinner.length() > 0) {
            String dinnerString = "";
            for (int i = 0; i < mDinner.length(); i++) {
                Recipe recipe = (Recipe) mDinner.get(i);
                dinnerString += recipe.getName() + "\n";
            }
            mTvDinnerMeals.setText(dinnerString);
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
        mDateString = monthString + "/" + dayString + "/" + year;
        mTvDatePicker.setText(mDateString);

        mDateFromString(mDateString);
    }

    public void mDateFromString(String date) {
        try {
            mDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
            Log.i(TAG, mDate.toString());
        } catch (ParseException e) {
            return;
        }
    }

    public void saveRecipes(JSONArray recipes) throws JSONException {
        for (int i = 0 ; i < recipes.length(); i++) {
            Recipe recipe = (Recipe) recipes.get(i);
            ParseUser user = ParseUser.getCurrentUser();
            int cheap = user.getInt(User.KEY_CHEAP_PREFERENCE);
            int dairyFree = user.getInt(User.KEY_DAIRY_FREE_PREFERENCE);
            int vegetarian = user.getInt(User.KEY_VEGETARIAN_PREFERENCE);

            if (recipe.getCheap()) {
                cheap += 1;
            }
            else {
                cheap -= 1;
            }

            if (recipe.getDairyFree()) {
                dairyFree += 1;
            }

            else {
                dairyFree -= 1;
            }

            if (recipe.getVegetarian()) {
                vegetarian += 1;
            }

            else {
                vegetarian -= 1;
            }

            user.put(User.KEY_CHEAP_PREFERENCE, cheap);
            user.put(User.KEY_DAIRY_FREE_PREFERENCE, dairyFree);
            user.put(User.KEY_VEGETARIAN_PREFERENCE, vegetarian);

            recipe.saveInBackground(e -> Log.i(TAG, recipe.getName() + " saved"));
        }
    }

    public void submitMealPlan() throws JSONException {
        if (mDateString.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        mDateFromString(mDateString);
        mMealPlan = new MealPlan();
        saveRecipes(mBreakfast);
        saveRecipes(mLunch);
        saveRecipes(mDinner);
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
                ParseQuery<GroceryList> listQuery = ParseQuery.getQuery(GroceryList.class);
                listQuery.whereLessThanOrEqualTo(GroceryList.KEY_START_DATE, mDate);
                listQuery.whereGreaterThanOrEqualTo(GroceryList.KEY_END_DATE, mDate);
                listQuery.whereEqualTo(GroceryList.KEY_OWNER, ParseUser.getCurrentUser());
                listQuery.setLimit(1);

                listQuery.findInBackground(new FindCallback<GroceryList>() {
                    @Override
                    public void done(List<GroceryList> lists, com.parse.ParseException e) {
                        if (lists.size() > 0) {
                            GroceryList oldList = lists.get(0);
                            oldList.deleteInBackground();
                        }
                    }
                });

                Toast.makeText(getContext(), "Meal plan saved successfully", Toast.LENGTH_SHORT).show();
                mTvDatePicker.setText("");
                mTvBreakfastMeals.setText("");
                mTvLunchMeals.setText("");
                mTvDinnerMeals.setText("");
                mBreakfast = new JSONArray();
                mLunch = new JSONArray();
                mDinner = new JSONArray();
                mDateString = "";
                mDate = null;
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
                    Log.i(TAG, "no meal plan found");
                    mMealPlan = new MealPlan();
                }

                else {
                    Log.i(TAG, "found the meal plan");
                    mMealPlan = plans.get(0);
                    mBreakfast = mMealPlan.getBreakfast();
                    Log.i(TAG, mBreakfast.toString());
                    mLunch = mMealPlan.getLunch();
                    Log.i(TAG, mLunch.toString());
                    mDinner = mMealPlan.getDinner();
                    Log.i(TAG, mDinner.toString());

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
            meal.putString("date", mTvDatePicker.getText().toString());
            try {
                meal.putString("breakfastArray", recipeToJsonObjectArray(mBreakfast).toString());
                meal.putString("lunchArray", recipeToJsonObjectArray(mLunch).toString());
                meal.putString("dinnerArray", recipeToJsonObjectArray(mDinner).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

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

    public JSONArray recipeToJsonObjectArray(JSONArray recipes) throws JSONException {
        JSONArray newRecipes = new JSONArray();
        for(int i = 0; i < recipes.length(); i++) {
            Recipe recipe = (Recipe) recipes.get(i);
            newRecipes.put(recipe.toJsonObject());
        }
        return newRecipes;
    }

    public JSONArray jsonObjectToRecipeArray(JSONArray recipes) throws JSONException {
        JSONArray newRecipes = new JSONArray();
        for(int i = 0; i < recipes.length(); i++) {
            JSONObject object = (JSONObject) recipes.get(i);
            Recipe recipe = new Recipe();
            recipe.setName(object.getString(Recipe.KEY_NAME));
            recipe.setIngredients(object.getJSONArray(Recipe.KEY_INGREDIENTS));
            recipe.setImageLink(object.getString(Recipe.KEY_IMAGE_LINK));
            recipe.setSpoonacularId(object.getInt(Recipe.KEY_SPOONACULAR_ID));
            recipe.setCalories(object.getDouble(Recipe.KEY_CALORIES));
            recipe.setCarbs(object.getDouble(Recipe.KEY_CARBS));
            recipe.setFat(object.getDouble(Recipe.KEY_FAT));
            recipe.setProtein(object.getDouble(Recipe.KEY_PROTEIN));
            recipe.setInstructions(object.getString(Recipe.KEY_INSTRUCTIONS));
            recipe.setCheap(object.getBoolean(Recipe.KEY_CHEAP));
            recipe.setDairyFree(object.getBoolean(Recipe.KEY_CHEAP));
            recipe.setVegetarian(object.getBoolean(Recipe.KEY_VEGETARIAN));

            recipe.saveInBackground(e -> Log.i(TAG, recipe.getName() + " got saved"));
            newRecipes.put(recipe);
        }
        return newRecipes;
    }
}