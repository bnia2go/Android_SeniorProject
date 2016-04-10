package com.example.nia.groupproject_sojourner;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Nia on 3/3/2016.
 */
public class GameThread extends  Thread {
    private static final String TAG = GameThread.class.getSimpleName();

    private final SurfaceHolder surfaceHolder;

    private GamePlay gamePlay;

    private static boolean running;

    //frames per second
    private final static int FPS = 50;

    public static int getFps() {
        return FPS;
    }

    // max frames to be skipped
    private final static int MAX_FRAME_SKIPS = 4;

    // frame period
    private final static int FRAME_PERIOD = 900 / FPS;

    public GameThread(SurfaceHolder surfaceHolder, GamePlay gamePlay) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePlay = gamePlay;
        //running = true;
    }

    public static void setRunning(boolean running) {
        GameThread.running = running;
    }

    @Override
    public void run() {

        Canvas canvas;
        long startTime;
        long timeExec;
        int sleepTime;
        int skipFrame;

        while (running) {
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();

                synchronized (surfaceHolder) {
                    startTime = System.currentTimeMillis();
                    // reset frames skipped
                    skipFrame = 0;
                    // update state
                    this.gamePlay.update();
                    this.gamePlay.instantDisplay(canvas);

                    timeExec = System.currentTimeMillis() - startTime;

                    sleepTime = (int) (FRAME_PERIOD - timeExec);

                    if (0 < sleepTime) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    while (sleepTime < 0 && skipFrame < MAX_FRAME_SKIPS) {
                        this.gamePlay.update();

                        sleepTime += FRAME_PERIOD;
                        skipFrame++;
                    }

                    if (skipFrame > 0)
                        Log.v(TAG, "Skipped " + skipFrame + " frames");
                }
            } finally {
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
