package com.pratmodi.main.model;

import java.io.Serializable;

public class TweetBasicModel  implements Serializable {

    private String text;
    private String id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TweetBasicModel{" +
                "text='" + text + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
