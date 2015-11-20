package com.duokeyboard.duokeyboard;

public class WordModel {
    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setFamiliar_sentence(String familiar_sentence) {
        this.familiar_sentence = familiar_sentence;
    }

    public void setUnknown_sentence(String unknown_sentence) {
        this.unknown_sentence = unknown_sentence;
    }

    public String word;

    public String getMeaning() {
        return meaning;
    }

    public String meaning;

    public String getFamiliar_sentence() {
        return familiar_sentence;
    }

    public String familiar_sentence;

    public String getUnknown_sentence() {
        return unknown_sentence;
    }

    public String unknown_sentence;

    public WordModel()
    {}

    public WordModel(String word, String meaning, String familiar_sentence, String unknown_sentence)
    {
        this.word = word;
        this.meaning = meaning;
        this.familiar_sentence = familiar_sentence;
        this.unknown_sentence = unknown_sentence;
    }
    {}

    public String getWord()
    {
        return word;
    }

}