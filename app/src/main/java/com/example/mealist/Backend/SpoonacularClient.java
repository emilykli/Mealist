package com.example.mealist.Backend;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.BuildConfig;

public class SpoonacularClient extends AsyncHttpClient {

    public static final String[] API_KEYS = {BuildConfig.SPOON_KEY_1, BuildConfig.SPOON_KEY_2, BuildConfig.SPOON_KEY_3};
    public static int API_KEY_INDEX = 2;
    public static final String RECIPE_SEARCH_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=%s";
    public static final String RECIPE_INFO_URL = "https://api.spoonacular.com/recipes/{id}/information?apiKey=%s";
    public static final String GENERATE_RECIPE_URL = "https://api.spoonacular.com/mealplanner/generate?apiKey=%s";
    public static final String RANDOM_RECIPE_URL = "https://api.spoonacular.com/recipes/random?apiKey=%s";

    public static final int SEARCH_LIMIT = 3;

    public SpoonacularClient() {
        super();
    }

    public void getRecipes(String query, JsonHttpResponseHandler handler) {
        String url = String.format(RECIPE_SEARCH_URL, API_KEYS[API_KEY_INDEX]);
        RequestParams params = new RequestParams();
        params.put("query", query);
        params.put("number", SEARCH_LIMIT);
        get(url, params, handler);
    }

    /**
     * Uses "Get Recipe Information" endpoint,
     * gets ingredients, nutrients, link to recipe instructions, and grocery store aisle
     * @param id
     * @param handler
     */
    public void getRecipeInformation(int id, JsonHttpResponseHandler handler) {
        String id_string = String.valueOf(id);
        String url = (String.format(RECIPE_INFO_URL, API_KEYS[API_KEY_INDEX])).replace("{id}", id_string);
        RequestParams params = new RequestParams();
        params.put("includeNutrition", "true");
        get(url, params, handler);
    }

    public void generateMealPlan(JsonHttpResponseHandler handler) {
        String url = String.format(GENERATE_RECIPE_URL, API_KEYS[API_KEY_INDEX]);
        RequestParams params = new RequestParams();
        params.put("timeFrame", "day");
        get(url, params, handler);
    }

    public void generateRandomMeals(JsonHttpResponseHandler handler) {
        String url = String.format(RANDOM_RECIPE_URL, API_KEYS[API_KEY_INDEX]);
        RequestParams params = new RequestParams();
        params.put("number", SEARCH_LIMIT);
        get(url, params, handler);
    }

    public static void changeApiKey() {
        API_KEY_INDEX = (API_KEY_INDEX + 1) % 3;
    }

}
