package com.example.daren.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;

import android.content.Context;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.text.method.ScrollingMovementMethod;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ScrollView;
import android.widget.TextView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import android.content.Intent;



import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.util.Log;

import android.widget.CompoundButton;
import android.widget.ProgressBar;

import android.widget.Toast;
import android.widget.ToggleButton;


import android.widget.Button;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 * Created by Daren on 06/12/2017.
 */

public class VoiceRecognitionActivity extends Fragment implements RecognitionListener {

    View myView;
    private static final String TAG = "VoiceRecognition";
    private boolean isBound;
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private Button showDefinition;
    private Button nextDefButton;
    private FloatingActionButton endSession;
    private FloatingActionButton sessionBackLog;
    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private ScrollView scroller;
    private TextView backLogText;
    private String sessionBackLogText;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    String speechString = "";
    boolean speechStarted = false;
    boolean scrollershown = false;
    private Date currentTime;
    private BluetoothConnectionService bluetoothConnectionService;

    public static final String API_KEY = "b184931b-4583-43c8-9ea9-baac48d5e3f8";
    public static final String SERVER = "https://www.dictionaryapi.com/api/references/medical/v2/xml/";
    public String query = "";

    private ArrayList<Word[]> spokenMedicalTerms = new ArrayList<>();
    private ArrayList<Word> spokenWordsContainer = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.new_session, container, false);
        ((MainActivity)getActivity()).requestRecordAudioPermission();
        /*super.onCreate(savedInstanceState);
        txtSpeechInput = (TextView) myView.findViewById(R.id.txtSpeechInput);
        btnSpeak = (Button) myView.findViewById(R.id.myButton);
        getActivity().registerReceiver(this.receiver, new IntentFilter("FILTER"));
        Intent intent = new Intent(this.getActivity(), MyService.class);
        getActivity().startService(intent);

        isBound = false;*/


        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        returnedText =  myView.findViewById(R.id.textView1);
        progressBar = myView.findViewById(R.id.progressBar1);
        toggleButton =  myView.findViewById(R.id.toggleButton1);
        showDefinition = myView.findViewById(R.id.showDefinition);
        sessionBackLog = myView.findViewById(R.id.sessionBacklog);
        nextDefButton = myView.findViewById((R.id.nextDefinition));
        endSession = myView.findViewById(R.id.endSession);
        scroller = myView.findViewById(R.id.backLogScroller);
        backLogText = myView.findViewById(R.id.backLogText);
        backLogText.setMovementMethod(new ScrollingMovementMethod());
        returnedText.setMovementMethod(new ScrollingMovementMethod());

        bluetoothConnectionService = ((MainActivity)getActivity()).getMainBluetoothConnection();

        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    speech.setRecognitionListener(VoiceRecognitionActivity.this);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    speech.startListening(recognizerIntent);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                    speech.destroy();

                }
            }
        });

        File path = getActivity().getFilesDir();
        currentTime = Calendar.getInstance().getTime();

        File file = new File(path, "sessions.txt");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // write code for saving data to the file
        }



        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Element root = document.getDocumentElement();

                //Element newSession = document.createElement("session");

                Element sessionName = document.createElement("name");
                sessionName.appendChild(document.createTextNode(currentTime.toString()));
                //newSession.appendChild(sessionName);

                for(Word addWord : spokenWordsContainer){

                    Element word = document.createElement("word");
                    word.appendChild(document.createTextNode(addWord.getWord() + " "+ addWord.getDefinition()));
                    sessionName.appendChild(word);


                }

                root.appendChild(sessionName);

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
                    result = new StreamResult(getActivity().openFileOutput("sessions.xml", Context.MODE_PRIVATE));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    transformer.transform(source, result);

                } catch (TransformerException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new presession()).commit();
            }
        });

        showDefinition.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(!spokenMedicalTerms.isEmpty()){
                    spokenMedicalTerms.get(0)[1] = new Word("1","1");
                    returnedText.setText(spokenMedicalTerms.get(0)[0].getDefinition());
                    if(bluetoothConnectionService != null) {
                        bluetoothConnectionService.write(("def:" + spokenMedicalTerms.get(0)[0].getDefinition()).getBytes());
                    }
                }

            }
        });

        nextDefButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(!spokenMedicalTerms.isEmpty()){

                    if (spokenMedicalTerms.get(0)[1].getWord() == "1"){
                        sessionBackLogText = sessionBackLogText + (spokenMedicalTerms.get(0)[0].getWord() + " - " + spokenMedicalTerms.get(0)[0].getDefinition()+"\n\n");
                        backLogText.setText(sessionBackLogText);
                        spokenWordsContainer.add(spokenMedicalTerms.get(0)[0]);
                    }
                    spokenMedicalTerms.remove(0);

                }
                if(!spokenMedicalTerms.isEmpty()){
                    returnedText.setText(spokenMedicalTerms.get(0)[0].getWord());
                    if(bluetoothConnectionService != null) {
                        bluetoothConnectionService.write(("word:" + spokenMedicalTerms.get(0)[0].getWord()).getBytes());
                        //bluetoothConnectionService.write((spokenMedicalTerms.get(0).getWord() + " - " + spokenMedicalTerms.get(0).getDefinition()).getBytes());
                    }
                }
                else returnedText.setText("");
            }
        });

        scrollershown = false;
        sessionBackLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scrollershown == false){

                    scroller.setVisibility(View.VISIBLE);
                    scrollershown = true;
                    showDefinition.setVisibility(View.GONE);
                    nextDefButton.setVisibility(View.GONE);
                    toggleButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    returnedText.setVisibility(View.GONE);

                }else{
                    scroller.setVisibility(View.GONE);
                    scrollershown = false;
                    showDefinition.setVisibility(View.VISIBLE);
                    nextDefButton.setVisibility(View.VISIBLE);
                    toggleButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    returnedText.setVisibility(View.VISIBLE);
                }

            }
        });

        sessionBackLogText = "";

    }

    @Override
    public void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        speechStarted = true;
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {

        speechStarted = false;
        Log.i(LOG_TAG, "onEndOfSpeech");
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onError(int errorCode) {
        Log.d(LOG_TAG, "FAILED ");
        if (!speechStarted)
            speech.startListening(recognizerIntent);

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");

        ArrayList<String> matches = arg0
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

       // returnedText.setText(speechString + matches.get(0));


    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        speechString = speechString + ". " + matches.get(0);
        Downloader downloader = new Downloader(matches.get(0));
        downloader.execute();
    }


    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();
            String updatedText = intent.getExtras().get("MY_KEY").toString();
            //txtSpeechInput.setText("hello");
        }
    };


    private void handleNewRecord(String entry) {
       // String message = returnedText.getText().toString() + "\n" + entry;
       // returnedText.setText(message);

        String arr[] = entry.split(" ", 2);

        //If the XML API returns no definition, don't add the word, else add it
        if(!(arr[1].equals("No definition found"))){
            Log.i(TAG, "Medical Term" + arr[1]);
            Word[] tempWord = {(new Word(arr[0], arr[1])),(new Word("0","0"))};
            spokenMedicalTerms.add(tempWord);
            if(spokenMedicalTerms.size() == 1) {
                returnedText.setText(spokenMedicalTerms.get(0)[0].getWord());
                if(bluetoothConnectionService != null) {
                    bluetoothConnectionService.write(("word:" + spokenMedicalTerms.get(0)[0].getWord()).getBytes());
                    //bluetoothConnectionService.write((spokenMedicalTerms.get(0).getWord() + " - " + spokenMedicalTerms.get(0).getDefinition()).getBytes());
                }
            }

        }
    }



    private class Downloader extends AsyncTask<Object, String, Integer> {

        private String[] receivedWords;

        public Downloader(String receivedString) {
            receivedWords = receivedString.split(" ");
        }

        @Override
        protected Integer doInBackground(Object... arg0) {
            int recordsFound = 0;
            for(String word:receivedWords) {
                Log.i(TAG, "doInBackground: "+ word);
                query = word;
                XmlPullParser receivedData = tryDownloadingXmlData();
                recordsFound = tryParsingXmlData(receivedData);
            }
            return recordsFound;
        }

        private XmlPullParser tryDownloadingXmlData() {
            try {
                Log.i(TAG, "Now downloading...");
                URL xmlUrl = new URL(SERVER + query.trim() + "?key=" + API_KEY);
                XmlPullParser receivedData = XmlPullParserFactory.newInstance().newPullParser();
                receivedData.setInput(xmlUrl.openStream(), null);
                return receivedData;
            } catch (XmlPullParserException e) {
                Log.e(TAG, "XmlPullParserExecption", e);
            } catch (IOException e) {
                Log.e(TAG, "XmlPullParserExecption", e);
            }
            return null;
        }

        private int tryParsingXmlData(XmlPullParser receivedData) {
            if (receivedData != null) {
                try {
                    XMLParser parser = new XMLParser(receivedData, query);
                    String definition = parser.getFirstDefinition();
                    publishProgress(definition);
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "Pull Parser failure", e);
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception parsing XML", e);
                }
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String entry = values[0];
            Log.i(TAG, "Word requested: " + entry);


            // Pass it to the application
            handleNewRecord(entry);

            super.onProgressUpdate(values);
        }
    }



}
