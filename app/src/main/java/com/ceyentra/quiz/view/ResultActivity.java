package com.ceyentra.quiz.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JoinQuizActivity.state = 0;
    }

    private void setValues(String user, int marks, int rank){
        TextView name = findViewById(R.id.name);
        TextView markView = findViewById(R.id.marks);
        ImageView rankView = findViewById(R.id.cup);

        name.setText(user);
        markView.setText(""+marks);

        switch (rank){
            case 1: rankView.setImageResource(R.drawable.first_place); break;
            case 2: rankView.setImageResource(R.drawable.second_place); break;
            case 3: rankView.setImageResource(R.drawable.third_place); break;
            case 4: rankView.setImageResource(R.drawable.fourth_place); break;
            case 5: rankView.setImageResource(R.drawable.fifth_place); break;
            default: rankView.setImageResource(R.drawable.any_place);
        }


//        rankView.setText(""+rank);
    }

}
