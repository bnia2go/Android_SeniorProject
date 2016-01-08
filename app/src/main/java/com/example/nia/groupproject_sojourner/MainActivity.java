package com.example.nia.groupproject_sojourner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goUserStat(View v){
        Intent myIntent = new Intent(this, UserData.class );
        this.startActivity(myIntent);
    }

    public void playGame(View v){
        Intent myIntent = new Intent(this, GamePlay.class );
        this.startActivity( myIntent );
    }

}
