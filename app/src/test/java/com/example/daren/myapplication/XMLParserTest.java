package com.example.daren.myapplication;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class XMLParserTest {


    @Test
    public void BlacklistContainsTheWordI(){
        //create a void parser
        XMLParser parser = new XMLParser();

        ArrayList<String> words = parser.getBlacklist();
        assertTrue(words.contains("i"));
    }

    @Test
    public void BlacklistDoesntContainTheWordEmphysema(){
        //create a void parser
        XMLParser parser = new XMLParser();

        ArrayList<String> words = parser.getBlacklist();
        assertFalse(words.contains("emphysema"));
    }

}

