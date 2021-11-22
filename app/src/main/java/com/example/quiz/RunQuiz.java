/******************************************************************************
 * Quiz Program.
 * This file will display the actual quiz question and answer.
 * When an answer is picked the color will change to blue to indicate what is picked.
 * After the answer is submitted it will show what the correct answer is. If you submit
 * without choosing an answer it will be marked as incorrect.
 *
 * Written by Nhu Le (NLL170000)
 * February 25 2021
 ******************************************************************************/
package com.example.quiz;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class RunQuiz extends AppCompatActivity {
    TextView q,a,b,c,d;
    int finalScore = 0;
    int countQuestion = 0;
    int question = 1;
    int answers = 5;
    String temp, answer, quizName, username, chosenAnswer;
    TextView[] textViews= {q,a,b,c,d};
    String [] quiz;
    Fragment submit, correct;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_quiz);
        textViews[0]=(TextView)findViewById(R.id.question);
        textViews[1]=(TextView)findViewById(R.id.answer1);
        textViews[2]=(TextView)findViewById(R.id.answer2);
        textViews[3]=(TextView)findViewById(R.id.answer3);
        textViews[4]=(TextView)findViewById(R.id.answer4);
        submit = new btnFrag1();
        correct = new btnFrag2();
        //get the passed arraylist and store it
        Intent intent = getIntent();
        if(intent!=null){
            quiz = intent.getStringArrayExtra("Quiz");
        }
        quiz = intent.getStringArrayExtra("Quiz");
        username = intent.getStringExtra("username");

        //create submit fragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, submit);
        fragmentTransaction.commit();


        createQuiz(question,answers);

    }

    //switch between submit and answer fragment
    public void swap(Fragment x){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, x);
        fragmentTransaction.commit();
    }


    //function when click will take you to the final score activity
    public void score() {
        quizName = quiz[0];
        Intent intent = new Intent(this, score.class);
        intent.putExtra("name",username);
        intent.putExtra("score",finalScore);
        intent.putExtra("quiz",quizName);
        intent.putExtra("total",countQuestion);
        startActivity(intent);
    }

    //generate quiz question
    public void createQuiz(int question, int answers){
        this.chosenAnswer = null;
        //start new question w no answer selected
        for(int j =0; j<textViews.length;j++){
            textViews[j].setBackgroundColor(Color.WHITE);
        }
        //no questions from set left; go to score activity
        if(countQuestion == (quiz.length-1)/5){
            score();
            return;
        }
         //take each answer and question to display
        countQuestion++;
        int index = 0;
        for(int i = question; i <= answers; i++) {
            temp = quiz[i];
            //store correct answer
            if (temp.charAt(0) == '*') {
                temp = temp.replace("*", "");
                this.answer = temp;
            }
            textViews[index].setText(temp);
            index++;
        }
    }

    //set what's click to light blue
    public void bgColor(View view) {
        for(int i =0; i<textViews.length;i++){
            textViews[i].setBackgroundColor(Color.WHITE);
        }
        view.setBackgroundColor(Color.CYAN);
        this.chosenAnswer =  ((TextView) view).getText().toString();
    }

    //calculate how many answers was picked correctly
    public void setCorrect(){
        finalScore++;
    }

    //submit answet and call to generate next question
   public void onSubmit(View view) {
       if(chosenAnswer == null){
           Toast.makeText(this, "Answer choice required", Toast.LENGTH_SHORT).show();
       }
        else {
            question += 5;
            answers += 5;
            createQuiz(question, answers);
        }
    }



}