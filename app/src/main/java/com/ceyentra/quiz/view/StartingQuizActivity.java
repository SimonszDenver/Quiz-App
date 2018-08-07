package com.ceyentra.quiz.view;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ceyentra.quiz.R;
import com.ceyentra.quiz.socket.SocketConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class StartingQuizActivity extends AppCompatActivity {

    private Socket socket;
    private TextView textViewShowTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_quiz);

        this.socket = SocketConnection.getInstance().getSocket();
        if (!this.socket.connected()) {
            this.socket.connect();
        }
        this.socket.on("Countdown_before",onCountDown);
        this.socket.connect();
        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);
    }

    private Emitter.Listener onCountDown = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            StartingQuizActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        int seconds = Integer.parseInt(data.getString("remaing_sconds"));

                        if (seconds <= 0){
                            changeActivity();
                        }
                        textViewShowTime.setText(String.format("%01d", seconds));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


/*    private void setTimer(){
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
    }*/

    private void changeActivity(){
        this.socket.off("Countdown_before");
        Intent intent = new Intent(this,QuestionActivity.class);
        startActivity(intent);
    }
}
