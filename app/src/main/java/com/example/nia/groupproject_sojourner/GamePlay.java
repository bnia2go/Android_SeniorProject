package com.example.nia.groupproject_sojourner;
import java.util.ArrayDeque;
import java.util.Random;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.View;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.Log;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.graphics.Canvas;
import android.content.Context;
import android.content.ContextWrapper;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Created by Nia on 12/5/2015.
 */
public class GamePlay extends SurfaceView implements SurfaceHolder.Callback, TouchInterface {
    private static final int CHERRY_PERCENT = 20;
    private static final int BANANA_PERCENT = 5;

    private static final int TIME_PERCENT = 2;
    private static final int OTHER_PERCENT = 1;


    private GameThread snakeThread;
    private Context snakeContext;
    private Paint paintSnake;
    private int tc;
    private ArrayDeque<WhichWay> whichWay;

    private Point pointHere;
    private int freeCellD, freeCellR;
    private SnakeModel sm;
    private Food food;
    private UniqueClass uniqueClass;  //SpecialElement

    private String highScoreKey = "highScore";
    private long hs;
    private boolean updateHighScore;

    private Bitmap gamePlayBoard, snakeProtected;
    private Bitmap background;
    private Bitmap food1, food2, food3;
    private Bitmap timer, scale;

    public GamePlay(Context snakeContext) {
        super(snakeContext);

        //background = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
        //setBackgroundDrawable(getResources().getDrawable(R.drawable.grass));
        this.snakeContext = snakeContext;
        getHolder().addCallback(this);
        setOnTouchListener(new TouchSwipeDetection(this));
        paintSnake = new Paint();
        whichWay = new ArrayDeque<WhichWay>();
        displayPics();
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        //stop and clean up thread
        boolean startAgain = false;
        snakeThread.setRunning(false);
        while (startAgain) {
            try {
                snakeThread.join();
                startAgain = false;
            } catch (InterruptedException e) {

            }
        }
    }

    private void displayPics() {
        gamePlayBoard = BitmapFactory.decodeResource(getResources(), R.drawable.border);
        scale = BitmapFactory.decodeResource(getResources(), R.drawable.scale);
        timer = BitmapFactory.decodeResource(getResources(), R.drawable.clock);
        food1 = BitmapFactory.decodeResource(getResources(), R.drawable.cherry);
        food2 = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        food3 = BitmapFactory.decodeResource(getResources(), R.drawable.lime);

    }

    /**
     * Game initialize method.
     */
    public void initGame() {
        // reset queue
        whichWay.clear();
        tc = 0;

        updateHighScore = false;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(snakeContext);
        hs = sharedPref.getLong(highScoreKey, 0);

        // set game board and objects
        int boardWidth = 20;
        freeCellD = getWidth() / boardWidth;
        freeCellR = freeCellD / 2;
        int boardHeight = 1500 / freeCellD;
        pointHere = new Point(boardWidth, boardHeight);

        sm = new SnakeModel(freeCellR, (gamePlayBoard != null && scale != null && food1 != null));
        newFood();
        // start game loop gameThread
        snakeThread = new GameThread(getHolder(), this);
        snakeThread.start();
    }

    public void update() {
        //increase time counter
        tc++;

        //update timer counter
        if (tc % GameThread.getFps() == 0)
            sm.updateTimer();

        //update snake speed
        if (tc % sm.getPaceDelay() == 0) {
            if (sm.increasePace())
                sm.incrementPace();

            //determine direction
            boolean finish = false;
            while (!whichWay.isEmpty() && !finish) {
                WhichWay ww = whichWay.poll();

                switch (ww) {
                    case UP:
                    case DOWN:
                        if (sm.isHorizontal()) {
                            sm.setWhichWay(ww);
                            finish = true;
                        }
                        break;
                    case RIGHT:
                    case LEFT:
                        if (sm.isVertical()) {
                            sm.setWhichWay(ww);
                            finish = true;
                        }
                        break;
                }
            }

            didSnakeHitBorder();

            //if snake is still alive:
            // - move the snake
            // - check if it ate the food
            // - update uniqueclass
            // else :
            // - update score if it hasn't been updated
            if (!sm.checkDead()) {
                sm.moveIt(); //
                ateItsFood();
                updateUniqueClass();
            } else {
                if (!updateHighScore) {
                    keepScore();
                    updateHighScore = true;
                }
            }
        }
    }

