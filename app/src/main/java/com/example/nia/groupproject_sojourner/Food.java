package com.example.nia.groupproject_sojourner;

/**
 * Created by Nia on 3/8/2016.
 */

import android.graphics.Point;

//This class creates Food objects for added objects class
public class Food extends AddedObjects {
    //keep track of score for each type of food consumed
    //Cherry - 10
    //Banana - 30
    //Lime - 50
    private int score;
    private int foodGroup;

    public Food(Point p, SnakeModel snake, int rad, int score){
        super(rad);
        this.score = score;
        obj = ObjectType.FOOD;
    }

    public int getScore(){ return score;}

    public int getFoodGroup() {return foodGroup;}
}
