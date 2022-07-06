package com.example.mealist.AddRecipe;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject{
    public static final String KEY_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_AISLE = "aisle";
    public static final String KEY_IS_CHECKED = "isChecked";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public Number getQuantity() {
        return getNumber(KEY_QUANTITY);
    }

    public void setQuantity(Number quantity) {
        put(KEY_QUANTITY, quantity);
    }

    public String getUnit() {
        return getString(KEY_UNIT);
    }

    public void setUnit(String unit) {
        put(KEY_UNIT, unit);
    }

    public String getAisle() {
        return getString(KEY_AISLE);
    }

    public void setAisle(String aisle) {
        put(KEY_AISLE, aisle);
    }

    public boolean getChecked() { return getBoolean(KEY_IS_CHECKED); }

    public void setChecked(boolean isChecked) { put(KEY_IS_CHECKED, isChecked); }

}