    @Override
    public void onClick(View v, int vert, int hor) {
        if (sm.checkDead()) {
            initGame();
        } else {
            WhichWay ww = whichWay.isEmpty() ? sm.moveThisWay() : whichWay.getLast();

            if (ww.isHoriz()) {
                // if snake is moving along x axis
                // if touch above/below of the head
                if (hor < sm.getSnakeHead().getPointLoc().y * freeCellD) {
                    // move up
                    ww = WhichWay.UP;
                } else {
                    // move down
                    ww = WhichWay.DOWN;
                }
            } else {
                // if snake is moving along y axis
                // if touch left/right of the head
                if (vert < sm.getSnakeHead().getPointLoc().x * freeCellD) {
                    // move left
                    ww = WhichWay.LEFT;
                } else {
                    // move right
                    ww = WhichWay.RIGHT;
                }
            }

            // add direction to queue of directions to be applied to the snake
            whichWay.add(ww);
        }
    }

    @Override
    public void b2top(View v) {
        if (sm.moveThisWay().isHoriz())
            sm.setWhichWay(WhichWay.UP);
    }

    @Override
    public void t2bottom(View v) {
        if (sm.moveThisWay().isHoriz())
            sm.setWhichWay(WhichWay.DOWN);
    }

    @Override
    public void l2right(View v) {
        if (sm.moveThisWay().isVert())
            sm.setWhichWay(WhichWay.RIGHT);
    }

    @Override
    public void r2left(View v) {
        if (sm.moveThisWay().isVert())
            sm.setWhichWay(WhichWay.LEFT);
    }

    private void newFood() {
        Random randomLocation = new Random();
        int change = randomLocation.nextInt(90) + 1;

        if (change <= CHERRY_PERCENT)
            food = new Cherry(pointHere, sm, freeCellR);
        else if (CHERRY_PERCENT < change && change <= CHERRY_PERCENT + BANANA_PERCENT)
            food = new Banana(pointHere, sm, freeCellR);
        else
            food = new Lime(pointHere, sm, freeCellR);
    }

    private void updateUniqueClass() {
        if (uniqueClass == null) {
            Random random = new Random();
            int num = random.nextInt(90) + 1;

            if (num <= TIME_PERCENT)
                uniqueClass = new PaceClock(pointHere, sm, freeCellR);
        } else if (sm.sayAh(uniqueClass)) {
            if(uniqueClass.getType() == AddedObjects.ObjectType.TIMER)
            {
                sm.startTimer();
            } else
            {
                sm.setNotProtected(true);
            }
            uniqueClass = null;
        } else {
            uniqueClass.incrementTimer();
            if (uniqueClass.isItGone())
                uniqueClass = null;
        }
    }

    private void didSnakeHitBorder() {
        Point top = sm.getSnakeHead().getPointLoc();

        switch (sm.moveThisWay()) {
            case DOWN:
                if (top.y >= pointHere.y - 2)
                    sm.destroyIt();
                break;
            case UP:
                if (top.y <= 1)
                    sm.destroyIt();
                break;
            case RIGHT:
                if (top.x >= pointHere.x - 2)
                    sm.destroyIt();
                break;
            case LEFT:
                if (top.x <= 1)
                    sm.destroyIt();
                break;
        }

        if (sm.checkDead() && sm.getNotProtected()) {
            sm.setNotProtected(false);
            sm.resurrect();
        }
    }

    public void ateItsFood() {
        if (sm.sayAh(food)) {
            sm.increaseSize();
            sm.enablePaceIncrementFg();
            sm.increaseScore(food.getScore());

            // update high score
            if (sm.getCurrScore() > hs)
                hs = sm.getCurrScore();

            // create more food
            newFood();
        }
    }

    //save score user data
    private void keepScore() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(snakeContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(highScoreKey, hs);
        editor.commit();
    }

    /**
     * Game draw method.
     */
    public void render(Canvas canvas) {
        //gamePlayBoard around gameplay view
        drawLining(canvas);

        drawFood(canvas);

        drawUniqueClass(canvas);

        drawNix(canvas);

        drawPoints(canvas);

        if (sm.checkDead())
            displayAlert(canvas);
    }

