package com.example.nia.groupproject_sojourner;

/**
 * Created by Nia on 3/3/2016.
 */

import android.graphics.Point;

public class UniqueClass extends AddedObjects {

    private static int TIME_LASTING;

    private int cc;

    private boolean isGone;

    public UniqueClass(Point pd, SnakeModel sm, int radius, int timeDur){
        super(radius);
        getNewLocation(pd, sm);
        TIME_LASTING = timeDur;
        resetTimer();
    }

    public void resetTimer(){ cc = 0;}

    public boolean isItGone(){ return isGone;}

    public void incrementTimer(){
        cc++;

        if(cc >= TIME_LASTING){
            isGone = true;
        }
    }
}
