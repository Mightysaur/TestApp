package com.example.daren.myapplication;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nadim on 19/02/18.
 */

public class Session {
    private ArrayList<Word> wordsSearched = new ArrayList();
    private Date sessionDate;

    public Session(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public ArrayList<Word> getWordsSearched() {
        return wordsSearched;
    }

    public Date getSessionDate() {
        return sessionDate;
    }
}
