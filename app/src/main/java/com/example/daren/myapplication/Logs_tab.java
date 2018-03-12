package com.example.daren.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Daren on 06/12/2017.
 */

public class Logs_tab extends Fragment{

    View myView;
    private Context mContext;
    private ListView listViewLog;
    private ListView listViewWords;
    private TextView listDefinition;
    private int viewLayer;
    private int currentSessionPos;
    private int currentWordPos;
    private FloatingActionButton backButton;
    private FloatingActionButton favouriteWord;
    private ArrayList<String> sessions3 = new ArrayList<String>();
    private ArrayList<ArrayList<String>> sessionWords = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> sessionDef = new ArrayList<ArrayList<String>>();
    private String[] sessonsTest = {"1","2","3"};
    protected FragmentActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity)mActivity).getSupportActionBar().setTitle("Sessions");
        mContext = this.mActivity;
        myView = inflater.inflate(R.layout.log,container,false);
        listViewLog = (ListView)myView.findViewById(R.id.logListView);
        listViewWords = (ListView)myView.findViewById(R.id.wordsListView);
        listDefinition = myView.findViewById(R.id.wordsListDef);
        backButton = myView.findViewById(R.id.logBackButton);
        favouriteWord = myView.findViewById(R.id.favouriteWord);


        File path = mActivity.getFilesDir();

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
            fis = mActivity.openFileInput("sessions.xml");
            try {
                document = documentBuilder.parse(fis);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(mActivity.getApplicationContext(), getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
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



        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_expandable_list_item_1,sessions3 );

        listViewLog.setAdapter(listViewAdapter);


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


        favouriteWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Favourite?");
                adb.setMessage("Are you sure you want to favourite this word?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
                            fis = getActivity().openFileInput("favourites.xml");
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

                        Element root = document.getDocumentElement();


                        Element favWord = document.createElement("word");
                        favWord.appendChild(document.createTextNode(sessionWords.get(currentSessionPos).get(currentWordPos) + " "+ sessionDef.get(currentSessionPos).get(currentWordPos)));




                        root.appendChild(favWord);

                        DOMSource source = new DOMSource(document);

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = null;
                        try {
                            transformer = transformerFactory.newTransformer();
                        } catch (TransformerConfigurationException e) {
                            e.printStackTrace();
                        }

                        StreamResult result = null;
                        try {
                            result = new StreamResult(getActivity().openFileOutput("favourites.xml", Context.MODE_PRIVATE));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            transformer.transform(source, result);

                        } catch (TransformerException e) {
                            Toast.makeText(getActivity().getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                });
                adb.show();
            }
        });




        return myView;
    }

    public void navigateToWords(int position){
        ArrayAdapter<String> listViewAdapter2 = new ArrayAdapter<String>(mActivity,android.R.layout.simple_expandable_list_item_1, sessionWords.get(position));
        currentSessionPos = position;
        viewLayer = 1;
        listViewWords.setAdapter(listViewAdapter2);
        listViewWords.setVisibility(View.VISIBLE);
        listViewLog.setVisibility(View.GONE);
    }

    public void navigateToDef(int position){
        currentWordPos = position;
        listDefinition.setText(sessionDef.get(currentSessionPos).get(position));
        viewLayer = 2;
        listDefinition.setVisibility(View.VISIBLE);
        favouriteWord.setVisibility(View.VISIBLE);
        listViewWords.setVisibility(View.GONE);

    }

    public void navigateBack(){
        if (viewLayer == 1){
            viewLayer = 0;
            listViewWords.setVisibility(View.GONE);
            listViewLog.setVisibility(View.VISIBLE);
        }else if(viewLayer == 2){
            viewLayer = 1;
            listDefinition.setVisibility(View.GONE);
            favouriteWord.setVisibility(View.GONE);
            listViewWords.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

}
