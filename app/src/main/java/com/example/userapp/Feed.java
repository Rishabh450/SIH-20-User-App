package com.example.userapp;

public class Feed {

    String content, imageURL, senderID, senderURL, time;

    public Feed() {
    }

    public Feed(String content, String imageURL, String senderID, String senderURL, String time) {
        this.content = content;
        this.imageURL = imageURL;
        this.senderID = senderID;
        this.senderURL = senderURL;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderURL() {
        return senderURL;
    }

    public void setSenderURL(String senderURL) {
        this.senderURL = senderURL;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
