package com.google.cloud.android.speech;

public class Keyword {
    private long id;

    private String keyword;
    private long points;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
