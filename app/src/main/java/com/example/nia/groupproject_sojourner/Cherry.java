package com.example.nia.groupproject_sojourner;

import android.graphics.Point;

/**
 * Created by Nia on 3/8/2016.
 */
public class Cherry extends Food{
    private static final int score = 10;

    public Cherry(Point p, SnakeModel snake, int rad){
        super(p, snake, rad, score);
    }
}
