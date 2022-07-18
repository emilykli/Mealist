package com.example.mealist.GroceryList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.MakeMealPlan.MealPlan;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ListFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "ListFragment";
    public static final String[] GRAINS = {"Pasta and Rice", "Bakery/Bread", "Cereal", "Bread"};
    public static final String[] PANTRY = {"Baking", "Spices and Seasonings", "Nut butters, Jams, and Honey",
            "Oil, Vinegar, Salad Dressing", "Condiments"};
    public static final String[] COLD_PRESERVED = {"Refrigerated", "Frozen", "Canned and Jarred"};
    public static final String[] DAIRY= {"Milk, Eggs, Other Dairy", "Cheese"};
    public static final String[] MEAT = {"Meat", "Seafood"};
    public static final String[] DRINKS = {"Tea and Coffee", "Beverages", "Alcoholic Beverages"};
    public static final String[] NUTS_FRUITS_VEG = {"Produce", "Dried Fruits", "Nuts"};


    private static SectionedRecyclerViewAdapter mSectionedAdapter;

    private TextView mTvStartDate;
    private TextView mTvEndDate;

    private ImageButton mBtnLeft;
    private ImageButton mBtnRight;

    private LocalDate mStart;
    private LocalDate mEnd;

    private RecyclerView mRvGroceryList;
    public List<Ingredient> mAllIngredients;


    long startTime;
    long startQuery;
    long finishQuery;
    long addIngredients;


    public ListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTime = System.currentTimeMillis();

        mTvStartDate = view.findViewById(R.id.tvStartDate);
        mTvEndDate = view.findViewById(R.id.tvEndDate);
        setStartEndDates();

        mBtnLeft = view.findViewById(R.id.btnLeft);
        mBtnRight = view.findViewById(R.id.btnRight);
        setButtonOnClickListeners();

        mRvGroceryList = view.findViewById(R.id.rvGroceryList);

        resetRecyclerView();

        queryGroceryList();
    }

    private void setButtonOnClickListeners() {
        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                mEnd = mStart.minusDays(1);
                mStart = mStart.minusDays(7);
                break;
            case R.id.btnRight:
                mStart = mEnd.plusDays(1);
                mEnd = mEnd.plusDays(7);
                break;
        }
        mTvStartDate.setText(mStart.getMonth().toString() + " " + mStart.getDayOfMonth());
        mTvEndDate.setText(mEnd.getMonth().toString() + " " + mEnd.getDayOfMonth());

        resetRecyclerView();
        queryGroceryList();

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

        mStart = start;
        mEnd = end;

        mTvStartDate.setText(start.getMonth().toString() + " " + start.getDayOfMonth());
        mTvEndDate.setText(end.getMonth().toString() + " " + end.getDayOfMonth());
    }

    private void resetRecyclerView() {
        mAllIngredients = new ArrayList<>();
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRvGroceryList.setAdapter(mSectionedAdapter);
        mRvGroceryList.setLayoutManager(linearLayoutManager);
    }

    private void queryGroceryList() {
        startQuery = System.currentTimeMillis();

        Date startDate = Date.from(mStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(mEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

        ParseQuery<GroceryList> listQuery = ParseQuery.getQuery(GroceryList.class);
        listQuery.whereEqualTo(GroceryList.KEY_OWNER, ParseUser.getCurrentUser());
        listQuery.whereEqualTo(GroceryList.KEY_END_DATE, endDate);
        listQuery.whereEqualTo(GroceryList.KEY_START_DATE, startDate);
        listQuery.setLimit(1);

        listQuery.findInBackground(new FindCallback<GroceryList>() {
            @Override
            public void done(List<GroceryList> lists, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting grocery list");
                    return;
                }
                if (lists.size() == 0) {
                    queryMealPlans();
                    return;
                }


                GroceryList myList = lists.get(0);
                mAllIngredients = myList.getIngredients();

                for (String aisle: GroceryList.AISLES) {
                    List<Ingredient> myAisle = myList.getAisle(aisle);
                    if (myAisle.size() > 0) {
                        mSectionedAdapter.addSection(new AisleSection(aisle, myAisle));
                    }
                }

                mSectionedAdapter.notifyDataSetChanged();
                addIngredients = System.currentTimeMillis();
                Log.i("time", "finish adding everything: " + (addIngredients - startTime));
            }
        });
    }

    private void queryMealPlans() {
        startQuery = System.currentTimeMillis();
        Log.i("time", "get to query: " + (startQuery - startTime));
        Date startDate = Date.from(mStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(mEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

        ParseQuery<MealPlan> mealQuery = ParseQuery.getQuery(MealPlan.class);
        mealQuery.whereLessThanOrEqualTo(MealPlan.KEY_DAY_OF, endDate);
        mealQuery.whereGreaterThanOrEqualTo(MealPlan.KEY_DAY_OF, startDate);
        mealQuery.whereEqualTo(MealPlan.KEY_OWNER, ParseUser.getCurrentUser());

        mealQuery.findInBackground((plans, e) -> {
            finishQuery = System.currentTimeMillis();
            Log.i("time", "finish query: "+  (finishQuery - startQuery));
            if (e != null) {
                Log.e(TAG, "issue with getting meal plans", e);
                return;
            }

            for (MealPlan plan : plans) {
                List<Recipe> breakfast = (ArrayList) plan.get(MealPlan.KEY_BREAKFAST);
                List<Recipe> lunch = (ArrayList) plan.get(MealPlan.KEY_LUNCH);
                List<Recipe> dinner = (ArrayList) plan.get(MealPlan.KEY_DINNER);

                try {
                    addIngredientsToList(breakfast);
                    addIngredientsToList(lunch);
                    addIngredientsToList(dinner);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                addIngredients = System.currentTimeMillis();
                Log.i("time", "finish adding ingredients: " + (addIngredients - finishQuery));
            }
            GroceryList list = new GroceryList();
            list.setOwner(ParseUser.getCurrentUser());
            list.setStartDate(startDate);
            list.setEndDate(endDate);

            for (Ingredient ingredient: mAllIngredients) {
                ingredient.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        String aisle = getAisleGrouping(ingredient.getAisle());
                        list.add(aisle, ingredient);
                        list.saveInBackground();
                    }
                });
            }

            list.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    int numAisles = 0;
                    for (String aisle: GroceryList.AISLES) {
                        List<Ingredient> myAisle = list.getAisle(aisle);
                        if (myAisle.size() > 0) {
                            mSectionedAdapter.addSection(new AisleSection(aisle, myAisle));
                            mSectionedAdapter.notifyItemInserted(numAisles);
                            numAisles++;
                        }
                    }
                }
            });

        });

    }

    public static String getAisleGrouping(String aisle) {

        for (String name: GRAINS) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_GRAINS;
            }
        }

        for (String name : PANTRY) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_PANTRY;
            }
        }

        for (String name : COLD_PRESERVED) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_COLD_PRESERVED;
            }
        }

        for (String name : DAIRY) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_DAIRY;
            }
        }

        for (String name : MEAT) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_MEAT;
            }
        }

        for (String name : DRINKS) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_DRINKS;
            }
        }

        for (String name : NUTS_FRUITS_VEG) {
            if (aisle.contains(name)) {
                return GroceryList.KEY_NUTS_FRUIT_VEG;
            }
        }

        return GroceryList.KEY_OTHER;
    }

    private void addIngredientsToList(List<Recipe> meal) throws ParseException {
        for (Recipe recipe : meal) {
            List<Ingredient> ingredients = (ArrayList) recipe.fetchIfNeeded().get(Recipe.KEY_INGREDIENTS);
            for (Ingredient ingredient : ingredients) {
                mAllIngredients.add(ingredient);
            }
        }
    }

    public static void headerClicked(AisleSection section) {
        final SectionAdapter sectionAdapter = mSectionedAdapter.getAdapterForSection(section);

        final boolean wasExpanded = section.isExpanded();
        final int previousItemsTotal = section.getContentItemsTotal();

        section.setExpanded(!wasExpanded);
        sectionAdapter.notifyHeaderChanged();

        if (wasExpanded) {
            sectionAdapter.notifyItemRangeRemoved(0, previousItemsTotal);
        } else {
            sectionAdapter.notifyAllItemsInserted();
        }
    }
}