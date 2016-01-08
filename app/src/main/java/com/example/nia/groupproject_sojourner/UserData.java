package com.example.nia.groupproject_sojourner;

import android.content.Intent;
import android.os.Bundle;
import java.io.*;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

/**
 * Created by Nia on 12/5/2015.
 *
 * Used to display a User's Data
 * New High Score
 * Previous High Score
 *
 */
public class UserData extends AppCompatActivity {

    private int currentHighScore;
    private int previousHighScore;
    private static final String FILE_NAME = "score.txt";
    public TextView currentScore_label;
    public TextView previousScore_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        currentScore_label = (TextView) findViewById(R.id.currentScore);
        previousScore_label = (TextView) findViewById(R.id.previousScore);
        update();
    }




    public void goBack(View v){
        this.finish();
    }

    public void setScore(int score){
        previousHighScore = currentHighScore;
        currentHighScore = score;
    }

    public int getCurrentScore(){
        return currentHighScore;
    }

    public int getPreviousScore(){
        return previousHighScore;
    }


    public void update(){
       int cScore = this.getCurrentScore();
       int pScore = this.getPreviousScore();
       currentScore_label.setText("Current Score: " + cScore);
       previousScore_label.setText("Previous Score: " + pScore);

    }

    /* public UserData( ContextWrapper context ) {
        try {
            FileInputStream fis = context.openFileInput( FILE_NAME );
            ObjectInputStream ois = new ObjectInputStream( fis );

            }
        catch( Exception e ) {

            }
        }*/

    /*
    public void writeToFile( ContextWrapper context ) {
        try {
            FileOutputStream fos = context.openFileOutput( FILE_NAME, Context.MODE_PRIVATE );
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject( this );
            oos.close( );
            }
        catch( FileNotFoundException fnfe ) {
            Log.w( "MainActivity", fnfe.getMessage( ) );
            }
        catch( IOException ioe ) {
            Log.w( "MainActivity", ioe.getMessage( ) );
            }
        }*/

}
