package com.smsanalytic.lotto.model;

public class WordReplaceEntity {
    private String word;
    private String wordDisplay;

    public WordReplaceEntity(String word, String wordDisplay) {
        this.word = word;
        this.wordDisplay = wordDisplay;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordDisplay() {
        return wordDisplay;
    }

    public void setWordDisplay(String wordDisplay) {
        this.wordDisplay = wordDisplay;
    }
}
