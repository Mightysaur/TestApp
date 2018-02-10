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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = this.getActivity();
        myView = inflater.inflate(R.layout.log,container,false);
        listViewLog = (ListView)myView.findViewById(R.id.logListView);
        listViewWords = (ListView)myView.findViewById(R.id.wordsListView);
        listViewDefinition = (ListView)myView.findViewById(R.id.definitionListView);
        backButton = myView.findViewById(R.id.logBackButton);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,sessions );

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
