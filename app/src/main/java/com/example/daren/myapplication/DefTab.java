package com.example.daren.myapplication;


import android.app.Fragment;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;

public class DefTab extends Fragment {

    View myView;
    TextView textView;
    private static final String TAG = "DictionaryApp";

    public static final String API_KEY = "b184931b-4583-43c8-9ea9-baac48d5e3f8";
    public static final String SERVER = "https://www.dictionaryapi.com/api/references/medical/v2/xml/";
    public static final String QUERY = "doctor";
    public static final String QUERY_URL = SERVER + QUERY + "?key=" + API_KEY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //int layoutTemp = R.layout.fragment_definition_lookup_tab;
        myView = inflater.inflate(R.layout.fragment_def_tab, container, false);
        textView = (TextView) myView.findViewById(R.id.netResult);

        return myView;
    }

    public void onClick_GetXML(View v){
        Log.i(TAG, "onClick_GetXML: ");
        AsyncDownloader downloader = new AsyncDownloader();
        downloader.execute();

    }

    private void handleNewRecord(String entry) {
        String message = textView.getText().toString() + "\n" + entry;
        textView.setText(message);
    }


    private class AsyncDownloader extends AsyncTask<Object, String, Integer> {

        @Override
        protected Integer doInBackground(Object... arg0) {
            XmlPullParser receivedData = tryDownloadingXmlData();
            int recordsFound = tryParsingXmlData(receivedData);
            return recordsFound;
        }

        private XmlPullParser tryDownloadingXmlData() {
            try {
                Log.i(TAG, "Now downloading...");
                URL xmlUrl = new URL(QUERY_URL);
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
                    return processReceivedData(receivedData);
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "Pull Parser failure", e);
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception parsing XML", e);
                }
            }
            return 0;
        }

        private int processReceivedData(XmlPullParser xmlData) throws XmlPullParserException, IOException {
            int recordsFound = 0;

            String definition = "";       // Text

            int eventType = -1;
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                String tagName = xmlData.getName();

                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        if(tagName.equals("dt") && recordsFound < 1 ){
                            eventType = xmlData.next();

                            while(!((eventType == XmlPullParser.END_TAG) && (tagName.equals("dt")))){
                                if ((eventType == XmlPullParser.START_TAG) || (eventType == XmlPullParser.END_TAG)){
                                    tagName = xmlData.getName();
                                }
                                if(eventType == XmlPullParser.TEXT){
                                    definition += xmlData.getText();
                                    Log.i(TAG, "processReceivedData: "+ definition);
                                }
                                eventType = xmlData.next();
                            }
                            definition += "\n";
                            recordsFound ++;
                            publishProgress(definition);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    // Grab data text (very simple processing)
                    // NOTE: This could be full XML data to process.

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlData.next();
            }

            // Handle no data available: Publish an empty event.
            if (recordsFound == 0) {
                publishProgress();
            }
            Log.i(TAG, "Finished processing "+recordsFound+" records.");
            return recordsFound;
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
