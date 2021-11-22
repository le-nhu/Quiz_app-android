package com.example.quiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class btnFrag1 extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public btnFrag1() {
        // Required empty public constructor
    }

    public static btnFrag1 newInstance(String param1, String param2) {
        btnFrag1 fragment = new btnFrag1();
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
        View view = inflater.inflate(R.layout.fragment_btn_frag1, container, false);
        Button submit = (Button)view.findViewById(R.id.btnEnter);
        submit.setOnClickListener(this);
        return view;
    }


    @Override
    //generate whther answer choice was correct or not
    public void onClick(View v) {
        RunQuiz main = (RunQuiz) getActivity();
        String b = main.chosenAnswer;
        if(b==null){
            Toast.makeText(getActivity(), "Answer choice required", Toast.LENGTH_SHORT).show();
        }
        else {
            main.swap(main.correct);
        }
    }
}