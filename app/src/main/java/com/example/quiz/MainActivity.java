package com.example.quiz;
/******************************************************************************
 * Quiz Program.
 *
 * This program reads in available quiz text file. Users will be prompted to
 * enter their name and select a quiz. There will be two choice of quiz: a local and global.
 *
 * The program will find all quiz files and display the name of the quiz.
 * For this to work there must be a files directory containing .txt files with
 * the starting name as "Quiz". Without, it will not run as no quiz displays, otherwise
 * to proceed simply click the next button at the bottom.
 *
 * The quiz will go in order of available questions. Each question holds 4 answer.
 * The program will calculate the correct answer amount and generate a score at the end
 * 
 * Upon entering the name professor, regardless of capitalizations in letters, the
 * user can now create or edit a quiz. Create and edit will deal with the local quizzes only
 * and not the online. To get back to taking a quiz, simply have the name entered not be
 * equal to professor.
 *
 * Written by Nhu Le
 * February 25 2021, 2nd phase with asynch and network on March 11,2021, finalize with
 * allowing for user to create/edit quizzes on April 4,2021
 ******************************************************************************/

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    File [] quizzes;
    ArrayList<String> quizNames,fileNames,t;
    RecyclerView quizList;
    File filesDir;
    int selection;
    String [] quizNamesArray,arr;
    Button nextButton,createButton,editButton;
    EditText name;
    String nameEnter;
    AsyncQuiz as;
    AsyncQuestion aq;
    RadioButton online,local;
    String nameInput;
    int mode = 0;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameInput = name.getText().toString().toLowerCase();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(nameInput.equals("professor")){
                nextButton.setVisibility(View.INVISIBLE);
                online.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);
                createButton.setVisibility(View.VISIBLE);
            }
            else{
                nextButton.setVisibility(View.VISIBLE);
                online.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                createButton.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //keeps button from moving up when keyboard for name comes up
        nextButton = findViewById(R.id.takequiz);
        createButton = findViewById(R.id.createquiz);
        editButton = findViewById(R.id.editquiz);
        quizList = (RecyclerView) findViewById(R.id.quizList);
        quizNames = new ArrayList<String>();
        fileNames = new ArrayList<String>();
        t=new ArrayList<String>();
        name = (EditText) findViewById(R.id.name);
        online = findViewById(R.id.onlineQuiz);
        local = findViewById(R.id.localQuiz);
        name.addTextChangedListener(textWatcher);
        createLocal();
    }

    //get file data from URL, use async to store info from url to then be displayed through recycler view
    public void createOnline(){
        as = new AsyncQuiz(this);
        String quizURL = "https://personal.utdallas.edu/~john.cole/Data/Quizzes.txt";
        as.execute(quizURL);
    }

    //gets file from local directory and store it into and arraylist. Iterate through to get names and set it in recycler view
    public void createLocal(){
        filesDir = getFilesDir();

        //retrieve all file that is name Quiz*.txt
        FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.lastIndexOf('.') > 0) {
                    //Get the index of the last instance of .
                    int index = name.lastIndexOf('.');
                    //Get the substring from . and onwards
                    String txt = name.substring(index);
                    //get all text file that starts w Quiz
                    if (txt.equals(".txt") && name.startsWith("Quiz")) {
                        fileNames.add(name);
                        return true;
                    }
                }
                return false;
            }
        };

        quizzes = filesDir.listFiles(ff);

        //retrieve list of quiz names
        try {
            for (int i = 0; i < quizzes.length; i++) {
                int line = 0;
                Scanner quiz = new Scanner(quizzes[i]);
                while (quiz.hasNext()) {
                    if (line == 0) {
                        String name = quiz.nextLine();
                        quizNames.add(name);
                        line++;
                    } else {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(quizNames.isEmpty()){
            local.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No local files!",
                    Toast.LENGTH_LONG).show();
        }
        else {
            quizNamesArray = quizNames.toArray(new String[quizNames.size()]);
            //set up the adapter
            SimpleAdapter adapter = new SimpleAdapter(quizNamesArray, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            quizList.setLayoutManager(layoutManager);
            quizList.setAdapter(adapter);
        }
    }

    //function when click will take you to the selected quiz; 2ns activity will begin with the question prompt
    public void nextButton(View view) throws IOException {
            nameEnter = name.getText().toString();
            if (nameEnter.length() == 0) {
                name.setError("Name required");
            } else {
                name.setText(nameEnter);
                createQuiz();   //will take the selected quiz and get file
                if (arr != null) {
                    Intent intent = new Intent(this, RunQuiz.class);
                    intent.putExtra("Quiz", arr);
                    intent.putExtra("username", nameEnter);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No quiz properly selected to take", Toast.LENGTH_SHORT).show();
                }
            }

    }

    //gets file ready to send over to second activity
    public void createQuiz() throws IOException {
        if(mode ==0 && !quizNames.isEmpty()) {
            try {
                //store file lines into array content
                int line = 0;
                Scanner quiz = new Scanner(quizzes[selection]);
                List<String> temps = new ArrayList<String>();
                while (quiz.hasNext()) {
                    String name = quiz.nextLine();
                    temps.add(name);
                    t.add(name);
                }
                quiz.close();
                arr = temps.toArray(new String[temps.size()]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(mode == 1) {
            onlineChange();
        }
    }

    //index of array
    public void setSelection(int s){
        selection = s;
    }

    //uses quiz file from local directory
    public void onRadioLocal(View view) {
        mode = 0;
        //delete everything from recycler view
        quizList.setAdapter(null);
        //uncheck the choice for the online radio button
        online.setChecked(false);

        //previously empty files, recheck if a quiz was created

            filesDir = getFilesDir();

            //retrieve all file that is name Quiz*.txt
            FilenameFilter ff = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.lastIndexOf('.') > 0) {
                        //Get the index of the last instance of .
                        int index = name.lastIndexOf('.');
                        //Get the substring from . and onwards
                        String txt = name.substring(index);
                        //get all text file that starts w Quiz
                        if (txt.equals(".txt") && name.startsWith("Quiz")) {
                            return true;
                        }
                    }
                    return false;
                }
            };

            quizzes = filesDir.listFiles(ff);
            quizNames.clear();

            //retrieve list of quiz names
            try {
                for (int i = 0; i < quizzes.length; i++) {
                    int line = 0;
                    Scanner quiz = new Scanner(quizzes[i]);
                    while (quiz.hasNext()) {
                        if (line == 0) {
                            String name = quiz.nextLine();
                            quizNames.add(name);
                            line++;
                        } else {
                            break;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        //set up the adapter
        quizNamesArray = quizNames.toArray(new String[quizNames.size()]);
        SimpleAdapter adapter = new SimpleAdapter(quizNamesArray, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        quizList.setLayoutManager(layoutManager);
        quizList.setAdapter(adapter);
    }

    //uses quiz files from online URL
    public void onRadioOnline(View view) {
        mode = 1;
        //uncheck the choice for the local radio button
        local.setChecked(false);
        //delete everything from recycler view
        quizList.setAdapter(null);
        createOnline();
    }

    //takes the selected quiz from online and store in the arr for quiz selection
    public void onlineChange(){
        String quizLinkSelect = quizNamesArray[selection];
        String beginLink = "https://personal.utdallas.edu/~john.cole/Data/" + quizLinkSelect;
        aq = new AsyncQuestion(this);
        aq.execute(beginLink);
    }

    public void editButton(View view) throws IOException {
        if(quizNames.isEmpty() || mode==1){
            Toast.makeText(this, "Edit can only be done on local files",
                    Toast.LENGTH_LONG).show();
        }
        else {
            String[] f = fileNames.toArray(new String[fileNames.size()]);
            createQuiz();
            int modeEnter = 0;
            Intent intent = new Intent(this, createQuiz.class);
            intent.putExtra("QuizA",t);
            intent.putExtra("File",f[selection].toString());
            intent.putExtra("Path",quizzes[selection].toString());
            intent.putExtra("mode", modeEnter);
            startActivity(intent);
        }
    }

    public void createButton(View view) {
       int modeEnter = 1;
       Intent intent = new Intent(this,createQuiz.class);
       intent.putExtra("mode", modeEnter);
       startActivity(intent);
    }

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ListRow>{
        private String[] mQuiz;
        private int selectedItem = 0;
        MainActivity parent = new MainActivity();

        //take array of string to be shown in list
        public SimpleAdapter(String[] q, MainActivity p){
            mQuiz = q;
            parent = p;
            onlineChange();
        }
        @NonNull
        @Override
        //when viewholder created, return holder w the view
        public ListRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizlist_item, parent, false);
            return new ListRow(v);
        }

        @Override
        //when view is bound, get data to display for every list item
        public void onBindViewHolder(@NonNull ListRow holder, int position) {
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
            onlineChange();
            notifyItemChanged(selectedItem);
        }
        }
    }

    //get names and data from online quiz
    private class AsyncQuiz extends AsyncTask<String, Void, ArrayList> {
        ArrayList quizOnlineList,quizOnlineList1;
        MainActivity context;

        //constructor takes activity so we can access it ui method
        public AsyncQuiz(MainActivity q){
            context = q;
        }

        @Override
        protected ArrayList doInBackground(String... uList) {
            URL u = null;
            String strLine;
            InputStream ins = null;
            Scanner scanner = null;
            int response = 0;
            String quizURL = uList[0];
            quizOnlineList = new ArrayList();
            quizOnlineList1 = new ArrayList();

            try{
                u = new URL(quizURL);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

            URLConnection connection = null;
            try{
                connection = u.openConnection();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

            HttpURLConnection httpConn =(HttpURLConnection) connection;
            try{
                response = httpConn.getResponseCode();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

            if(response == HttpURLConnection.HTTP_OK){
                try{
                    ins = httpConn.getInputStream();
                    scanner = new Scanner(ins);
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                try{
                    while(scanner.hasNext()){
                        strLine = scanner.nextLine();
                        quizOnlineList.add(strLine);

                    }
                    scanner.close();
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }

            int counter = 0;
            for(int i = 0;i<quizOnlineList.size();i++){
                try{
                    u = new URL( "https://personal.utdallas.edu/~john.cole/Data/" + quizOnlineList.get(counter));
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                connection = null;
                try{
                    connection = u.openConnection();
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }

                httpConn =(HttpURLConnection) connection;
                try{
                    response = httpConn.getResponseCode();
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                if(response == HttpURLConnection.HTTP_OK){
                    try{
                        ins = httpConn.getInputStream();
                        scanner = new Scanner(ins);
                    }
                    catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                    try{
                        strLine = scanner.nextLine();
                        quizOnlineList1.add(strLine);
                        scanner.close();
                    }
                    catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                }
                counter++;
            }

            httpConn.disconnect();
            return quizOnlineList1;
        }

        //get results back and publish to the ui
        @Override
        protected void onPostExecute(ArrayList arrayList) {
            quizNamesArray = (String[]) quizOnlineList.toArray(new String[arrayList.size()]);
            String[] a = (String[]) arrayList.toArray(new String[arrayList.size()]);
            SimpleAdapter adapter = new SimpleAdapter(a, context);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            quizList.setLayoutManager(layoutManager);
            quizList.setAdapter(adapter);
        }
    }

    //get questions and answers from selected online quiz
    private class AsyncQuestion extends AsyncTask<String, Void, ArrayList> {
        ArrayList quizQuestions;
        MainActivity context;

        //constructor takes activity so we can access it ui method
        public AsyncQuestion(MainActivity q){
            context = q;
        }

        @Override
        protected ArrayList doInBackground(String... uList) {
            URL u = null;
            String strLine;
            InputStream ins = null;
            Scanner scanner = null;
            int response = 0;
            String quizURL = uList[0];
            quizQuestions = new ArrayList();

            try{
                u = new URL(quizURL);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

            URLConnection connection = null;
            try{
                connection = u.openConnection();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

            HttpURLConnection httpConn =(HttpURLConnection) connection;
            try{
                response = httpConn.getResponseCode();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }

            if(response == HttpURLConnection.HTTP_OK){
                try{
                    ins = httpConn.getInputStream();
                    scanner = new Scanner(ins);
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                try{
                    while(scanner.hasNext()){
                        strLine = scanner.nextLine();
                        quizQuestions.add(strLine);
                    }
                    scanner.close();
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            httpConn.disconnect();
            return quizQuestions;
        }

        //get results back and publish to the ui
        @Override
        protected void onPostExecute(ArrayList arrayList) {
            try {
                arr = (String[]) arrayList.toArray(new String[arrayList.size()]);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

}