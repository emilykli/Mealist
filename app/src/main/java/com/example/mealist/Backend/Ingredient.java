package com.example.mealist.Backend;


import com.parse.ParseObject;

public class Ingredient {
    public static final String KEY_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_UNIT = "unit";

    String name;
    Double quantity;
    String unit;

    public Ingredient(String name, Double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public ParseObject getParseObject() {
        ParseObject ingredient = new ParseObject("Ingredient");
        ingredient.put(KEY_NAME, this.name);
        ingredient.put(KEY_QUANTITY, this.quantity);
        ingredient.put(KEY_UNIT, this.unit);
        return ingredient;
    }

}
