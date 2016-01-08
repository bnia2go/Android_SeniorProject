package com.example.nia.groupproject_sojourner;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
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

/**
 * Created by Nia on 12/5/2015.
 */
public class GamePlay extends Activity implements View.OnTouchListener {
    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 500;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private SnakeModel snakeModel;

    public static final String MA = "MainActivity";

    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        snakeModel = new SnakeModel(this);
        LayoutInflater lg = getLayoutInflater();
        View v = lg.inflate(snakeModel, null);
        RelativeLayout rv = (RelativeLayout)findViewById(R.id.gamePlay);
        rv.addView(v);
    }


    public boolean onTouch( View v, MotionEvent event ) {
        int action = event.getAction( );
        switch( action ) {
            case MotionEvent.ACTION_UP:
                Log.w( MA, "UP: v = " + v + "; event = " + event );
                break;
            }
        return true;
    }


    public void goBack(View v){
        Intent myIntent = new Intent(this, MainActivity.class );
        this.startActivity(myIntent);
    }

    public void goUser(View v) {
        Intent myIntent = new Intent(this, UserData.class );
        this.startActivity( myIntent );
    }


}
