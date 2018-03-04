package com.example.daren.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Daren on 06/12/2017.
 */

public class presession extends Fragment{

    View myView;
    private Button startNewSession;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.presession,container,false);
        startNewSession = myView.findViewById(R.id.presessionstart);

        startNewSession.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new VoiceRecognitionActivity()).commit();
            }
        });



        return myView;
    }
}
