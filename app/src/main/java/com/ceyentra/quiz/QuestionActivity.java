package com.ceyentra.quiz;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuestionActivity extends AppCompatActivity{

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
        setContentView(R.layout.activity_question);

        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);

        setTimer();
        mProgressBar.setVisibility(View.INVISIBLE);
        startTimer();
        mProgressBar1.setVisibility(View.VISIBLE);
    }

    private void setTimer(){
        totalTimeCountInMilliseconds =  11 * 1000;
        mProgressBar1.setMax( 10 * 1000);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                mProgressBar1.setProgress((int) (leftTimeInMilliseconds));
                textViewShowTime.setText(String.format("%02d", seconds % 60));
            }
            @Override
            public void onFinish() {
                textViewShowTime.setText("00");
                textViewShowTime.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar1.setVisibility(View.GONE);

            }
        }.start();
    }
}
