package com.example.daren.myapplication;

/**
 * Created by nadim on 19/02/18.
 */

public class Word {
    private String word;
    private String definition;


    public Word(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public String getWord() {
        return word;
    }
}
