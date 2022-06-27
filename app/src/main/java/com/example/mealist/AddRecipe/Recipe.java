package com.example.mealist.AddRecipe;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_IMAGE_LINK = "imageLink";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public JSONArray getIngredients() {
        return getJSONArray(KEY_INGREDIENTS);
    }

    public void setIngredients(JSONArray ingredients) {
        put(KEY_INGREDIENTS, ingredients);
    }

    public String getImageLink() { return getString(KEY_IMAGE_LINK); }

    public void setImageLink(String link) { put(KEY_IMAGE_LINK, link); }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonRecipe = new JSONObject();
        jsonRecipe.put(KEY_NAME, getName());
        jsonRecipe.put(KEY_INGREDIENTS, getIngredients());
        jsonRecipe.put(KEY_IMAGE_LINK, getImageLink());
        return jsonRecipe;
    }

    @NonNull
    @Override
    public String toString() {
//        return super.toString();
        return "~" + getString(KEY_NAME) + "~";
    }
}
