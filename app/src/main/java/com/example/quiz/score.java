package com.example.quiz;
/******************************************************************************
 * Quiz Program.
 *
 * This takes in the results, name, ans quiz to be displayed to user.
 * There will be an option to go back to the main activity. Upon so everything
 * will restart.
 *
 * Written by Nhu Le (NLL170000)
 * February 25 2021
 ******************************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.FilenameFilter;

public class score extends AppCompatActivity {
    TextView name;
    TextView quiz;
    TextView score;
    String username, quizName;
    int finalScore, total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        TextView name=(TextView)findViewById(R.id.name);
        TextView quiz=(TextView)findViewById(R.id.quiz);
        TextView score=(TextView)findViewById(R.id.score);

        //get the passed arraylist and store it
        Intent intent = getIntent();
        if(intent!=null){
            int iValue = intent.getIntExtra( "string",  0);

        }
        username = intent.getStringExtra("name");
        quizName = intent.getStringExtra("quiz");
        finalScore = intent.getIntExtra("score", 0);
        total = intent.getIntExtra("total", 0);

        //set to display name, quiz name, score
        name.setText(username + "\n");
        quiz.setText(quizName + "\n");
        score.setText(String.valueOf(finalScore) + "/" + String.valueOf(total)  + "\n");
    }

    //allow to go back to main activity screen
    public void onSubmit(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}