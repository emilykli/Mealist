package com.example.mealist.AddRecipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.R;
import com.example.mealist.Backend.SpoonacularClient;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class AddRecipeFragment extends Fragment {
    public static final String TAG = "AddRecipeFragment";

    private TextView mTvMealName;

    protected static String mMealTime;
    protected static String mBreakfast;
    protected static String mLunch;
    protected static String mDinner;
    protected static String mMealName;

    private EditText mEtRecipeSearch;
    private Button mBtnSearch;

    private RecyclerView mRvRecipeSearchResults;
    private RecipeAdapter mAdapter;
    private List<Recipe> mRecipes;

    private ProgressBar mPbLoading;

    SpoonacularClient client;

    public AddRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMealTime = getArguments().getString("date", "");
        mBreakfast = getArguments().getString("breakfastArray");
        mLunch = getArguments().getString("lunchArray");
        mDinner = getArguments().getString("dinnerArray");
        mMealName = getArguments().getString("meal", "default meal");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = new SpoonacularClient();

        mTvMealName = view.findViewById(R.id.tvMealName);
        mTvMealName.setText(mMealName);

        mEtRecipeSearch = view.findViewById(R.id.etRecipeSearch);
        mBtnSearch = view.findViewById(R.id.btnSearch);
        setupSearchButton();

        mRvRecipeSearchResults = view.findViewById(R.id.rvRecipeSearchResults);

        mRecipes = new ArrayList<>();
        mAdapter = new RecipeAdapter(getContext(), mRecipes, getParentFragmentManager());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRvRecipeSearchResults.setAdapter(mAdapter);
        mRvRecipeSearchResults.setLayoutManager(linearLayoutManager);

        mPbLoading = view.findViewById(R.id.pbLoading);

    }

    public void setupSearchButton() {
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mEtRecipeSearch.getText().toString();
                if (searchQuery.isEmpty()) {
                    Toast.makeText(getContext(), "search cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPbLoading.setVisibility(ProgressBar.VISIBLE);
                client.getRecipes(searchQuery, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        mAdapter.clear();
                        addRecipes(jsonObject);
                        mPbLoading.setVisibility(ProgressBar.INVISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "search failed", throwable);
                    }
                });
                mEtRecipeSearch.setText("");

            }
        });
    }

    public void addRecipes(JSONObject jsonObject) {
        try {
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                Recipe recipe = new Recipe();
                JSONObject recipeJson = (JSONObject) results.get(i);

                int recipeId = recipeJson.getInt("id");

                JSONArray ingredients = new JSONArray();
                recipe.setIngredients(ingredients);
                recipe.setName(recipeJson.getString("title"));
                recipe.setImageLink(recipeJson.getString("image"));

                client.getIngredients(recipeId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray unprocessedIngredients = jsonObject.getJSONArray("ingredients");
                            for (int i = 0; i < unprocessedIngredients.length(); i++) {
                                JSONObject unprocessedIngredient = (JSONObject) unprocessedIngredients.get(i);
                                String name = unprocessedIngredient.getString("name");
                                Ingredient ingredient = new Ingredient();
                                ingredient.setName(unprocessedIngredient.getString("name"));
                                JSONObject metricAmount = unprocessedIngredient.getJSONObject("amount").getJSONObject("metric");
                                ingredient.setQuantity(metricAmount.getDouble("value"));
                                String unit = metricAmount.getString("unit");
                                if (unit.isEmpty()) {
                                    unit = "(whole)";
                                }
                                ingredient.setUnit(unit);
                                ingredient.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Toast.makeText(getContext(), "error getting singular ingredient", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                recipe.add(Recipe.KEY_INGREDIENTS, ingredient);

                                recipe.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Toast.makeText(getContext(), "error while saving", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "error getting ingredients array", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "error getting ingredients" + response, throwable);
                    }
                });
                mRecipes.add(recipe);
                mAdapter.notifyItemInserted(mRecipes.size() - 1);
            }
        } catch (JSONException e) {
            Log.e(TAG, "error getting results", e);
        }
    }


}