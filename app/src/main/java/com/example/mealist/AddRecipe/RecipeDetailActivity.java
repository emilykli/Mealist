package com.example.mealist.AddRecipe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mealist.MakeMealPlan.MealPlan;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String TAG = "RecipeDetailActivity";

    private ImageView mIvRecipeImage;
    private TextView mTvRecipeName;
    private TextView mTvRecipeIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Log.i(TAG, "oncreate");

        if (getIntent().getExtras() != null) {
            Recipe recipe = (Recipe) getIntent().getParcelableExtra("recipe");

            mIvRecipeImage = findViewById(R.id.ivRecipeImage);
            Glide.with(this).load(recipe.getImageLink()).into(mIvRecipeImage);

            mTvRecipeName = findViewById(R.id.tvRecipeName);
            mTvRecipeName.setText(recipe.getName());

            mTvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);

            try {
                Log.i(TAG, "hi");
                setRecipeText(recipe);
            } catch (JSONException e) {
                mTvRecipeIngredients.setText("error setting recipe text :(");
            }


        }
    }

    public void setRecipeText(Recipe recipe) throws JSONException {
        String recipeText = "";

        Log.i(TAG, "hello i am here now");
        List<Ingredient> ingredients = (ArrayList) recipe.get("ingredients");

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = (Ingredient) ingredients.get(i);
            String ingredientText = ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName() + "\n";
            Log.i("ingredients: ", ingredientText);
            recipeText += ingredientText;
        }

        mTvRecipeIngredients.setText(recipeText);

    }

}