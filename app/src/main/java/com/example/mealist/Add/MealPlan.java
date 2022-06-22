package com.example.mealist.Add;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("MealPlan")
public class MealPlan extends ParseObject {
    public static final String KEY_OWNER = "owner";
    public static final String KEY_BREAKFAST = "breakfast";
    public static final String KEY_LUNCH = "lunch";
    public static final String KEY_DINNER = "dinner";
    public static final String KEY_DAY_OF = "dayOf";

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public JSONArray getBreakfast() {
        return getJSONArray(KEY_BREAKFAST);
    }

    public JSONArray getLunch() {
        return getJSONArray(KEY_LUNCH);
    }

    public JSONArray getDinner() {
        return getJSONArray(KEY_DINNER);
    }

    public Date getDayOf() {
        return getDate(KEY_DAY_OF);
    }

    public void setDayOf(Date date) {
        put(KEY_DAY_OF, date);
    }
}
