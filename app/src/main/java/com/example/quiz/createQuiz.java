package com.example.quiz;
/* This activity gives the option of creating or editing a quiz.
* If edit, you are given the chance to delete a question as well
* as changing the answer choice,key, and creating new question.
* Delete all the question means the file is then deleted as no questions are left.
*
* In create you can only create questions. To create another quiz,
* go back to main and reclick on create. For create, if you change file
* name then it will create another file, and upon the first question
* creation of the new file, you will then be able to see its question
* and answer. If you change the file but have not submit then you
* only see the questions from before and not the answer as it is not the
* same file
*
* Clicking on the main button gets you back to the main menu
* */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;

import java.io.OutputStreamWriter;

import java.util.ArrayList;



public class createQuiz extends AppCompatActivity {
    String [] quizQuestions;
    ArrayList<String> qTemp = new ArrayList<>();
    int mode, selection;
    RecyclerView quizList;
    EditText fileName, quizName;
    Button delete;
    TextView fileText, nameText;
    EditText[] edittext = new EditText[5];
    String getFileIntent, path;
    RadioButton correct1,correct2,correct3,correct4;
    ArrayList<String> quizListTemp,createTemp;
    int first = 0, editAdd = 0, file_one=0;
    //watch for change in the name of the quiz//cannot be change depending on each file
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            quizName.setEnabled(false);
        }
    };
    //watch for multiple file creation
    private TextWatcher textWatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            first = 0;
            createTemp.clear();
            quizName.getText().clear();
            quizName.setEnabled(true);
            file_one = 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        quizList = (RecyclerView) findViewById(R.id.quizList);
        fileName = findViewById(R.id.filequizname);
        fileText = findViewById(R.id.filename);
        nameText = findViewById(R.id.qname);
        quizName = findViewById(R.id.name);
        delete = findViewById(R.id.delete);
        edittext[0] = findViewById(R.id.question);
        edittext[1] = findViewById(R.id.answer1Input);
        edittext[2] = findViewById(R.id.answer2Input);
        edittext[3] = findViewById(R.id.answer3Input);
        edittext[4]= findViewById(R.id.answer4Input);
        correct1= findViewById(R.id.answer1);
        correct2= findViewById(R.id.answer2);
        correct3= findViewById(R.id.answer3);
        correct4= findViewById(R.id.answer4);
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode",1);
        //edit mode, can delete
        if (mode == 0) {
            quizListTemp = intent.getStringArrayListExtra("QuizA");
            getFileIntent = intent.getStringExtra("File");
            path = intent.getStringExtra("Path");
            fileText.setText("File name");
            nameText.setText("Quiz name");
            quizName.setText(quizListTemp.get(0));
            fileName.setText(getFileIntent);
            fileName.setEnabled(false);
            quizName.setEnabled(false);
            delete.setVisibility(View.VISIBLE);
            getQuestions(quizListTemp);
            getAnswers(quizListTemp);
        }
        //create mode, only create new files and make questions
        else if(mode==1){
            createTemp = new ArrayList<>();
            fileName.addTextChangedListener(textWatcher1);
            quizName.addTextChangedListener(textWatcher);
        }
        else{

        }

    }

    //retrieve answers to be display and find the one marked correct, light up the radio button for correct answe
    private void getAnswers(ArrayList<String> x) {
        if(x.size() >0) {
            int index = selection * 5;
            String a = x.get(2 + index++);
            String b = x.get(2 + index++);
            String c = x.get(2 + index++);
            String d = x.get(2 + index++);
            if (a.charAt(0) == '*') {
                a = a.replace("*", "");
                edittext[1].setText(a);
                correct1.setChecked(true);
                correct2.setChecked(false);
                correct3.setChecked(false);
                correct4.setChecked(false);
            } else {
                edittext[1].setText(a);
            }

            if (b.charAt(0) == '*') {
                b = b.replace("*", "");
                edittext[2].setText(b);
                correct1.setChecked(false);
                correct2.setChecked(true);
                correct3.setChecked(false);
                correct4.setChecked(false);
            } else {
                edittext[2].setText(b);
            }
            if (c.charAt(0) == '*') {
                c = c.replace("*", "");
                edittext[3].setText(c);
                correct1.setChecked(false);
                correct2.setChecked(false);
                correct3.setChecked(true);
                correct4.setChecked(false);
            } else {
                edittext[3].setText(c);
            }
            if (d.charAt(0) == '*') {
                d = d.replace("*", "");
                edittext[4].setText(d);
                correct1.setChecked(false);
                correct2.setChecked(false);
                correct3.setChecked(false);
                correct4.setChecked(true);
            } else {
                edittext[4].setText(d);
            }
        }
        else{
            clear();
        }

    }

    //retrieve questions to be displayed
    private void getQuestions(ArrayList<String> x) {
        qTemp.clear();

        for (int i = 1; i < x.size(); i += 5) {
            qTemp.add(x.get(i));
        }

        quizQuestions = qTemp.toArray(new String[qTemp.size()]);
        getQuiz();
    }

    //set up the recycler view
    public void getQuiz(){
        //set up the adapter
        SimpleAdapter adapter = new SimpleAdapter(quizQuestions, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        quizList.setLayoutManager(layoutManager);
        quizList.setAdapter(adapter);
        edittext[0].setText(quizQuestions[0]);
    }

    //correct button makes it so only 1 can be clicked
    public void correct1(View view) {
        correct1.setChecked(true);
        correct2.setChecked(false);
        correct3.setChecked(false);
        correct4.setChecked(false);
    }

    public void correct2(View view) {
        correct1.setChecked(false);
        correct2.setChecked(true);
        correct3.setChecked(false);
        correct4.setChecked(false);
    }

    public void correct3(View view) {
        correct1.setChecked(false);
        correct2.setChecked(false);
        correct3.setChecked(true);
        correct4.setChecked(false);
    }

    public void correct4(View view) {
        correct1.setChecked(false);
        correct2.setChecked(false);
        correct3.setChecked(false);
        correct4.setChecked(true);
    }

    //return to main, everything is saved in file
    public void doneButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //clear all input fields
    public void clear(){
        correct1.setChecked(false);
        correct2.setChecked(false);
        correct3.setChecked(false);
        correct4.setChecked(false);
        edittext[0].getText().clear();
        edittext[1].getText().clear();
        edittext[2].getText().clear();
        edittext[3].getText().clear();
        edittext[4].getText().clear();
    }

    //calls clear
    public void clearButton(View view) {
        if(mode==0){
            editAdd=1;
        }
        clear();
    }

    //save button for edit mode where it edits the stuff to be stored in file
    public void edit(){
        boolean one,two,three,four;
        one = correct1.isChecked();
        two = correct2.isChecked();
        three = correct3.isChecked();
        four = correct4.isChecked();

        //q&a
        String q = edittext[0].getText().toString();
        String a = edittext[1].getText().toString();
        String b = edittext[2].getText().toString();
        String c = edittext[3].getText().toString();
        String d = edittext[4].getText().toString();

        //create a temporary arraylist which stores the current item
        ArrayList<String> temp = new ArrayList<String>();
        int j = 0;
        for(int i = 0 ; i < quizListTemp.size() ; i++) {
            temp.add(quizListTemp.get(i));
        }
        quizListTemp.clear();

        //always store the quiz name
        if (selection == 0) {
            quizListTemp.add(quizName.getText().toString());
            quizListTemp.add(q);
            for(int i = 1 ; i < edittext.length ; i++) {
                if(one && i==1) {
                    quizListTemp.add("*" + edittext[i].getText().toString());
                }
                else if(two && i==2) {
                    quizListTemp.add("*" + edittext[i].getText().toString());
                }
                else if(three && i==3) {
                    quizListTemp.add("*" + edittext[i].getText().toString());
                }
                else if(four&& i==4) {
                    quizListTemp.add("*" + edittext[i].getText().toString());
                }
                else{
                    quizListTemp.add(edittext[i].getText().toString());
                }
            }
            //if the question to be deleted is the first content, directly skip over it
            for(int i = 6 ; i < temp.size() ; i++) {
                quizListTemp.add(temp.get(i));
            }
        } else {
            //otherwise, find the location of where the questions resides in the file
            for(j = 0 ; j < temp.size() ; j++) {
                if((temp.get(j)) == (quizQuestions[selection])) {
                    break;
                }
            }
            for(int i = 0 ; i < (temp.size()) ; i++) {

                if(i == j) {
                    quizListTemp.add(q);
                    i++;
                    for(int x = 1 ; x < edittext.length ; x++) {
                        //store the answer that is selected as correct to be with an asterick
                        if(one && x==1) {
                            quizListTemp.add("*"  + edittext[x].getText().toString());
                        }
                        else if(two && x==2) {
                            quizListTemp.add("*" + edittext[x].getText().toString());
                        }
                        else if(three && x==3) {
                            quizListTemp.add("*" + edittext[x].getText().toString());
                        }
                        else if(four&& x==4) {
                            quizListTemp.add("*" + edittext[x].getText().toString());
                        }
                        else{
                            quizListTemp.add(edittext[x].getText().toString());
                        }

                        i++;
                    }
                    if(i == temp.size()) {
                        break;
                    }
                }
                quizListTemp.add(temp.get(i));
            }
        }
    }


    //save button has 2 mode
    public void saveButton(View view) throws IOException {
        //see which one is the correct answer
        boolean one,two,three,four;
        one = correct1.isChecked();
        two = correct2.isChecked();
        three = correct3.isChecked();
        four = correct4.isChecked();

        //q&a
        String q = edittext[0].getText().toString();
        String a = edittext[1].getText().toString();
        String b = edittext[2].getText().toString();
        String c = edittext[3].getText().toString();
        String d = edittext[4].getText().toString();

        //need both file name and quizname
        String fNAME = fileName.getText().toString();
        String qNAME = quizName.getText().toString();

        //check for valid input
        if (fNAME.length() == 0 || quizName.length()==0 || a.length()==0|| q.length()==0||b.length()==0|| c.length()==0 || d.length()==0) {
            if(qNAME.length()==0){
                quizName.setError("Quiz Name required");
            }
            if(fNAME.length() == 0) {
                fileName.setError("File Name required");
            }
            if(q.length()==0){
                edittext[0].setError("Question required");
            }
            if(a.length()==0){
                edittext[1].setError("Answer required");
            }
            if(b.length()==0){
                edittext[2].setError("Answer required");
            }
            if(c.length()==0){
                edittext[3].setError("Answer required");
            }
            if(d.length()==0){
                edittext[4].setError("Answer required");
            }

        }
        else {
            //save button functionality for edit
            if (mode == 0) {
                //add mode for edit mode
                if (editAdd == 1) {
                    editAdd = 0;
                    ArrayList<String> temp = new ArrayList<String>();
                    for(int i = 0 ; i < quizListTemp.size() ; i++) {
                        temp.add(quizListTemp.get(i));
                    }
                    quizListTemp.clear();
                    if (!one && !two & !three && !four) {
                        Toast.makeText(this, "Answer choice required", Toast.LENGTH_SHORT).show();
                    } else {
                        if (one) {
                            temp.add(q );
                            temp.add("*" + a );
                            temp.add(b );
                            temp.add(c );
                            temp.add(d);
                        }
                        if (two) {
                            temp.add(q );
                            temp.add(a );
                            temp.add("*" + b );
                            temp.add(c );
                            temp.add(d);
                        }
                        if (three) {
                            temp.add(q );
                            temp.add(a );
                            temp.add(b );
                            temp.add("*" + c );
                            temp.add(d);
                        }
                        if (four) {
                            temp.add(q);
                            temp.add( a);
                            temp.add(b );
                            temp.add(c );
                            temp.add("*" + d);
                        }

                        for(int i = 0 ; i < temp.size() ; i++) {
                            quizListTemp.add(temp.get(i));
                        }

                        FileOutputStream fileout = openFileOutput(fileName.getText().toString(), MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        for (int i = 0; i < quizListTemp.size(); i++) {
                            outputWriter.write(quizListTemp.get(i) + "\n");
                        }

                        outputWriter.close();
                        fileout.close();
                        getQuestions(quizListTemp);
                        getAnswers(quizListTemp);
                    }
                }
                //normal save for edit mode
                else {
                    try {
                        edit();
                        FileOutputStream fileout = openFileOutput(fileName.getText().toString(), MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        for (int i = 0; i < quizListTemp.size(); i++) {
                            outputWriter.write(quizListTemp.get(i) + "\n");
                        }

                        outputWriter.close();
                        fileout.close();
                        Toast.makeText(getBaseContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getQuestions(quizListTemp);
                    getAnswers(quizListTemp);
                }

            }
            //save button functionality for create
            else {
                if (!one && !two & !three && !four) {
                    Toast.makeText(this, "Answer choice required", Toast.LENGTH_SHORT).show();
                } else {
                    if(first == 0 || file_one==0){
                        createTemp.add(fNAME);
                        first = 1;
                        file_one=1;
                    }
                    ArrayList<String> temp = new ArrayList<String>();
                    for(int i = 0 ; i < createTemp.size() ; i++) {
                        temp.add(createTemp.get(i));
                    }
                    createTemp.clear();
                    if (one) {
                        temp.add( q );
                        temp.add("*" +a );
                        temp.add(b );
                        temp.add(c);
                        temp.add(d);
                    }
                    else if (two) {
                        temp.add(q );
                        temp.add(a );
                        temp.add("*" +b );
                        temp.add(c );
                        temp.add(d);
                    }
                    else if (three) {
                        temp.add( q );
                        temp.add(a );
                        temp.add(b );
                        temp.add("*" +c );
                        temp.add(d);
                    }
                    else{
                        temp.add(q );
                        temp.add(a );
                        temp.add(b );
                        temp.add(c );
                        temp.add("*" + d);
                    }

                    for(int i = 0 ; i < temp.size() ; i++) {
                        createTemp.add(temp.get(i));
                    }

                    FileOutputStream fileout = openFileOutput("Quiz" + fileName.getText().toString() + ".txt", MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    for (int i = 0; i < createTemp.size(); i++) {
                        outputWriter.write(createTemp.get(i) + "\n");
                    }

                    outputWriter.close();
                    fileout.close();
                    getQuestions(createTemp);
                    getAnswers(createTemp);
                    clear();
                }
            }
        }
    }

    //traverse the list to get all the question, find the question and skip over it thus "deleting"
    public void delete(){
        ArrayList<String> temp = new ArrayList<String>();
        int j = 0;
        if(quizListTemp.size()>0){
            for (int i = 0; i < quizListTemp.size(); i++) {
                temp.add(quizListTemp.get(i));
            }
            quizListTemp.clear();

            if (selection == 0) {
                quizListTemp.add(quizName.getText().toString());
                for (int i = 6; i < temp.size(); i++) {
                    quizListTemp.add(temp.get(i));
                }
            } else {
                for (j = 0; j < temp.size(); j++) {
                    if ((temp.get(j)) == (quizQuestions[selection])) {
                        break;
                    }
                }
                for (int i = 0; i < (temp.size()); i++) {
                    if (i == j) {
                        i++;
                        for (int x = 1; x < edittext.length; x++) {
                            i++;
                        }
                        if (i == temp.size()) {
                            break;
                        }
                    }
                    quizListTemp.add(temp.get(i));
                }
            }
        }
        else{
            quizListTemp.clear();
        }
    }

    //delete an item, the recycler view must also refresh
    public void deleteButton(View view) throws IOException {
        if(quizListTemp.size() >6) {
            try {
                delete();
                FileOutputStream fileout = openFileOutput(fileName.getText().toString(), MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                for (int i = 0; i < quizListTemp.size(); i++) {
                    outputWriter.write(quizListTemp.get(i) + "\n");
                }
                outputWriter.close();
                Toast.makeText(getBaseContext(), "Question deleted successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            selection = 0;
            getQuestions(quizListTemp);
            getAnswers(quizListTemp);
        }
        else{
            File file = new File(path);
            if(file.delete()){
                System.out.println("File deleted");
            }else System.out.println("File delete error");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    //will tell what question is selected
    public void setSelection(int s){
        selection = s;
        if(mode==0) {
            placeQuestion();
            getAnswers(quizListTemp);
        }
        else{
            placeQuestion();
            getAnswers(createTemp);
        }
    }

    //set the selected question into the question field
    private void placeQuestion() {
        edittext[0].setText(quizQuestions[selection]);
    }

    //recycler view to display all  of the questions of a list
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ListRow>{
        private String[] mQuiz;
        private int selectedItem = 0;
        createQuiz parent;

        //take array of string to be shown in list
        public SimpleAdapter(String[] q,  createQuiz p){
            mQuiz = q;
            parent = p;
        }
        @NonNull
        @Override
        //when viewholder created, return holder w the view
        public SimpleAdapter.ListRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizlist_item, parent, false);
            return new SimpleAdapter.ListRow(v);
        }

        @Override
        //when view is bound, get data to display for every list item
        public void onBindViewHolder(@NonNull SimpleAdapter.ListRow holder, int position) {
            holder.mQuizname.setText(mQuiz[position]);
            if(selectedItem == position){
                holder.itemView.setBackgroundColor(Color.CYAN);
            }
            else
                holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        //get how many item to fit screen
        public int getItemCount() {
            return mQuiz == null ? 0 : mQuiz.length;
        }


        //view holder for each row of RecyclerView
        public class ListRow extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView mQuizname;
            public ListRow(View view){
                super(view);
                mQuizname = (TextView) view.findViewById(R.id.QuizName);
                view.setOnClickListener(this);
            }


            //save which item is clicked and notify parent
            @Override
            public void onClick(View v) {
                int iPos = quizList.getChildLayoutPosition(v);
                notifyItemChanged(selectedItem);
                selectedItem = getLayoutPosition();
                parent.setSelection(selectedItem);
                notifyItemChanged(selectedItem);
            }
        }
    }

}