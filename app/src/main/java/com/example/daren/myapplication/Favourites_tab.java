package com.example.daren.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Daren on 06/12/2017.
 */

public class Favourites_tab extends Fragment{

    View myView;
    private Context mContext;
    private ListView favListView;
    private TextView listDefinition;
    private int viewLayer;
    private FloatingActionButton backButton;
    private FloatingActionButton deleteFavWord;
    private int selectedWord;
    private ArrayList<String> FavWords = new ArrayList<String>();
    private ArrayList<String> FavDef = new ArrayList<String>();
    protected FragmentActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.favourites_tab,container,false);

        ((AppCompatActivity)mActivity).getSupportActionBar().setTitle("Favourites");
        mContext = this.mActivity;
        favListView = myView.findViewById(R.id.favListView);
        listDefinition = myView.findViewById(R.id.favListViewDef);
        backButton = myView.findViewById(R.id.favBackButton);
        deleteFavWord = myView.findViewById(R.id.deleteFavButton);

        File path = mActivity.getFilesDir();

        File file = new File(path, "favourites.xml");
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
            fis = mActivity.openFileInput("favourites.xml");
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
                Node words = sessionsNodeList.item(i);

                ArrayList<String> tempWordHolder = new ArrayList<String>();
                ArrayList<String> tempDefHolder = new ArrayList<String>();


                String arr[] = words.getTextContent().split(" ", 2);
                FavWords.add(arr[0]);
                FavDef.add(arr[1]);


            }



        }



        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_expandable_list_item_1,FavWords);

        favListView.setAdapter(listViewAdapter);

        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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

        deleteFavWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete this favourited word?");
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
                            //Toast.makeText(getActivity().getApplicationContext(), getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
                            fis = mActivity.openFileInput("favourites.xml");
                            try {
                                document = documentBuilder.parse(fis);
                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Element root = document.getDocumentElement();

                        Node removedWord = document.getElementsByTagName("word").item(selectedWord);

                        document.getDocumentElement().removeChild(removedWord);


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
                            result = new StreamResult(mActivity.openFileOutput("favourites.xml", Context.MODE_PRIVATE));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            transformer.transform(source, result);

                        } catch (TransformerException e) {
                            Toast.makeText(mActivity.getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new Favourites_tab()).commit();

                    }
                });
                adb.show();
            }
        });

        return myView;
    }

    private void navigateToDef(int position) {
        viewLayer = 1;
        selectedWord = position;
        listDefinition.setText(FavDef.get(position));
        favListView.setVisibility(View.GONE);
        listDefinition.setVisibility(View.VISIBLE);
        deleteFavWord.setVisibility(View.VISIBLE);

    }

    public void navigateBack(){
        if (viewLayer == 1){
            viewLayer = 0;
            listDefinition.setVisibility(View.GONE);
            favListView.setVisibility(View.VISIBLE);
            deleteFavWord.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

}
