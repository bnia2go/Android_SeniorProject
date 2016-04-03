package com.example.nia.groupproject_sojourner;

/**
 * Created by Nia on 3/3/2016.
 */
import android.graphics.Point;
import android.util.Log;
import java.util.Random;

//this class generates new locations for FOOD and TIMER
public class AddedObjects {
    protected int radius;
    protected Point pointLocation;
    protected ObjectType obj;

    public enum ObjectType {
        FOOD, TIMER, PROS
    }

    public AddedObjects(int radius){
        pointLocation = new Point();
        this.radius = radius;
    }

    public Point getPointLoc(){
        return pointLocation;
    }

    public int getRadius() { return radius;}

    public ObjectType getType() {return obj;}

    public void getNewLocation (Point pd, SnakeModel sm){
        Random random = new Random();
        Point p2 = new Point();

        boolean check;
        do {
            check = true;
            p2.x = random.nextInt(pd.x - 2) + 1;
            p2.y = random.nextInt(pd.y - 2) + 1;

            for( FreeCell freecell: sm.getFreeCells())
                if(freecell.getPointLoc().equals(p2))
                    check = false;
        } while (!check);

        pointLocation = p2;
    }

}
