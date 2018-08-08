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
    private static int state;

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
        socket.connect();
    }

    /*
     * On Activity Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.socket.disconnect();
        this.socket.off("State");
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
                    /* TODO */
                }
            });
        }
    };

    private void setState(int state){
        this.state = state;
    }
}
