package com.example.mealist.AddRecipe;

import java.util.Comparator;

/**
 * Object[] = [(Recipe) recipe, (double) recommendation points]
 *
 * Used for recipe recommendation priority queue
 *
 */
public class RecommendationComparator implements Comparator<Object[]> {

    @Override
    public int compare(Object[] o1, Object[] o2) {
        double points1 = (double) o1[1];
        double points2 = (double) o2[1];

        if (points1 < points2) {
            return 1;
        }
        else if (points1 > points2) {
            return -1;
        }
        return 0;
    }
}
