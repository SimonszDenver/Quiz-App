package com.ceyentra.quiz.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ceyentra.quiz.R;
import com.ceyentra.quiz.data.UserData;
import com.ceyentra.quiz.socket.SocketConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;

public class JoinQuizActivity extends AppCompatActivity {

    private Socket socket;
    public static int state;
    public static int time;

    /*
     * On Activity Create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_quiz);

        /*
         * Get Socket Connection
         */
        this.socket = SocketConnection.getInstance().getSocket();
        this.socket.on("State",onChangeState);
        this.socket.on("GetStart",onStartActivity);
        this.socket.on("SendReset",onResetActivity);

        socket.connect();
    }

    /*
     * On Activity Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.socket.off("State");
    }


    private Emitter.Listener onStartActivity = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JoinQuizActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        JoinQuizActivity.time = jsonObject.getInt("time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setStateOnStart();
                }
            });
        }
    };

    private void setStateOnStart(){
        JoinQuizActivity.state = 1;
    }

    /*
     * Get Start button click
     */
    public void onGettingStarted(View view) {
        EditText textField = findViewById(R.id.username);
        String username = textField.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            return;
        }
        /*
         *Set username to the UserData Object
         */
        UserData.getInstance().setUsername(username);

        /*
         * Create Json Object using username
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
         * Emit json
         */
        socket.emit("Join", jsonObject);

        System.out.println(this.socket.connected());

        if (this.socket.connected()) {

            if (state == 0) {
                Intent intent = new Intent(this, WaitingActivity.class);
                startActivity(intent);
                finish();
            } else if (state == 1) {
                Intent intent = new Intent(this, WaitingOnQuizActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    private Emitter.Listener onChangeState = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JoinQuizActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        int state = data.getInt("state");
                        setState(state);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void setState(int state){
        this.state = state;
    }

    /*
     * Reset Activity
     */
    private Emitter.Listener onResetActivity = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JoinQuizActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    resetActivity();
                }
            });
        }
    };

    private void resetActivity(){
        this.socket.off("GetStart");
        JoinQuizActivity.state=0;
        Intent intent = new Intent(this,JoinQuizActivity.class);
        startActivity(intent);
    }
}
