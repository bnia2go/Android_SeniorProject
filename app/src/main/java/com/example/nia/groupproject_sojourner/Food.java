package com.example.nia.groupproject_sojourner;

/**
 * Created by Nia on 3/8/2016.
 */

import android.graphics.Point;

//This class creates Food objects for added objects class
public class Food extends AddedObjects {
    //keep track of score for each type of food consumed
    //Cherry - 10, foodgroup 1
    //Banana - 30, foodgroup 2
    //Lime - 50, foodgroup 3
    private int score;
    protected int foodGroup;

    public Food(Point p, SnakeModel sm, int rad, int score){
        super(rad);
        this.score = score;
        obj = ObjectType.FOOD;
        getNewLocation(p, sm);
    }

    public int getScore(){ return score;}

    /*
    returns the type of food so that new food can be generated
     */
    public int getFoodGroup() {return foodGroup;}
}
