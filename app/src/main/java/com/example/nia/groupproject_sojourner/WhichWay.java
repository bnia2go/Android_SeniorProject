package com.example.nia.groupproject_sojourner;

/**
 * Created by Nia on 3/5/2016.
 */
public enum WhichWay {
    RIGHT(0),
    DOWN(1),
    LEFT(2),
    UP(3);

    private final int dir;

    private WhichWay(int dir) {
        this.dir = dir;
    }

    public boolean isHorizontal() {
        return dir == 0 || dir == 2;
    }

    public boolean isVertical() {
        return !isHorizontal();
    }

}