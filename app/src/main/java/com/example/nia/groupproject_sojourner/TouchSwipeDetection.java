package com.example.nia.groupproject_sojourner;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
/**
 * Created by Nia on 3/3/2016.
 */
public class TouchSwipeDetection implements View.OnTouchListener {
    static final String tag = "TouchSwipeDetection";
    private TouchInterface touchInterface;
    static final int MIN_DIST = 100;
    static final int MAX_CLICK = 50;
    private float downX;
    private float downY;

    public TouchSwipeDetection( TouchInterface touchInterface){
        this.touchInterface = touchInterface;
    }

    public void rightToLeft(View v){ touchInterface.r2left(v); }

    public void leftToRight(View v){
        touchInterface.l2right(v);
    }

    public void topToBottom(View v){
        touchInterface.t2bottom(v);
    }

    public void bottomToTop(View v){
        touchInterface.b2top(v);
    }

    private void onClick(View v, int w, int z){
        touchInterface.onClick(v, w, z);
    }

    public boolean onTouch(View v, MotionEvent me){
        switch(me.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = me.getX();
                downY = me.getY();
                return true;
            case MotionEvent.ACTION_UP:
            {
                float goingAcross = me.getX();
                float goingDown = me.getY();

                float deltaX = downX - goingAcross;
                float deltaY = downY - goingDown;

                if(deltaX <= MAX_CLICK && deltaY <= MAX_CLICK){
                    this.onClick(v, (int) goingAcross, (int) goingDown);
                    return true;
                }

                //Horizontal Swipe
                if (Math.abs(deltaX) > MIN_DIST)
                {
                    if(0 > deltaX)
                    {
                        this.leftToRight(v);
                        return true;
                    }
                    if(0 < deltaX)
                    {
                        this.rightToLeft(v);
                        return true;
                    }
                } else
                {
                    Log.i(tag, "Need long swipe.");
                }

                //Vertical Swipe
                if(Math.abs(deltaY) > MIN_DIST)
                {
                    if(0 > deltaY){
                        this.topToBottom(v);
                        return true;
                    }
                    if(0 < deltaY ){
                        this.bottomToTop(v);
                        return true;
                    }
                } else
                {
                    Log.i(tag, "Need long swipe.");
                }
            }
        }
        return false;
    }
}
