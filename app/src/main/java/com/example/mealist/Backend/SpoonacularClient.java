package com.example.mealist.Backend;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.BuildConfig;

public class SpoonacularClient extends AsyncHttpClient {

    public static final String API_KEY = BuildConfig.SPOON_KEY_2;
    public static final String RECIPE_SEARCH_URL = String.format("https://api.spoonacular.com/recipes/complexSearch?apiKey=%s", API_KEY);
    public static final String INGREDIENTS_SEARCH_URL = String.format("https://api.spoonacular.com/recipes/{id}/ingredientWidget.json?apiKey=%s", API_KEY);
    public static final String NUTRIENT_SEARCH_URL = String.format("https://api.spoonacular.com/recipes/{id}/nutritionWidget.json?apiKey=%s", API_KEY);
    public static final String RECIPE_INFO_URL = String.format("https://api.spoonacular.com/recipes/{id}/information?includeNutrition=true&apiKey=%s", API_KEY);
    public static final String GENERATE_RECIPE_URL = String.format("https://api.spoonacular.com/mealplanner/generate?apiKey=%s", API_KEY);
    public static final String RANDOM_RECIPE_URL = String.format("https://api.spoonacular.com/recipes/random?apiKey=%s", API_KEY);

    public SpoonacularClient() {
        super();
    }

    public void getRecipes(String query, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("query", query);
        params.put("number", 3);
        get(RECIPE_SEARCH_URL, params, handler);
    }

    /**
     * Uses "Get Recipe Information" endpoint, has all of the functions of
     * getIngredients and getNutrients (as well as getting the link to the recipe instructions
     * and the grocery store "aisle" (ex. produce, vegetables) for each ingredient
     * Takes up less API call "points" overall than the 2 other functions
     * @param id
     * @param handler
     */
    public void getRecipeInformation(int id, JsonHttpResponseHandler handler) {
        String id_string = String.valueOf(id);
        String url = RECIPE_INFO_URL.replace("{id}", id_string);
        get(url, handler);
    }

    public void generateMealPlan(JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("timeFrame", "day");
        get(GENERATE_RECIPE_URL, params, handler);
    }

    public void generateRandomMeals(JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        // TODO: maybe use preferences to put params in "tags", add new argument for all the tags
        params.put("number", 4);
        get(RANDOM_RECIPE_URL, params, handler);
    }

    public void getIngredients(int id, JsonHttpResponseHandler handler) {
        String id_string = String.valueOf(id);
        String Url = INGREDIENTS_SEARCH_URL.replace("{id}", id_string);
        get(Url, handler);
    }

    public void getNutrients(int id, JsonHttpResponseHandler handler) {
        String id_string = String.valueOf(id);
        String Url = NUTRIENT_SEARCH_URL.replace("{id}", id_string);
        get(Url, handler);
    }
}
