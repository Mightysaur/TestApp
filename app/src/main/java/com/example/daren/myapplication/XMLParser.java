package com.example.daren.myapplication;

/**
 * Created by nadim on 24/01/18.
 */



import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;


public class XMLParser {

    private XmlPullParser receivedData;
    private String word;

    public ArrayList<String> getBlacklist() {
        loadBlacklistWords();
        return blacklist;
    }

    private ArrayList<String> blacklist = new ArrayList<>();

    private void loadBlacklistWords() {
        String blacklistStr = "Me You Him Her House Door Dog Cat Animal If in my name the then else home car seat I hi is no yes diagnosed" +
                              " medicine medical doctor test treatment pharmacy hospital emergency service services" +
                              " feeling very ill will prescribe with to come back";
        String[] blacklistArr = blacklistStr.split(" ");
        for(String str:blacklistArr){
            blacklist.add(str.toLowerCase());
        }
    }

    public XMLParser(){

    }
    public XMLParser(XmlPullParser receivedData, String query) {
        this.receivedData = receivedData;
        this.word = query;
    }

    private String firstDefinitionFrom(XmlPullParser xmlData) throws XmlPullParserException, IOException {
        int recordsFound = 0;

        loadBlacklistWords();

        String definition = "";


        //First Checks if the word is in the blacklist
        if(blacklist.contains(word.toLowerCase())){
            return word + " No definition found";
        }

        int eventType = -1;
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            String tagName = xmlData.getName();

            switch (eventType) {

                case XmlPullParser.START_TAG:

                    //Retrieves the first definition only, ignoring tags in between
                    if(tagName.equals("dt") && recordsFound < 1 ){
                        eventType = xmlData.next();

                        while(!((eventType == XmlPullParser.END_TAG) && (tagName.equals("dt")))){
                            if ((eventType == XmlPullParser.START_TAG) || (eventType == XmlPullParser.END_TAG)){
                                tagName = xmlData.getName();
                            }
                            if(eventType == XmlPullParser.TEXT){
                                definition += xmlData.getText();
                            }
                            eventType = xmlData.next();
                        }
                        recordsFound ++;
                    }
                    break;

                case XmlPullParser.TEXT:
                    break;

                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xmlData.next();
        }

        if(definition == ""){
            return word + " No definition found";
        }

        return word + " " + definition;
    }

    public String getFirstDefinition() throws IOException, XmlPullParserException {
        return firstDefinitionFrom(receivedData);
    }
}