    //where to draw each AddedObject Type
    private void drawFreeCell(Canvas canvas, Point p, Bitmap bitmap) {
        int x = p.x * freeCellD;
        int y = p.y * freeCellD;
        Rect distance = new Rect(x, y, x + freeCellD, y + freeCellD);

        Rect main = new Rect(10, 10, bitmap.getWidth(), bitmap.getHeight());

        canvas.drawBitmap(bitmap, main, distance, paintSnake);
    }

    private void drawLining(Canvas canvas) {
        paintSnake.setColor(Color.BLACK);

        // draw top and bottom gamePlayBoard
        for (int i = 0; i < pointHere.x; i++) {
            drawFreeCell(canvas, new Point(i, 0), gamePlayBoard);
            drawFreeCell(canvas, new Point(i, pointHere.y - 1), gamePlayBoard);
        }

        // fill rightMost and leftMost gamePlayBoard
        for (int i = 0; i < pointHere.y; i++) {
            drawFreeCell(canvas, new Point(0, i), gamePlayBoard);
            drawFreeCell(canvas, new Point(pointHere.x - 1, i), gamePlayBoard);
        }
    }

    private void drawFood(Canvas canvas) {
        Bitmap bitmap;
        //Possible foods
        // Banana
        // Cherry
        // Lime

        if(food instanceof Banana)
        {
            bitmap = food2;
        } else if(food instanceof Cherry)
        {
            bitmap = food1;
        } else if(food instanceof Lime)
        {
            bitmap = food3;
        } else
        {
            bitmap = gamePlayBoard;
        }

        drawFreeCell(canvas, food.getPointLoc(), bitmap);
    }

    private void drawUniqueClass(Canvas canvas) {
        if (uniqueClass != null) {
            if (uniqueClass.getType() == AddedObjects.ObjectType.TIMER)
                // draw timer
                drawFreeCell(canvas, uniqueClass.getPointLoc(), timer);
        }
    }

    private void drawNix(Canvas canvas) {
        if (sm.checkUsingPics()) {
            for (FreeCell freeCell : sm.getFreeCells())
                if (sm.getNotProtected())
                    drawFreeCell(canvas, freeCell.getPointLoc(), scale);
                else
                    drawFreeCell(canvas, freeCell.getPointLoc(), scale);
        } else {
            paintSnake.setColor(Color.BLACK);

            for (FreeCell freeCell : sm.getFreeCells()) {
                Point p = freeCell.getPointLoc();

                int x = freeCellR + p.x * freeCellD;
                int y = freeCellR + p.y * freeCellD;
                canvas.drawCircle(x, y, freeCellR, paintSnake);
            }
        }
    }

    private void drawPoints(Canvas canvas) {
        String[] text;
        text = new String[]{"Top: " + hs,
                    "Score: " + sm.getCurrScore(),
                    "Time: " + sm.getSlowedPaceRemain()};

        int textSize = 3 * freeCellD / 2;
        int leftPadding = freeCellD + textSize / 4;
        int topPadding = getWidth() + textSize * 5;

        paintSnake.setTextSize(textSize);
        paintSnake.setColor(Color.GREEN);

        canvas.drawText(text[0], leftPadding, topPadding + textSize, paintSnake);
        canvas.drawText(text[1], leftPadding, topPadding + 2 * textSize, paintSnake);
        canvas.drawText(text[2], leftPadding, topPadding + 3 * textSize, paintSnake);
    }

    private void displayAlert(Canvas canvas) {
        String[] text = new String[]{"CRASHED!", "TAP to PLAY AGAIN."};

        int textSize = 3 * freeCellD / 2;
        int leftPadding = freeCellD + textSize / 4;
        int topPadding = getHeight() / 2 - text.length * textSize;

        paintSnake.setTextSize(textSize);
        paintSnake.setColor(Color.YELLOW);
        canvas.drawText(text[0], leftPadding, topPadding + textSize, paintSnake);
        canvas.drawText(text[1], leftPadding, topPadding + 2 * textSize, paintSnake);
    }


    /*public void goBack(){

    }*/



}
