package com.alexlowe.feckless;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "time_prefs";
    private int seconds;
    private boolean running;
    private Button btnRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRun = (Button)findViewById(R.id.run_button);

        retrieveSeconds();

        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            btnRun.setText(savedInstanceState.getString("text"));
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        runTimer();
    }

    private void retrieveSeconds() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if(sp.contains("secondsAtOnPause")) {
            long resumeTime = System.currentTimeMillis();
            long stopTime = sp.getLong("timeAtOnPause", 0L);
            int elapsed = (int) (((resumeTime - stopTime)/1000));

            seconds = sp.getInt("secondsAtOnPause", 0) + elapsed;
            running = true;
            btnRun.setText(R.string.pause);
        }else if (sp.contains("secondsNotRunning")){
            running = false;
            seconds = sp.getInt("secondsNotRunning", 0);
        }else{
            seconds = 0;
            running = false;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREFS_NAME);
        editor.apply();
    }

    public void onClickRun(View view){
        if(running){
            running = false;
            btnRun.setText(R.string.start);
        }else{
            running = true;
            btnRun.setText(R.string.pause);
        }
    }


    public void onClickReset(View view){
        running = false;
        btnRun.setText(R.string.start);
        seconds = 0;
    }

    private void runTimer(){
        final TextView timeview = (TextView)findViewById(R.id.time_view);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
                timeview.setText(time);

                if (running) {
                    seconds++;
                }

                if (seconds > 359998) {
                    running = false;
                }

                handler.postDelayed(this, 1000);
            }

        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putString("text", btnRun.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addHour(View view) {
        if(seconds < 356400){
            seconds += 3600;
        }
    }

    public void subHour(View view) {
        if(seconds > 3600){
            seconds -= 3600;
        }else{
            seconds = 0;
        }
    }

    public void addMin(View view) {
        if(seconds < 359940){
            seconds += 60;
        }
    }

    public void subMin(View view) {
        if(seconds > 60){
            seconds -= 60;
        }else{
            seconds = 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(running){
            SharedPreferences timePrefs = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = timePrefs.edit();
            editor.putInt("secondsAtOnPause", seconds);
            editor.putLong("timeAtOnPause", System.currentTimeMillis());
            editor.remove("secondsNotRunning");
            editor.apply();
        }else{
            SharedPreferences timePrefs = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = timePrefs.edit();
            editor.putInt("secondsNotRunning", seconds);
            editor.remove("secondsAtOnPause");
            editor.remove("timeAtOnPause");
            editor.apply();
        }
    }
}