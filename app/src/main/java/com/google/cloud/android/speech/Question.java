package com.google.cloud.android.speech;

import java.util.List;

public class Question {
    private long id;

    private String tag;
    private Boolean show;
    private String question;
    private String answer;
    private String source;
    private long compositeKeywordsCount;
    private String confirmationQuestion;

    private List<Long> counterQuestionIDs;

    private List<Keyword> keywords;
    private String audioString;

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getCompositeKeywordsCount() {
        return compositeKeywordsCount;
    }

    public void setCompositeKeywordsCount(long compositeKeywordsCount) {
        this.compositeKeywordsCount = compositeKeywordsCount;
    }

    public String getConfirmationQuestion() {
        return confirmationQuestion;
    }

    public void setConfirmationQuestion(String confirmationQuestion) {
        this.confirmationQuestion = confirmationQuestion;
    }

    public List<Long> getCounterQuestionIDs() {
        return counterQuestionIDs;
    }

    public void setCounterQuestionIDs(List<Long> counterQuestionIDs) {
        this.counterQuestionIDs = counterQuestionIDs;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public String getAudioString() {
        return audioString;
    }

    public void setAudioString(String audioString) {
        this.audioString = audioString;
    }
}
