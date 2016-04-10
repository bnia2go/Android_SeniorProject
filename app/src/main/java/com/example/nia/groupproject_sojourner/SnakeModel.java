package com.example.nia.groupproject_sojourner;

import android.graphics.Point;
import android.util.Log;
import java.util.ArrayDeque;
/**
 * Created by Nia on 3/4/2016.
 */


public class SnakeModel {
    //for clock
    private boolean slowMotion = false;
    private double delay;
    private int cc;

    //pace of snake
    private final static int PACE_STYLE = 30;
    private final static int SLOW_MO_DELAY = GameThread.getFps() / 5;
    private ArrayDeque<FreeCell> fcs;
    private FreeCell lastTail;
    private int rad;
    private double lastPace, nextMPace, mDelay;
    private boolean paceIncreased;
    private WhichWay goThisWay;
    private int stillAlive;
    private int currentScore;
    private boolean notProtected = false;


    private boolean getPics;

    public SnakeModel(int rad, boolean getPics){
        this.rad = rad;
        fcs = new ArrayDeque<FreeCell>();
        fcs.addLast(new FreeCell(3,2,rad));
        fcs.addLast(new FreeCell(2,2,rad));
        fcs.addLast(new FreeCell(1,2,rad));

        double startPaceDelay = nextMPace = SLOW_MO_DELAY;
        lastPace = startPaceDelay / 3;
        mDelay = (startPaceDelay - lastPace) / PACE_STYLE;
        paceIncreased = false;

        goThisWay = WhichWay.DOWN;
        stillAlive = 10;
        currentScore = 0;

        this.getPics = getPics;
    }

    private void hitMyself()
    {
        for(FreeCell freeCell: fcs)
        {
            if (freeCell != getSnakeHead() && freeCell.getPointLoc().equals(getSnakeHead().getPointLoc()))
                destroyIt();
        }
    }

    public boolean sayAh(AddedObjects aob){
        return getSnakeHead().getPointLoc().equals(aob.getPointLoc());
    }

    public void increaseSize (){ fcs.addLast(new FreeCell(lastTail));}

    public void moveIt(){
        //location of snake head
        Point top = getSnakeHead().getPointLoc();

        //insert new freecell behind head
        switch(goThisWay){
            case UP:
                fcs.addFirst(new FreeCell(top.x, top.y -1, rad));
                break;
            case DOWN:
                fcs.addFirst(new FreeCell(top.x, top.y +1, rad));
                break;
            case RIGHT:
                fcs.addFirst(new FreeCell(top.x + 1, top.y, rad));
                break;
            case LEFT:
                fcs.addFirst(new FreeCell(top.x - 1, top.y, rad));
                break;
        }

        lastTail = fcs.removeLast();
        //after removing last freecell and saving it, check if you hit yourself
        hitMyself();

    }

    public void incrementPace(){
        if(!slowMotion){
            nextMPace -= mDelay;
            if(nextMPace < lastPace)
                nextMPace = lastPace;

            paceIncreased = false;
        }
    }

    public void startTimer(){
        if(!slowMotion)
            delay = nextMPace;
        nextMPace = SLOW_MO_DELAY;

        cc = PaceClock.getEffDur();
        slowMotion = true;
    }

    public void updateTimer(){
        if(slowMotion){
            cc--;
            if(cc == 0){
                slowMotion = false;
                nextMPace = delay;  //resume normal speed
            }
        }
    }

    public void increaseScore( int sc){
        this.currentScore += sc;
    }
    public void setNotProtected( boolean check){ this.notProtected = check;}
    public boolean getNotProtected(){ return notProtected; }
    public FreeCell getSnakeHead(){ return fcs.peekFirst();}
    public ArrayDeque<FreeCell> getFreeCells(){ return fcs;}
    public WhichWay moveThisWay() {
        return goThisWay;
    }
    public void setWhichWay(WhichWay goHere){ this.goThisWay = goHere;}

    public int getPaceDelay(){return (int)Math.round(nextMPace);}
    public boolean increasePace(){ return paceIncreased;}
    public boolean isHorizontal(){ return goThisWay.isHoriz();}
    public boolean isVertical(){ return goThisWay.isVert();}
    public void enablePaceIncrementFg(){ paceIncreased = true;}

    public int getRemainingTime(){ return cc;}
    public boolean checkDead(){return stillAlive == 0; }
    public boolean checkUsingPics(){ return getPics;}
    public void destroyIt(){ stillAlive = 0;}
    public void resurrect(){stillAlive = 10;}
    public int getCurrScore(){ return currentScore;}

}
