package com.ceyentra.quiz;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartingQuizActivity extends AppCompatActivity {

    int i = -1;
    ProgressBar mProgressBar, mProgressBar1;
    private Button buttonStartTime, buttonStopTime;
    private EditText edtTimerValue;
    private TextView textViewShowTime;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_quiz);

        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);

        setTimer();
        startTimer();
    }

    private void setTimer(){
        totalTimeCountInMilliseconds =  6 * 1000;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                textViewShowTime.setText(String.format("%01d", seconds % 60));
            }
            @Override
            public void onFinish() {
                changeActivity();
            }
        }.start();
    }

    private void changeActivity(){
        Intent intent = new Intent(this,QuestionActivity.class);
        startActivity(intent);
    }
}
