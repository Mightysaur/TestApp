package com.example.daren.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.*;

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
    private ArrayList<String> sessions3 = new ArrayList<String>();
    private ArrayList<ArrayList<String>> sessionWords = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> sessionDef = new ArrayList<ArrayList<String>>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = this.getActivity();
        myView = inflater.inflate(R.layout.log,container,false);
        listViewLog = (ListView)myView.findViewById(R.id.logListView);
        listViewWords = (ListView)myView.findViewById(R.id.wordsListView);
        listViewDefinition = (ListView)myView.findViewById(R.id.definitionListView);
        backButton = myView.findViewById(R.id.logBackButton);


        File path = getActivity().getFilesDir();

        File file = new File(path, "sessions.xml");
        FileInputStream inputStream = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        FileInputStream fis;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            fis = getActivity().openFileInput("sessions.xml");
            try {
                document = documentBuilder.parse(fis);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        Node root = document.getDocumentElement();
        NodeList sessionsNodeList = root.getChildNodes();

        if(sessionsNodeList != null){
            int lengthSessions = sessionsNodeList.getLength();
            for (int i=0; i<lengthSessions;i++){
                Node name = sessionsNodeList.item(i);

                NodeList words = name.getChildNodes();
                int lengthWords = words.getLength();
                ArrayList<String> tempWordHolder = new ArrayList<String>();
                ArrayList<String> tempDefHolder = new ArrayList<String>();
                sessions3.add(words.item(0).getTextContent());
                for(int j = 1; j<lengthWords;j++){
                    String arr[] = words.item(j).getTextContent().split(" ", 2);
                    tempWordHolder.add(arr[0]);
                    tempDefHolder.add(arr[1]);
                }
                sessionWords.add(tempWordHolder);
                sessionDef.add(tempDefHolder);
            }



        }else{
            sessions3.add("No Sessions");
        }

        //Element sessionNames = (Element) sessionsNodeList.item(0);


        //Toast.makeText(getActivity().getApplicationContext(), test,Toast.LENGTH_SHORT).show();

        /*
        try {
            inputStream = new FileInputStream(file);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;

                while ((line = br.readLine()) != null) {
                    sessions3.add(line);

                }
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
        */

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

        listViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
                navigateToDef(position);
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
        ArrayAdapter<String> listViewAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1, sessionWords.get(position));
        viewLayer = 1;
        listViewWords.setAdapter(listViewAdapter2);
        listViewWords.setVisibility(View.VISIBLE);
        listViewLog.setVisibility(View.GONE);
    }

    public void navigateToDef(int position){
        ArrayAdapter<String> listViewAdapter3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1, sessionDef.get(position));
        viewLayer = 2;
        listViewDefinition.setAdapter(listViewAdapter3);
        listViewDefinition.setVisibility(View.VISIBLE);
        listViewWords.setVisibility(View.GONE);
    }

    public void navigateBack(){
        if (viewLayer == 1){
            viewLayer = 0;
            listViewWords.setVisibility(View.GONE);
            listViewLog.setVisibility(View.VISIBLE);
        }else if(viewLayer == 2){
            viewLayer = 1;
            listViewDefinition.setVisibility(View.GONE);
            listViewWords.setVisibility(View.VISIBLE);
        }
        //listViewWords.setVisibility(View.VISIBLE);
        //listViewLog.setVisibility(View.GONE);
    }




}
