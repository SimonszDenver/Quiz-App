package com.ceyentra.quiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class JoinQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_quiz);
    }

    public void onGettingStarted(View view){
        Intent intent = new Intent(this,StartingQuizActivity.class);
        startActivity(intent);
    }
}
