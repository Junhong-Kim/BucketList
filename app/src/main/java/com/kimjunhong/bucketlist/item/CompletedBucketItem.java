package com.kimjunhong.bucketlist.item;

/**
 * Created by INMA on 2017. 5. 13..
 */

public class CompletedBucketItem {
    String title;
    String with;
    String date;
    String location;
    int picture;

    public CompletedBucketItem(String title, String with, String date, String location, int picture) {
        this.title = title;
        this.with = with;
        this.date = date;
        this.location = location;
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
