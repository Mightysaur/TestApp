package com.example.daren.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by nadim on 19/02/18.
 */

public class Downloader extends AsyncTask<Object, String, Integer> {

    private URL xmlURL;
    private String returnedText;
    private TextView textView;
    private String query;

    public static final String API_KEY = "b184931b-4583-43c8-9ea9-baac48d5e3f8";
    public static final String SERVER = "https://www.dictionaryapi.com/api/references/medical/v2/xml/";

    public Downloader(String query, TextView textView) {
        this.query = query;
        this.textView = textView;
        try {
            this.xmlURL = new URL(SERVER + query.trim() + "?key=" + API_KEY);
        } catch (MalformedURLException e) {
            System.out.println("URL not found");
        }
        this.returnedText = "";
    }



        @Override
        //Creates a new XMLPP in a background thread to download the XML
        protected Integer doInBackground(Object... arg0) {
            XmlPullParser receivedData = tryDownloadingXmlData();
            int recordsFound = tryParsingXmlData(receivedData);
            return recordsFound;
        }

        //Sets the received data to what is returned by the page
        private XmlPullParser tryDownloadingXmlData() {
            try {
                Log.i(TAG, "Now downloading...");
                XmlPullParser receivedData = XmlPullParserFactory.newInstance().newPullParser();
                receivedData.setInput(xmlURL.openStream(), null);
                return receivedData;
            } catch (XmlPullParserException e) {
                Log.e(TAG, "XmlPullParserExecption", e);
            } catch (IOException e) {
                Log.e(TAG, "XmlPullParserExecption", e);
            }
            return null;
        }

        //Creates an XMLParser that returns the data that it was configured to return (can be modified from the XML Parser class)
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
            returnedText = entry;
            textView.setText(returnedText);
            super.onProgressUpdate(values);
        }
}
