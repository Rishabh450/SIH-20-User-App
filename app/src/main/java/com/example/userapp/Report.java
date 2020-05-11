package com.example.userapp;

public class Report {

    String criminalId, timeSeen, dateSeen, placeSeen, details, location, imageURL;

    public Report(String criminalId, String timeSeen, String dateSeen, String placeSeen, String details) {
        this.criminalId = criminalId;
        this.timeSeen = timeSeen;
        this.dateSeen = dateSeen;
        this.placeSeen = placeSeen;
        this.details = details;
    }

    public Report(String criminalId, String timeSeen, String dateSeen, String placeSeen, String details, String location) {
        this.criminalId = criminalId;
        this.timeSeen = timeSeen;
        this.dateSeen = dateSeen;
        this.placeSeen = placeSeen;
        this.details = details;
        this.location = location;
    }

    public Report(String criminalId, String timeSeen, String dateSeen, String placeSeen, String details, String location, String imageURL) {
        this.criminalId = criminalId;
        this.timeSeen = timeSeen;
        this.dateSeen = dateSeen;
        this.placeSeen = placeSeen;
        this.details = details;
        this.location = location;
        this.imageURL = imageURL;
    }

    public Report() {
    }

    public String getCriminalId() {
        return criminalId;
    }

    public void setCriminalId(String criminalId) {
        this.criminalId = criminalId;
    }

    public String getTimeSeen() {
        return timeSeen;
    }

    public void setTimeSeen(String timeSeen) {
        this.timeSeen = timeSeen;
    }

    public String getDateSeen() {
        return dateSeen;
    }

    public void setDateSeen(String dateSeen) {
        this.dateSeen = dateSeen;
    }

    public String getPlaceSeen() {
        return placeSeen;
    }

    public void setPlaceSeen(String placeSeen) {
        this.placeSeen = placeSeen;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
