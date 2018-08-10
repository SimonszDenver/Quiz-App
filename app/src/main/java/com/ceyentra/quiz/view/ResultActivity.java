package com.ceyentra.quiz.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ceyentra.quiz.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String user = getIntent().getStringExtra("user");
        int marks = getIntent().getIntExtra("marks",0);
        int rank = getIntent().getIntExtra("rank",0);

        setValues(user,marks,rank);
    }

    private void setValues(String user, int marks, int rank){
        TextView name = findViewById(R.id.name);
        TextView markView = findViewById(R.id.marks);
        TextView rankView = findViewById(R.id.rank);

        name.setText(user);
        markView.setText(""+marks);
        rankView.setText(""+rank);
    }
}
