package com.example.mealist.AddRecipe;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.Access.User;
import com.example.mealist.R;
import com.example.mealist.Backend.SpoonacularClient;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import okhttp3.Headers;

public class AddRecipeFragment extends Fragment {
    public static final String TAG = "AddRecipeFragment";

    private TextView mTvMealName;
    private TextView mTvNoRecipes;

    protected static String mMealTime;
    protected static String mBreakfast;
    protected static String mLunch;
    protected static String mDinner;
    protected static String mMealName;

    private EditText mEtRecipeSearch;
    private Button mBtnSearch;

    private Button mBtnRecommend;

    private RecyclerView mRvRecipeSearchResults;
    private RecipeAdapter mAdapter;
    private List<Recipe> mRecipes;

    private PriorityQueue<Object[]> mPqRecipes;

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
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = new SpoonacularClient();

        mTvNoRecipes = view.findViewById(R.id.tvNoRecipes);

        mTvMealName = view.findViewById(R.id.tvMealName);
        mTvMealName.setText(mMealName);

        mEtRecipeSearch = view.findViewById(R.id.etRecipeSearch);
        mBtnSearch = view.findViewById(R.id.btnSearch);
        setupSearchButton();

        mBtnRecommend = view.findViewById(R.id.btnRecommend);
        setupRecommendButton();

        mRvRecipeSearchResults = view.findViewById(R.id.rvRecipeSearchResults);

        mRecipes = new ArrayList<>();
        mAdapter = new RecipeAdapter(getContext(), mRecipes, getParentFragmentManager());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRvRecipeSearchResults.setAdapter(mAdapter);
        mRvRecipeSearchResults.setLayoutManager(linearLayoutManager);

