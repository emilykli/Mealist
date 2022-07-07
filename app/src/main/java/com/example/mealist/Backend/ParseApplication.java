package com.example.mealist.Backend;

import android.app.Application;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.GroceryList.GroceryList;
import com.example.mealist.MakeMealPlan.MealPlan;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.BuildConfig;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(GroceryList.class);
        ParseObject.registerSubclass(Ingredient.class);
        ParseObject.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(MealPlan.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.PARSE_APP_ID)
                .clientKey(BuildConfig.PARSE_CLIENT_KEY)
                .server(BuildConfig.SERVER_URL)
                .build()
        );
    }
}
