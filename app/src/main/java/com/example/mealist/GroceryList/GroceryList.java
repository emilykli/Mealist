package com.example.mealist.GroceryList;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("GroceryList")
public class GroceryList extends ParseObject {
    public static final String KEY_OWNER = "owner";
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_START_DATE = "startRange";
    public static final String KEY_END_DATE = "endRange";

    public Date getStartDate() {
        return getDate(KEY_START_DATE);
    }

    public void setStartDate(Date start) {
        put(KEY_START_DATE, start);
    }

    public Date getEndDate() {
        return getDate(KEY_END_DATE);
    }

    public void setEndDate(Date end) {
        put(KEY_END_DATE, end);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public ArrayList getIngredients() {
        return (ArrayList) get(KEY_INGREDIENTS);
    }

    public void setIngredients(JSONArray ingredients) {
        put(KEY_INGREDIENTS, ingredients);
    }

}
