package com.example.quiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class btnFrag2 extends Fragment implements Button.OnClickListener {
    Button right;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public btnFrag2() {
        // Required empty public constructor
    }

    public static btnFrag2 newInstance(String param1, String param2) {
        btnFrag2 fragment = new btnFrag2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btn_frag2, container, false);
        right = (Button)view.findViewById(R.id.btnRight);
        RunQuiz main = (RunQuiz) getActivity();
        String a = main.answer;
        String b = main.chosenAnswer;
        //check whether answer choice was correct, if so increment the counter to keep track
        //display correct answer
        if (a.equals(b)) {
            main.setCorrect();
            right.setText("Correct! Click to Continue");
        } else {
            right.setText("Correct answer was: " + a + " . Click to Continue");
        }

        right.setOnClickListener(this);
        return view;
    }

    @Override
    //generate new question
    public void onClick(View v) {
        RunQuiz main = (RunQuiz) getActivity();
        main.onSubmit(v);
        main.swap(main.submit);
    }


}