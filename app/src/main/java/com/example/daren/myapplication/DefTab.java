package com.example.daren.myapplication;


import android.app.Activity;
import android.app.Fragment;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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
    protected FragmentActivity mActivity;


    public String query = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        ((AppCompatActivity)mActivity).getSupportActionBar().setTitle("Lookup Definition");
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
        Downloader downloader = new Downloader(query, textView);
        downloader.execute();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

}
