package com.example.nia.groupproject_sojourner;
import java.util.ArrayDeque;
import java.util.Random;
import android.content.Context;
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

/**
 * Created by Nia on 12/5/2015.
 */
public class GamePlay extends SurfaceView implements SurfaceHolder.Callback, TouchInterface {

    private GameThread snakeThread;
    private Context snakeContext;
    private Paint paintSnake;
    private int tc;
    private ArrayDeque<WhichWay> whichWay;

    private Point pointHere;
    private int freeCellD, freeCellR;
    private SnakeModel sm;
    private Food food;
    private UniqueClass uniqueClass;

    private String highScoreKey = "highScore";
    private String [] gameOver = new String []{"CRASHED!", "TAP to PLAY AGAIN."};
    private long hs;
    private boolean updateHighScore;

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

    private void displayAlert(Canvas canvas) {
        int size = 3 * freeCellD / 2;
        int padLeft = freeCellD + size / 4;
        int padTop = (getHeight() / 2) - 2 * size;

        paintSnake.setTextSize(size);
        paintSnake.setColor(Color.rgb(255, 255, 102));
        canvas.drawText(gameOver[0], padLeft, padTop + size, paintSnake);
        canvas.drawText(gameOver[1], padLeft, padTop + 2 * size, paintSnake);
    }

