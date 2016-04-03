package com.example.nia.groupproject_sojourner;

/**
 * Created by Nia on 3/4/2016.
 */

import android.graphics.Point;

public class FreeCell extends AddedObjects {
    public FreeCell(int a, int b, int rad){
        super(rad);
        pointLocation = new Point(a, b);
    }

    public FreeCell(FreeCell freeCell){
        super(freeCell.getRadius());
        pointLocation = new Point(freeCell.pointLocation);
    }
}
