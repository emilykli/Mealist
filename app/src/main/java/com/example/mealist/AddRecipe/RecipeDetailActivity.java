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

import com.bumptech.glide.Glide;
import com.example.mealist.Access.MainActivity;
import com.example.mealist.MakeMealPlan.MakePlanFragment;
import com.example.mealist.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String TAG = "RecipeDetailActivity";

    final FragmentManager mFragmentManager = getSupportFragmentManager();

    // TODO: make onclick for button and then
    //                 mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    // if that doesn't work pass in fragment manager from mainactivity and use that one instead

    private ImageView mIvRecipeImage;
    private TextView mTvRecipeName;
    private TextView mTvRecipeIngredients;
    private Button mBtnAdd;

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Log.i(TAG, "oncreate");

        if (getIntent().getExtras() != null) {
            mRecipe = (Recipe) getIntent().getParcelableExtra("recipe");

            mIvRecipeImage = findViewById(R.id.ivRecipeImage);
            Glide.with(this).load(mRecipe.getImageLink()).into(mIvRecipeImage);

            mTvRecipeName = findViewById(R.id.tvRecipeName);
            mTvRecipeName.setText(mRecipe.getName());

            mTvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);

            try {
                Log.i(TAG, "hi");
                setRecipeText(mRecipe);
            } catch (JSONException e) {
                mTvRecipeIngredients.setText("error setting recipe text :(");
            }

            mBtnAdd = findViewById(R.id.btnAdd);
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(RecipeDetailActivity.this, MainActivity.class);
                    i.putExtra(AddRecipeFragment.mMealName + "ClickedRecipe", mRecipe);
                    startActivity(i);
                }
            });


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