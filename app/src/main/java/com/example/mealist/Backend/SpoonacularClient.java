package com.example.mealist.Backend;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.BuildConfig;

public class SpoonacularClient extends AsyncHttpClient {

    public static final String API_KEY = BuildConfig.SPOON_KEY_1;
    public static final String RECIPE_SEARCH_URL = String.format("https://api.spoonacular.com/recipes/complexSearch?apiKey=%s", API_KEY);
    public static final String INGREDIENTS_SEARCH_URL = String.format("https://api.spoonacular.com/recipes/{id}/ingredientWidget.json?apiKey=%s", API_KEY);

    public SpoonacularClient() {
        super();
    }

    public void getRecipes(JsonHttpResponseHandler handler, String query) {
        RequestParams params = new RequestParams();
        params.put("query", query);
        params.put("number", 2);
        params.put("addRecipeInformation", true);
        get(RECIPE_SEARCH_URL, params, handler);
    }

    public void getIngredients(JsonHttpResponseHandler handler, int id) {
        String id_string = String.valueOf(id);
        String Url = INGREDIENTS_SEARCH_URL.replace("{id}", id_string);
        get(Url, handler);
    }
}
