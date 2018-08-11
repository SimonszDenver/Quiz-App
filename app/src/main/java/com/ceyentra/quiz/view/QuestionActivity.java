package com.ceyentra.quiz.view;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ceyentra.quiz.R;
import com.ceyentra.quiz.data.Question;
import com.ceyentra.quiz.data.UserData;
import com.ceyentra.quiz.socket.SocketConnection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class QuestionActivity extends AppCompatActivity{

    ProgressBar mProgressBar, mProgressBar1;
    private TextView textViewShowTime;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;

    private Socket socket;
    private boolean userAnswered=false;
    private int userGivenAnswer=-1;
    private Question question;
    private int correctAnswer = -1;
    private String [] answersArray;


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

        this.socket.on("Countdown_after",onCountDownAfter);
        this.socket.on("Question",onQuestionUpdate);
        this.socket.on("Answer", onGivenAnswer);
        this.socket.on("Result", onResult);
        this.socket.connect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JoinQuizActivity.state = 0;
        this.socket.off("Countdown_after");
        this.socket.off("Question");
        this.socket.off("Answer");
        this.socket.off("Result");
    }

    /*
     * Set a timer
     */
    private void setTimer(){
        totalTimeCountInMilliseconds =  11 * 1000;
        mProgressBar1.setMax( JoinQuizActivity.time * 1000);
    }

    /*
     * Run Countdown
     */
    private Emitter.Listener onCountDownAfter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuestionActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        int seconds = Integer.parseInt(data.getString("remaining_seconds"));

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
                        int question_number = data.getInt("question_number");
                        JSONArray options = data.getJSONArray("options");

                        String [] answers = new String[4];

                        for (int i=0;i<4;i++){
                            answers[i]=options.getString(i);
                        }

                        resetQuestion();
                        setQuestion(question_number,question,answers);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void resetQuestion(){
        if (userGivenAnswer != -1) {
            TextView selectedTextView = getSelectedTextView((userGivenAnswer));
            selectedTextView.setBackground(this.getResources().getDrawable(R.drawable.question_background));
            TextView correctAnswer = getSelectedTextView(getUserAnswer(question.getCorrectAnswer(this.correctAnswer)));
            correctAnswer.setBackground(this.getResources().getDrawable(R.drawable.question_background));
            this.userGivenAnswer = -1;
            this.userAnswered = false;
        }else{
            if (answersArray == null){
                return;
            }
            TextView correctAnswer = getSelectedTextView(getUserAnswer(question.getCorrectAnswer(this.correctAnswer)));
            if (null == correctAnswer){
                return;
            }
            correctAnswer.setBackground(this.getResources().getDrawable(R.drawable.question_background));
        }
    }


    /*
     * Trigger when server returns the answer
     */
    private Emitter.Listener onGivenAnswer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuestionActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args [0];

                    try {
                        JSONArray state_of_competition = data.getJSONArray("state_of_competition");
                        checkCorrectAnswer(state_of_competition);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /*
     *
     */
    private Emitter.Listener onResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuestionActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args [0];

                    try {
                        JSONArray points = data.getJSONArray("points_of_competition");
                        setResult(points);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void setResult(JSONArray points) throws JSONException {
        JSONObject user = null;
        int marks = 0;
        int rank = 0;
        for (int i=0;i < points.length(); i++){
            JSONArray usermarks = (JSONArray) points.get(i);
            user = (JSONObject) usermarks.get(0);
            if (user.getString("username").equals(UserData.getInstance().getUsername())){
                marks = usermarks.getInt(1);
                rank = usermarks.getInt(2);
                break;
            }
        }

        if (user == null){
            return;
        }

        Intent intent = new Intent(this,ResultActivity.class);
        intent.putExtra("user",user.getString("username"));
        intent.putExtra("marks",marks);
        intent.putExtra("rank",rank);
        startActivity(intent);
        finish();
    }

    /*
     * Check Correct Answer
     * correctness --->
     *      0 => wrong
     *      1 => correct
     */
    private void checkCorrectAnswer(JSONArray competitionState){

        String username = null;
        int correctness = -1;
        int correctAnswer = -1;
        for(int i=0; i< competitionState.length(); i++){
            try {
                JSONArray user = (JSONArray) competitionState.get(i);
                JSONObject jsonObject = user.getJSONObject(0);
                username = jsonObject.getString("username");

                if (username.equals(UserData.getInstance().getUsername())){
                    correctness = user.getInt(1);
                    correctAnswer = user.getInt(2);
                    this.correctAnswer = correctAnswer;
                     break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String registered_username = UserData.getInstance().getUsername();

        if (registered_username.equals(username)){
            TextView answer = getSelectedTextView((userGivenAnswer));
            if (correctness == 1){
                if (null != answer){
                    answer.setBackground(this.getResources().getDrawable(R.drawable.correct_answer));
                }
            }else{
                if (null != answer){
                    answer.setBackground(this.getResources().getDrawable(R.drawable.wrong_answer));
                    TextView correctAnswerView = getSelectedTextView(getUserAnswer(question.getCorrectAnswer(correctAnswer)));
                    correctAnswerView.setBackground(this.getResources().getDrawable(R.drawable.correct_answer));
                }else{
                    TextView correctAnswerView = getSelectedTextView(getUserAnswer(question.getCorrectAnswer(correctAnswer)));
                    correctAnswerView.setBackground(this.getResources().getDrawable(R.drawable.correct_answer));
                }
            }
        }

    }

    /*
     * Return selected text view
     */
    private TextView getSelectedTextView(int correctAnswer){
        switch (correctAnswer){
            case 1 : return findViewById(R.id.answer1);
            case 2 : return findViewById(R.id.answer2);
            case 3 : return findViewById(R.id.answer3);
            case 4 : return findViewById(R.id.answer4);
            default: return null;
        }
    }

    /*
     * Set question
     */
    private void setQuestion(int question_number, String question, String [] answers){
        this.question = new Question();
        this.question.setQuestionNO(question_number);
        this.question.setQuestion(question);
        this.question.setAnswers(answers);

        this.answersArray = new String[]{answers[0],answers[1],answers[2],answers[3]};

        int randomNumber = new Random().nextInt(100)/4 + 1;
        System.out.println(randomNumber);

        for(int i=0;i<randomNumber;i++){
            String value = answersArray[0];
            answersArray[0] = answersArray[3];
            answersArray[3] = answersArray[2];
            answersArray[2] = answersArray[1];
            answersArray[1] = value;
            System.out.println(answersArray[0]);
        }

        TextView answer1 = findViewById(R.id.answer1);
        answer1.setText(answersArray[0]);

        TextView answer2 = findViewById(R.id.answer2);
        answer2.setText(answersArray[1]);

        TextView answer3 = findViewById(R.id.answer3);
        answer3.setText(answersArray[2]);

        TextView answer4 = findViewById(R.id.answer4);
        answer4.setText(answersArray[3]);

        TextView ques = findViewById(R.id.question);
        ques.setText(question);

    }


    /*
     * This method recognise the answer that user have been given
     */
/*    public void onClickAnswer(View view){
        if(userAnswered){
            return;
        }
        LinearLayout view_id = findViewById(view.getId());
        ((TextView)view_id.getChildAt(0)).setBackground(this.getResources().getDrawable(R.drawable.user_answer));
        userGivenAnswer = question.getAnswerIndex(((TextView) view_id.getChildAt(0)).getText().toString());

        *//*
         * Set JSON to give answer
         *//*
        JSONObject jsonObject = new JSONObject();

        // answer 1 2 3 4
        try {
            jsonObject.put("answer", (userGivenAnswer+1));
            jsonObject.put("question_number", this.question.getQuestionNO());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        *//*
         * Emit json
         *//*
        socket.emit("Give_answer", jsonObject);
        userAnswered = true;
    }*/

    public void onClickAnswer(View view){
        if(userAnswered){
            return;
        }
        TextView clickedView = findViewById(view.getId());
        clickedView.setBackground(this.getResources().getDrawable(R.drawable.user_answer));
        userGivenAnswer = getUserAnswer(clickedView.getText().toString());

        /*
         * Set JSON to give answer
         */
        JSONObject jsonObject = new JSONObject();

        // answer 1 2 3 4
        try {
            jsonObject.put("answer", question.getAnswerIndex(clickedView.getText().toString()));
            jsonObject.put("question_number", this.question.getQuestionNO());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*
         * Emit json
         */
        socket.emit("Give_answer", jsonObject);
        userAnswered = true;
    }


    public int getUserAnswer(String answer){
        for (int i=0;i<answersArray.length;i++){
            if (answer.equals(answersArray[i])){
                return i+1;
            }
        }
        return -1;
    }
}
