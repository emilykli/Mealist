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
    public static final String KEY_SPOONACULAR_ID = "spoonacularId";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_CARBS = "carbs";
    public static final String KEY_FAT = "fat";
    public static final String KEY_PROTEIN = "protein";

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

    public Number getSpoonacularId() {
        return getNumber(KEY_SPOONACULAR_ID);
    }

    public void setSpoonacularId(Number id) {
        put(KEY_SPOONACULAR_ID, id);
    }

    public String getCalories() {
        return getString(KEY_CALORIES);
    }

    public void setCalories(String calories) {
        put(KEY_CALORIES, calories);
    }

    public String getCarbs() {
        return getString(KEY_CARBS);
    }

    public void setCarbs(String carbs) {
        put(KEY_CARBS, carbs);
    }

    public String getFat() {
        return getString(KEY_FAT);
    }

    public void setFat(String fat) {
        put(KEY_FAT, fat);
    }

    public String getProtein() {
        return getString(KEY_PROTEIN);
    }

    public void setProtein(String protein) {
        put(KEY_PROTEIN, protein);
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonRecipe = new JSONObject();
        jsonRecipe.put(KEY_NAME, getName());
        jsonRecipe.put(KEY_INGREDIENTS, getIngredients());
        jsonRecipe.put(KEY_IMAGE_LINK, getImageLink());
        jsonRecipe.put(KEY_SPOONACULAR_ID, getSpoonacularId());
        jsonRecipe.put(KEY_CALORIES, getCalories());
        jsonRecipe.put(KEY_CARBS, getCarbs());
        jsonRecipe.put(KEY_FAT, getFat());
        jsonRecipe.put(KEY_PROTEIN, getProtein());
        return jsonRecipe;
    }

    @NonNull
    @Override
    public String toString() {
//        return super.toString();
        return "~" + getString(KEY_NAME) + "~";
    }
}
