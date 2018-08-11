package com.ceyentra.quiz.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ceyentra.quiz.R;
import com.ceyentra.quiz.socket.SocketConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitingOnQuizActivity extends AppCompatActivity {

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_on_quiz);
        this.socket = SocketConnection.getInstance().getSocket();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private Emitter.Listener onResetActivity = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WaitingOnQuizActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        int state = data.getInt("state");
                        if (state == 0){
                            changeActivity();
                        }
                        JoinQuizActivity.state = 0;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void changeActivity(){
        Intent intent = new Intent(this,JoinQuizActivity.class);
        startActivity(intent);
        JoinQuizActivity.state = 0;
        finish();
    }
}
