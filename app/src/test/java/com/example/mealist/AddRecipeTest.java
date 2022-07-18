package com.example.mealist;

import com.example.mealist.AddRecipe.RecommendationComparator;

import static org.junit.Assert.*;
import org.junit.Test;

public class AddRecipeTest {

    @Test
    public void testRecommendationComparator() {
        int expected1 = 1;
        int expected2 = -1;
        int expected3 = 0;

        RecommendationComparator r = new RecommendationComparator();

        Object[] fakeRecipe1 = {"Recipe", 1.0};
        Object[] fakeRecipe2 = {"Recipe", 2.0};
        Object[] fakeRecipe3 = {"Recipe", 2.0};

        assertEquals(expected1, r.compare(fakeRecipe1, fakeRecipe2));
        assertEquals(expected2, r.compare(fakeRecipe2, fakeRecipe1));
        assertEquals(expected3, r.compare(fakeRecipe2, fakeRecipe3));
    }
}
