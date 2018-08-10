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

public class WaitingActivity extends AppCompatActivity {

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        this.socket = SocketConnection.getInstance().getSocket();
        this.socket.on("GetStart",onStartActivity);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.socket.off("GetStart",onStartActivity);
    }


    /*
     * Listener
     */
    private Emitter.Listener onStartActivity = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WaitingActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        int state = data.getInt("state");
                        if (state == 1){
                            changeActivity();
                        }else{
                            resetActivity();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void changeActivity(){
        Intent intent = new Intent(this,StartingQuizActivity.class);
        startActivity(intent);
        finish();
    }

    private void resetActivity(){
        Intent intent = new Intent(this,JoinQuizActivity.class);
        startActivity(intent);
        finish();
    }
}