        mPbLoading = view.findViewById(R.id.pbLoading);

    }

    private void setupSearchButton() {
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPqRecipes = new PriorityQueue(SpoonacularClient.SEARCH_LIMIT, new RecommendationComparator());

                String searchQuery = mEtRecipeSearch.getText().toString();
                if (searchQuery.isEmpty()) {
                    Toast.makeText(getContext(), "search cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPbLoading.setVisibility(ProgressBar.VISIBLE);
                mTvNoRecipes.setVisibility(TextView.INVISIBLE);
                getRecipesFromApi(searchQuery);
                mEtRecipeSearch.setText("");
            }
        });
    }

    private void getRecipesFromApi(String searchQuery) {
        client.getRecipes(searchQuery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                mAdapter.clear();
                addRecipesFromApiCall(jsonObject, "results");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                if (SpoonacularClient.NUM_API_KEY_CHANGES >= SpoonacularClient.API_KEYS.length) {
                    Toast.makeText(getContext(), "Search failed!", Toast.LENGTH_SHORT).show();
                    mPbLoading.setVisibility(ProgressBar.INVISIBLE);
                }
                else {
                    SpoonacularClient.changeApiKey();
                    getRecipesFromApi(searchQuery);
                }
            }
        });
    }

    private void setupRecommendButton() {
        mBtnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPqRecipes = new PriorityQueue(SpoonacularClient.SEARCH_LIMIT, new RecommendationComparator());
                mPbLoading.setVisibility(ProgressBar.VISIBLE);

                generateRecommendations();

            }
        });
    }

    /**
     * Takes randomly generated meals and ranks them based on recommendation algorithm
     */
    private void generateRecommendations() {
        client.generateRandomMeals(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                mAdapter.clear();
                JSONObject jsonObject = json.jsonObject;
                addRecipesFromApiCall(jsonObject, "recipes");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                if (SpoonacularClient.NUM_API_KEY_CHANGES >= SpoonacularClient.API_KEYS.length) {
                    Toast.makeText(getContext(), "Failed generating recommendations!", Toast.LENGTH_SHORT).show();
                }
                else {
                    SpoonacularClient.changeApiKey();
                    generateRecommendations();
                }
            }
        });
    }

    private void addRecipesFromApiCall(JSONObject jsonObject, String resultsKey) {
        try {
            JSONArray results = jsonObject.getJSONArray(resultsKey);
            for (int i = 0; i < results.length(); i++) {
                JSONObject recipeJson = (JSONObject) results.get(i);

                int recipeId = recipeJson.getInt("id");
                processRecipeById(recipeId);

            }
        } catch (JSONException e) {
            Log.e(TAG, "error getting results", e);
        }
    }

    private void processExtendedIngredients(JSONArray extendedIngredients, List<Ingredient> ingredients) throws JSONException {
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
    }

    private void setRecipeProperties(Recipe recipe, JSONObject jsonObject, int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            recipe.setName(jsonObject.getString("title"));
            recipe.setImageLink(jsonObject.getString("image"));
            recipe.setSpoonacularId(recipeId);

            JSONArray extendedIngredients = jsonObject.getJSONArray("extendedIngredients");

            processExtendedIngredients(extendedIngredients, ingredients);

            Ingredient.saveAllInBackground(ingredients, e -> {
                if (e != null) {
                    Log.e(TAG, "saving all ingredients failed");
                }
            });

            recipe.addAll(Recipe.KEY_INGREDIENTS, ingredients);

            recipe.setInstructions(jsonObject.getString("sourceUrl"));
            recipe.setCheap(jsonObject.getBoolean(Recipe.KEY_CHEAP));
            recipe.setDairyFree(jsonObject.getBoolean(Recipe.KEY_DAIRY_FREE));
            recipe.setVegetarian(jsonObject.getBoolean(Recipe.KEY_VEGETARIAN));

            JSONArray nutrients = jsonObject.getJSONObject("nutrition").getJSONArray("nutrients");
            try {
                for (int nutrientIndex = 0; nutrientIndex < nutrients.length(); nutrientIndex++) {
                    JSONObject nutrient = (JSONObject) nutrients.get(nutrientIndex);
                    String name = nutrient.getString("name");
                        if (name.equals("Calories")) {
                            recipe.setCalories(nutrient.getDouble("amount"));
                        }
                        else if (name.equals("Fat")) {
                            recipe.setFat(nutrient.getDouble("amount"));
                        }
                        else if (name.equals("Carbohydrates")) {
                            recipe.setCarbs(nutrient.getDouble("amount"));
                        }
                        else if (name.equals("Protein")) {
                            recipe.setProtein(nutrient.getDouble("amount"));
                        }

                }
            }
            catch (Exception e) {
                recipe.setCalories(0.0);
                recipe.setFat(0.0);
                recipe.setCarbs(0.0);
                recipe.setProtein(0.0);
                Toast.makeText(getContext(), "Error retrieving nutrient info...", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processRecipeById(int recipeId) {
        Recipe recipe = new Recipe();

        client.getRecipeInformation(recipeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                setRecipeProperties(recipe, jsonObject, recipeId);

                recipe.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "error while saving", Toast.LENGTH_SHORT).show();
                        }

                        ParseUser user = ParseUser.getCurrentUser();

                        double recipePriority = getRecipePriority(user, recipe);

                        Object[] priorityRecipe = {recipe, recipePriority};
                        mPqRecipes.add(priorityRecipe);

                        if (mPqRecipes.size() == SpoonacularClient.SEARCH_LIMIT) {
                            while (!mPqRecipes.isEmpty()) {
                                Object[] prioRecipe = mPqRecipes.poll();
                                Recipe r = (Recipe) prioRecipe[0];
                                mRecipes.add(r);
                                mAdapter.notifyItemInserted(mRecipes.size() - 1);
                            }
                            mPbLoading.setVisibility(ProgressBar.INVISIBLE);
                        }

                        }
                });
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }

    public double getRecipePriority(ParseUser user, Recipe recipe) {
        double userCheap = user.getDouble(User.KEY_CHEAP_PREFERENCE);
        double userVegetarian = user.getDouble(User.KEY_VEGETARIAN_PREFERENCE);
        double userDairyFree = user.getDouble(User.KEY_DAIRY_FREE_PREFERENCE);

        double cheapMultiplier = recipe.getCheap() ? 1.0 : -1.0;
        double vegetarianMultiplier = recipe.getVegetarian() ? 1.0 : -1.0;
        double dairyFreeMultiplier = recipe.getDairyFree() ? 1.0 : -1.0;

        double recipePriority = userCheap * cheapMultiplier + userVegetarian * vegetarianMultiplier + userDairyFree * dairyFreeMultiplier;

        return recipePriority;
    }


}