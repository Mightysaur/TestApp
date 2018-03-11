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

public class DeleteLog extends Fragment{

    View myView;
    private Context mContext;
    private ListView listViewLogDelete;
    private ListView listViewWords;
    private ListView listViewDefinition;
    private int viewLayer;
    private FloatingActionButton backButton;
    private ArrayList<String> sessions3 = new ArrayList<String>();
    private ArrayList<ArrayList<String>> sessionWords = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> sessionDef = new ArrayList<ArrayList<String>>();
    protected FragmentActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //mActivity.getActionBar().setTitle("Hello world App");
        ((AppCompatActivity)mActivity).getSupportActionBar().setTitle("Delete Session");
        mContext = this.getActivity();
        myView = inflater.inflate(R.layout.delete_log,container,false);
        listViewLogDelete = (ListView)myView.findViewById(R.id.logListViewDelete);

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



        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,sessions3 );

        listViewLogDelete.setAdapter(listViewAdapter);

        listViewLogDelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete this session");
                final int positionToRemove = position;
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
                            fis = mActivity.openFileInput("sessions.xml");
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

                        Node removedSession = document.getElementsByTagName("name").item(positionToRemove);

                        document.getDocumentElement().removeChild(removedSession);





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
                            result = new StreamResult(mActivity.openFileOutput("sessions.xml", Context.MODE_PRIVATE));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            transformer.transform(source, result);

                        } catch (TransformerException e) {
                            Toast.makeText(mActivity.getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new DeleteLog()).commit();

                    }});
                adb.show();


            }
        });



        viewLayer = 0;




        return myView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }


}
