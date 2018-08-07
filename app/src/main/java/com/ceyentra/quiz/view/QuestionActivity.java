package com.ceyentra.quiz.view;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceyentra.quiz.R;
import com.ceyentra.quiz.data.Question;
import com.ceyentra.quiz.socket.SocketConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuestionActivity extends AppCompatActivity{

    ProgressBar mProgressBar, mProgressBar1;
    private TextView textViewShowTime;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;

    private Socket socket;
    private boolean userAnswered=false;
    private int userGivenAnswer=-1;
    private Question question;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        textViewShowTime = (TextView) findViewById(R.id.textView_timerview_time);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);

        setTimer();
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar1.setVisibility(View.VISIBLE);

        this.socket = SocketConnection.getInstance().getSocket();
        if (!this.socket.connected()) {
            this.socket.connect();
        }

        this.socket.on("Countdown_after",onCountDouwnAfter);
        this.socket.on("Question",onQuestionUpdate);
        this.socket.connect();

    }

    private void setTimer(){
        totalTimeCountInMilliseconds =  11 * 1000;
        mProgressBar1.setMax( 10 * 1000);
    }

    /*
     * Run Countdown
     */
    private Emitter.Listener onCountDouwnAfter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuestionActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        int seconds = Integer.parseInt(data.getString("remaing_sconds"));

                        if (seconds <= 0){
                            return;
                        }
                        textViewShowTime.setText(String.format("%02d", seconds));
                        mProgressBar1.setProgress(seconds * 1000);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /*
     * Load questions.
     */
    private Emitter.Listener onQuestionUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuestionActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String question = data.getString("question");
                        JSONArray options = data.getJSONArray("options");
                        String [] answers = new String[4];

                        for (int i=0;i<4;i++){
                            answers[i]=options.getString(0);
                        }

                        setQuestion(question,answers);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /*
     * Set question
     */
    private void setQuestion(String question, String [] answers){
        this.question = new Question();
        this.question.setQuestion(question);
        this.question.setAnswers(answers);

        TextView answer1 = findViewById(R.id.answer1);
        answer1.setText(answers[0]);

        TextView answer2 = findViewById(R.id.answer2);
        answer2.setText(answers[1]);

        TextView answer3 = findViewById(R.id.answer3);
        answer3.setText(answers[2]);

        TextView answer4 = findViewById(R.id.answer4);
        answer4.setText(answers[3]);

        TextView ques = findViewById(R.id.question);
        ques.setText(question);

    }


    /*
     * This method recognise the answer that user have been given
     */
    public void onClickAnswer(View view){
        if(userAnswered){
            return;
        }
        LinearLayout view_id = findViewById(view.getId());
        ((TextView)view_id.getChildAt(0)).setBackground(this.getResources().getDrawable(R.drawable.user_answer));
        userGivenAnswer = question.getAnswerIndex(((TextView) view_id.getChildAt(0)).getText().toString());
        userAnswered = true;
    }
}
