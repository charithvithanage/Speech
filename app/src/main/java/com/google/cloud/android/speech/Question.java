package com.google.cloud.android.speech;

import java.util.List;

public class Question {
    private Long id;

    private String question;
    private String answer;
    private String source;

    private List<String> keywords;

    private String audioString;

    public String getAudioString() {
        return audioString;
    }

    public void setAudioString(String audioString) {
        this.audioString = audioString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
