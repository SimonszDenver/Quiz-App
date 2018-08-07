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

import org.json.JSONException;
import org.json.JSONObject;

public class JoinQuizActivity extends AppCompatActivity {

    private Socket socket;

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
        socket.connect();
    }

    /*
     * On Activity Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
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

        Intent intent = new Intent(this, WaitingActivity.class);
        startActivity(intent);

    }
}
