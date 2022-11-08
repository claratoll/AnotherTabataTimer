package com.example.anothertabatatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabataTimer extends AppCompatActivity {
    TextView timer;
    TextView rounds_left;
    TextView go_pause;
    int rest_number;
    int work_number;
    int workoutCyclesRemaining;
    int cyclesRemaining;
    private final long CD_INTERVAL = 1000;

    private final int WORK = 0;
    private final int REST = 1;

    boolean timer_free = true;

    private RelativeLayout relativeLayout;

    CountDownTimer pauseCountDownTimer;
    CountDownTimer goCountDownTimer;


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabata_timer);

        Intent intent = getIntent();
        work_number = intent.getIntExtra("work", 0);
        rest_number = intent.getIntExtra("rest", 0);
        workoutCyclesRemaining = intent.getIntExtra("rounds", 0);

        relativeLayout = findViewById(R.id.activity_timer);


        timer = findViewById(R.id.textView);
        rounds_left = findViewById(R.id.rounds);
        go_pause = findViewById(R.id.go_time);

        workout(work_number, WORK);
    }

    public void workout (int seconds_number, int mode) {
        MediaPlayer music = MediaPlayer.create(TabataTimer.this, R.raw.countdown);

        cyclesRemaining = workoutCyclesRemaining;
        cyclesRemaining--;

        if (mode == WORK && timer_free) {
            timer_free = false;
            goCountDownTimer = new CountDownTimer(seconds_number * 1000, CD_INTERVAL) {
                @Override
                public void onTick(long l) {
                    timer.setText("" + String.valueOf(Math.round(l * 0.001f)));
                    relativeLayout.setBackgroundColor(getResources().getColor(R.color.go_background));
                    rounds_left.setText("" + cyclesRemaining);
                    go_pause.setText("GO");

                    if (Math.round(l * 0.001f) == 3) {
                        music.start();
                    }
                }

                @Override
                public void onFinish() {
                    workout(rest_number, REST);
                    relativeLayout.setBackgroundColor(getResources().getColor(R.color.pause_background));
                    rounds_left.setText("" + cyclesRemaining--);
                    go_pause.setText("PAUSE");

                }
            }.start();

        } else if (mode == REST) {
            pauseCountDownTimer = new CountDownTimer(seconds_number * 1000, CD_INTERVAL) {
                @Override
                public void onTick(long l) {
                    timer.setText("" + String.valueOf(Math.round(l * 0.001f)));

                    if (Math.round(l * 0.001f) == 3){
                        music.start();
                    }
                }

                @Override
                public void onFinish() {
                    timer_free = true;
                    workoutCyclesRemaining--;
                    if (workoutCyclesRemaining > 0) {
                        workout(work_number, WORK);
                    } else {
                        workoutFinished();
                    }
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(goCountDownTimer != null) goCountDownTimer.cancel();
        if(pauseCountDownTimer != null) pauseCountDownTimer.cancel();
    }

    private void workoutFinished() {
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.background));
        timer.setText("Finished");
    }
}