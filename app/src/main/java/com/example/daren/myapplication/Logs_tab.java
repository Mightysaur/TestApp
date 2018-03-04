package com.example.daren.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Daren on 06/12/2017.
 */

public class Logs_tab extends Fragment{

    View myView;
    private Context mContext;
    private ListView listViewLog;
    private ListView listViewWords;
    private ListView listViewDefinition;
    private int viewLayer;
    private FloatingActionButton backButton;
    private String[] sessions = {"1","2","3"};
    private String[][] sessions2 = {{"4"},{"5"},{"6"}};
    private ArrayList<String> sessions3 = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = this.getActivity();
        myView = inflater.inflate(R.layout.log,container,false);
        listViewLog = (ListView)myView.findViewById(R.id.logListView);
        listViewWords = (ListView)myView.findViewById(R.id.wordsListView);
        listViewDefinition = (ListView)myView.findViewById(R.id.definitionListView);
        backButton = myView.findViewById(R.id.logBackButton);
        sessions[0] = "4";

        File path = getActivity().getFilesDir();

        File file = new File(path, "sessions.txt");
        FileInputStream inputStream = null;
        StringBuilder text = new StringBuilder();
        try {
            inputStream = new FileInputStream(file);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;

                while ((line = br.readLine()) != null) {
                    //text = line;
                    sessions3.add(line);
                    //text.append(line);
                    //text.append('\n');
                }
                //sessions[0] = text.toString();
                //Toast.makeText(getActivity().getApplicationContext(), getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,sessions3 );

        listViewLog.setAdapter(listViewAdapter);
        //listViewWords.setAdapter(listViewAdapter2);
        //listViewDefinition.setAdapter(listViewAdapter);

        listViewLog.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
                navigateToWords(position);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     navigateBack();
                                                 }
                                             });
        viewLayer = 0;




        return myView;
    }

    public void navigateToWords(int position){
        ArrayAdapter<String> listViewAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,sessions2[position]);
        viewLayer = 1;
        listViewWords.setAdapter(listViewAdapter2);
        listViewWords.setVisibility(View.VISIBLE);
        listViewLog.setVisibility(View.GONE);
    }

    public void navigateBack(){
        if (viewLayer == 1){
            viewLayer = 0;
            listViewWords.setVisibility(View.GONE);
            listViewLog.setVisibility(View.VISIBLE);
        }
        //listViewWords.setVisibility(View.VISIBLE);
        //listViewLog.setVisibility(View.GONE);
    }


}
