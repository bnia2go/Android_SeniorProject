package com.example.nia.groupproject_sojourner;

import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private GamePlay gamePlay;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gamePlay = new GamePlay(this);
        setContentView(R.layout.activity_main);
    }

    public void onRestart()
    {
        if(gamePlay != null)
            gamePlay.initGame();

        super.onRestart();
    }

    protected void onPause()
    {
        GameThread.setRunning(false);
        super.onPause();
    }

    public void playGame(View v){
        //Intent myIntent = new Intent(this, GamePlay.class );
        //this.startActivity( myIntent );
        setContentView(gamePlay);
    }

}
