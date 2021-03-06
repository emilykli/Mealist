package com.example.mealist;


import com.example.mealist.GroceryList.GroceryList;
import com.example.mealist.GroceryList.ListFragment;

import static org.junit.Assert.*;
import org.junit.Test;

public class GroceryListTest {

    @Test
    public void testGetAisleGrouping() {
        String expected = GroceryList.KEY_DAIRY;
        String actual = ListFragment.getAisleGrouping("Milk, Eggs, Other Dairy");
        assertEquals(actual, expected);
    }
}
