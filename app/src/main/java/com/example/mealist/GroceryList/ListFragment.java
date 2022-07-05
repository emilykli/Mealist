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
import android.widget.TextView;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.MakeMealPlan.MealPlan;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListFragment extends Fragment {
    public static final String TAG = "ListFragment";

    private TextView mTvStartDate;
    private TextView mTvEndDate;

    private LocalDate mStart;
    private LocalDate mEnd;

    private RecyclerView mRvGroceryList;
    private ListAdapter mAdapter;
    public List<Ingredient> mAllIngredients;


    long startTime;
    long startQuery;
    long finishQuery;
    long addIngredients;


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
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTime = System.currentTimeMillis();

        mTvStartDate = view.findViewById(R.id.tvStartDate);
        mTvEndDate = view.findViewById(R.id.tvEndDate);
        setStartEndDates();

        mRvGroceryList = view.findViewById(R.id.rvGroceryList);
        mAllIngredients = new ArrayList<>();
        mAdapter = new ListAdapter(getContext(), mAllIngredients);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRvGroceryList.setAdapter(mAdapter);
        mRvGroceryList.setLayoutManager(linearLayoutManager);

        queryGroceryList();
//        queryMealPlans();
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
                mAdapter.clear();
                mAdapter.addAll(mAllIngredients);
                mAdapter.notifyDataSetChanged();
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
            JSONArray ingredients = new JSONArray();
            for (Ingredient ingredient: mAllIngredients) {
                ingredients.put(ingredient);
            }
            Log.i(TAG, (ingredients).toString());
            list.setIngredients(ingredients);
            list.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i(TAG, "done saving grocery list");
                }
            });
        });
    }

    private void addIngredientsToList(List<Recipe> meal) throws ParseException {
        for (Recipe recipe : meal) {
            List<Ingredient> ingredients = (ArrayList) recipe.fetchIfNeeded().get(Recipe.KEY_INGREDIENTS);
            for (Ingredient ingredient : ingredients) {
                mAllIngredients.add(ingredient);
                mAdapter.notifyItemInserted(mAllIngredients.size() - 1);
            }
        }
    }
}