    private void displayPoints(Canvas canvas) {

        int s = 3 * freeCellD / 2;
        int lp = freeCellD + s / 4;
        int tp =  freeCellD * 20; //getWidth() + s * 5;

        //needs to be put in a for loop so that the top score, currentscore, timer can be updated
        // and not overwritten on the screen
        String [] showScore;
        String top = String.valueOf(hs);
        String current = String.valueOf(sm.getCurrScore());
        String time = String.valueOf(sm.getRemainingTime());

        if(sm.getRemainingTime() == 0){
            showScore = new String [] {"TOP: " + top, "Current: " + current};
        }
        else
            showScore = new String []{"TOP: " + top, "Current: " + current,
                    "Timer: " + time};

        for(int t = 0; t < showScore.length; t++) {
            paintSnake.setTextSize(s);
            paintSnake.setColor(Color.GREEN);
            canvas.drawText(showScore[t], lp, tp + (t + 1) * s, paintSnake);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //initialize game when main -> gameplay
        initGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        //stop and clean up thread
        boolean startAgain = true;
        snakeThread.setRunning(false);
        while (startAgain) {
            try {
                snakeThread.join();
                startAgain = false;
            } catch (InterruptedException e) {

            }
        }
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
        int boardHeight = getHeight() / freeCellD;
        pointHere = new Point(boardWidth, boardHeight);

        sm = new SnakeModel(freeCellR, (gamePlayBoard != null && scale != null && cherry != null));
        newFood();
        // start game loop gameThread
        snakeThread = new GameThread(getHolder(), this);
        snakeThread.setRunning(true);
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
                ateItsFood();
                sm.moveIt();
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

            if (ww.isHorizontal()) {
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
        if (sm.moveThisWay().isHorizontal())
            sm.setWhichWay(WhichWay.UP);
    }

    @Override
    public void t2bottom(View v) {
        if (sm.moveThisWay().isHorizontal())
            sm.setWhichWay(WhichWay.DOWN);
    }

    @Override
    public void l2right(View v) {
        if (sm.moveThisWay().isVertical())
            sm.setWhichWay(WhichWay.RIGHT);
    }

    @Override
    public void r2left(View v) {
        if (sm.moveThisWay().isVertical())
            sm.setWhichWay(WhichWay.LEFT);
    }

    private void updateUniqueClass() {
        if (uniqueClass == null) {
            Random random = new Random();
            int num = random.nextInt(90) + 1;

            if (num <= 2)
                uniqueClass = new PaceClock(pointHere, sm, freeCellR);
            else if(2 < num && num <= 3 )
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

    /*
        Check if nix hit border
     */

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

    /*
        Set the bitmap pics for objects used in game
     */

    private Bitmap gamePlayBoard;
    private Bitmap cherry, banana, lime;
    private Bitmap timer, scale;

    private void displayPics() {
        gamePlayBoard = BitmapFactory.decodeResource(getResources(), R.drawable.bordergrass);
        scale = BitmapFactory.decodeResource(getResources(), R.drawable.scale);
        timer = BitmapFactory.decodeResource(getResources(), R.drawable.clock);
        cherry = BitmapFactory.decodeResource(getResources(), R.drawable.cherry);
        banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        lime = BitmapFactory.decodeResource(getResources(), R.drawable.lime);

    }

    /*
        Check if nix ate his food.
        Updates score.
        Draws new food.
     */
    public void ateItsFood()
    {
        if (sm.sayAh(food))
        {
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

    private void newFood() {
        Random pickFood = new Random();
        int pick = pickFood.nextInt(3);

        if (pick <= 1)
            food = new Cherry(pointHere, sm, freeCellR);
        else if (pick == 2)
            food = new Banana(pointHere, sm, freeCellR);
        else
            food = new Lime(pointHere, sm, freeCellR);
    }

    //save score user data
    private void keepScore() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(snakeContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(highScoreKey, hs);
        editor.commit();
    }

    //where to draw each AddedObject Type
    private void drawFreeCell(Canvas canvas, Point p, Bitmap bitmap) {
        int x = p.x * freeCellD;
        int y = p.y * freeCellD;
        Rect distance = new Rect(x, y, x + freeCellD, y + freeCellD);

        Rect main = new Rect(10, 10, bitmap.getWidth(), bitmap.getHeight());

        canvas.drawBitmap(bitmap, main, distance, paintSnake);
    }

    /*
        Draws lining around gameboard
     */

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

    private void checkFoodGroup(Canvas canvas) {
        Bitmap treat;
        //Possible foods
        // Banana
        // Cherry
        // Lime

        if(food.getFoodGroup() == 1)
        {
            treat = cherry;
            Log.i("","FOOD IS CHERRY!");
        }
        else if(food.getFoodGroup() == 2)
        {
            treat = banana;
            Log.i("","FOOD IS BANANA!");
        }
        else if(food.getFoodGroup() == 3)
        {
            treat = lime;
            Log.i("","FOOD IS LIME!");
        }
        else
        {
            treat = gamePlayBoard;
            Log.i("","FOOD IS not!");
        }

        drawFreeCell(canvas, food.getPointLoc(), treat);
    }

    /*
        Retrieves and draws timer elements
     */
    private void getUniqueClass(Canvas canvas) {
        if (uniqueClass != null) {
            if (uniqueClass.getType() == AddedObjects.ObjectType.TIMER)
                // draw timer
                drawFreeCell(canvas, uniqueClass.getPointLoc(), timer);
            else
                drawFreeCell(canvas, uniqueClass.getPointLoc(), timer);
        }
    }

    /*
        This method updates the state of nix and draws it.
     */
    private void drawNix(Canvas canvas) {
        if (sm.checkUsingPics()) {
            for (FreeCell freeCell : sm.getFreeCells())
                if(sm.getNotProtected())
                    drawFreeCell(canvas, freeCell.getPointLoc(), scale);
                else
                    drawFreeCell(canvas, freeCell.getPointLoc(), scale);
        }
        else {
            paintSnake.setColor(Color.BLACK);

            for (FreeCell freeCell : sm.getFreeCells()) {
                Point p = freeCell.getPointLoc();

                int x = freeCellR - p.x * freeCellD;
                int y = freeCellR - p.y * freeCellD;
                canvas.drawCircle(x, y, freeCellR, paintSnake);
            }
        }
    }

    private void placeBoardDesign(Canvas canvas) {
        paintSnake.setColor(Color.BLACK);
        canvas.drawRect(0, 0, pointHere.x * freeCellD, pointHere.y * freeCellD, paintSnake);
    }

    /**
     * Draw Elements of Game
     */
    public void instantDisplay(Canvas canvas) {

        //background
        placeBoardDesign(canvas);

        //gamePlayBoard around gameplay view
        drawLining(canvas);

        checkFoodGroup(canvas);

        getUniqueClass(canvas);

        drawNix(canvas);

        displayPoints(canvas);

        //display crash message if Nix died
        if (sm.checkDead())
        {
            displayAlert(canvas);
        }
    }

}
