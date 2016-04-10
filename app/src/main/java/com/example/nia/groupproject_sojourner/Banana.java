package com.example.nia.groupproject_sojourner;

import android.graphics.Point;

/**
 * Created by Nia on 3/8/2016.
 * This class is BANANAS!
 */
public class Banana extends Food {
    private static final int score = 30;

    public Banana(Point p, SnakeModel snake, int rad)
    {
        super(p, snake, rad, score);
        foodGroup = 2;
    }
}
