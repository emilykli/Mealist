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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
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
    private Button mBtnFavorite;

    private Recipe mRecipe;

    private ParseUser mUser;
    private List<Recipe> mUserFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

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

            mBtnFavorite = findViewById(R.id.btnFavorite);

            assignUserAndFavorites();

            mBtnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBtnFavorite.getText().toString().equals("Favorite")) {
                        mUser.add("favorites", mRecipe);
                        mUser.saveInBackground();
                        mBtnFavorite.setText("Unfavorite");
                    }
                    else {
                        Recipe removable = null;
                        for (Recipe favorite : mUserFavorites) {
                            if (favorite.getName().equals(mRecipe.getName())) {
                                removable = favorite;
                                break;
                            }
                        }
                        mUserFavorites.remove(removable);
                        mUser.put("favorites", mUserFavorites);
                        mUser.saveInBackground();
                        mBtnFavorite.setText("Favorite");
                    }
                }
            });


        }
    }

    private void assignUserAndFavorites() {
        mUser = ParseUser.getCurrentUser();
        mUserFavorites = (ArrayList) mUser.get("favorites");
        for (Recipe favorite : mUserFavorites) {
            favorite.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (favorite.getName().equals(mRecipe.getName())) {
                        mBtnFavorite.setText("Unfavorite");
                    }
                }
            });
        }
    }

    private void setRecipeText(Recipe recipe) throws JSONException {
        String recipeText = "";

        List<Ingredient> ingredients = (ArrayList) recipe.get(Recipe.KEY_INGREDIENTS);

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = (Ingredient) ingredients.get(i);
            String ingredientText = ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName() + "\n";
            Log.i("ingredients: ", ingredientText);
            recipeText += ingredientText;
        }

        mTvRecipeIngredients.setText(recipeText);

    }

    private void setNutrientText(Recipe recipe) {
        String nutrientText = "";
        nutrientText += recipe.getCalories() + " kcal\n";
        nutrientText += recipe.getCarbs() + " g Carbs\n";
        nutrientText += recipe.getFat() + " g Fat\n";
        nutrientText += recipe.getProtein() + " g Protein";
        mTvNutritionInfo.setText(nutrientText);

    }

}