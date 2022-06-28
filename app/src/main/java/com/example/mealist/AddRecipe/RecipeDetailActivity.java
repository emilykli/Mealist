package com.example.mealist.AddRecipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.Access.MainActivity;
import com.example.mealist.Backend.SpoonacularClient;
import com.example.mealist.MakeMealPlan.MakePlanFragment;
import com.example.mealist.R;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String TAG = "RecipeDetailActivity";

    private ImageView mIvRecipeImage;
    private TextView mTvRecipeName;
    private TextView mTvRecipeIngredients;
    private TextView mTvNutritionInfo;
    private Button mBtnAdd;

    private Recipe mRecipe;

    private SpoonacularClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mClient = new SpoonacularClient();

        if (getIntent().getExtras() != null) {
            mRecipe = (Recipe) getIntent().getParcelableExtra("recipe");

            mIvRecipeImage = findViewById(R.id.ivRecipeImage);
            Glide.with(this).load(mRecipe.getImageLink()).into(mIvRecipeImage);

            mTvRecipeName = findViewById(R.id.tvRecipeName);
            mTvRecipeName.setText(mRecipe.getName());

            mTvNutritionInfo = findViewById(R.id.tvNutritionInfo);
            setNutrientText(mRecipe);

            mTvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);
            try {
                setRecipeText(mRecipe);
            } catch (JSONException e) {
                mTvRecipeIngredients.setText("error setting recipe text :(");
            }

            mBtnAdd = findViewById(R.id.btnAdd);
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(RecipeDetailActivity.this, MainActivity.class);
                    i.putExtra("dateString", AddRecipeFragment.mMealTime);
                    i.putExtra("breakfastArray", AddRecipeFragment.mBreakfast);
                    i.putExtra("lunchArray", AddRecipeFragment.mLunch);
                    i.putExtra("dinnerArray", AddRecipeFragment.mDinner);
                    i.putExtra(AddRecipeFragment.mMealName + "ClickedRecipe", mRecipe);
                    startActivity(i);
                }
            });


        }
    }

    private void setRecipeText(Recipe recipe) throws JSONException {
        String recipeText = "";

        List<Ingredient> ingredients = (ArrayList) recipe.get("ingredients");

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = (Ingredient) ingredients.get(i);
            String ingredientText = ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName() + "\n";
            Log.i("ingredients: ", ingredientText);
            recipeText += ingredientText;
        }

        mTvRecipeIngredients.setText(recipeText);

    }

    private void setNutrientText(Recipe recipe) {
        int recipeId = (int) recipe.getSpoonacularId();

        mClient.getNutrients(recipeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                String nutrientInfo = "";

                try {
                    String calories = jsonObject.getString("calories");
                    calories = calories.replace("k", "");
                    double numCals = Double.parseDouble(calories);
                    nutrientInfo += calories + " cal\n";
                    recipe.setCalories(numCals);

                    String carbs = jsonObject.getString("carbs");
                    nutrientInfo += carbs + " carbs\n";
                    double gCarbs = Double.parseDouble(carbs.replace("g", ""));
                    recipe.setCarbs(gCarbs);

                    String fat = jsonObject.getString("fat");
                    nutrientInfo += fat + " fat\n";
                    double gFat = Double.parseDouble(fat.replace("g", ""));
                    recipe.setFat(gFat);

                    String protein = jsonObject.getString("protein");
                    nutrientInfo += protein + " protein\n";
                    double gProtein = Double.parseDouble(protein.replace("g", ""));
                    recipe.setProtein(gProtein);

                    mTvNutritionInfo.setText(nutrientInfo);

                    recipe.saveInBackground();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(RecipeDetailActivity.this, "Error retrieving nutrient information", Toast.LENGTH_SHORT).show();
            }
        });
    }

}