package com.example.nia.groupproject_sojourner;

import android.graphics.Point;
/**
 * Created by Nia on 3/5/2016.
 * This class creates the Clock special element
 * MAX_CLOCK - max duration for the clock to be displayed
 * EFF_DUR - when eaten how long snake becomes invinsible
 */

public class PaceClock extends UniqueClass {
    private static final int MAX_TIME = 25;
    private static final int EFF_DUR = 10;

    public PaceClock( Point p, SnakeModel snake, int rad){
        super(p, snake, rad, MAX_TIME);
        obj = ObjectType.TIMER;
    }

    public static int getEffDur(){ return EFF_DUR;}
}
