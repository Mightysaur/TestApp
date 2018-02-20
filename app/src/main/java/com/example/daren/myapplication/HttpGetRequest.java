package com.example.daren.myapplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by nadim on 23/01/18.
 */

public class HttpGetRequest {

    public String getPage(String webpage){
        URL url = null;
        try {
            url = new URL(webpage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        }catch (IOException e){
            System.out.println("IOException");
        }
        finally {
            urlConnection.disconnect();
        }
        return "Webpage not found";
    }

    private String readStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";

        return result;
    }
}