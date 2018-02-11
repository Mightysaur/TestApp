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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;

public class DefTab extends Fragment implements View.OnClickListener{

    View myView;
    TextView textView;
    EditText editText;
    private static final String TAG = "DictionaryApp";

    public static final String API_KEY = "b184931b-4583-43c8-9ea9-baac48d5e3f8";
    public static final String SERVER = "https://www.dictionaryapi.com/api/references/medical/v2/xml/";
    private String query = "emphysema";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //int layoutTemp = R.layout.fragment_definition_lookup_tab;
        myView = inflater.inflate(R.layout.fragment_def_tab, container, false);
        textView = (TextView) myView.findViewById(R.id.netResult);
        editText = (EditText) myView.findViewById(R.id.editText);

        Button b = (Button) myView.findViewById(R.id.getXML);
        b.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick_GetXML: ");
        textView.setText("");
        query = editText.getText().toString();
        AsyncDownloader downloader = new AsyncDownloader();
        downloader.execute();
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
                URL xmlUrl = new URL(SERVER + query + "?key=" + API_KEY);
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
                    XMLParser parser = new XMLParser(receivedData);
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
