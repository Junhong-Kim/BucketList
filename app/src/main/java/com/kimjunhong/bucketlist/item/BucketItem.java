package com.kimjunhong.bucketlist.item;

/**
 * Created by INMA on 2017. 5. 12..
 */

public class BucketItem {
    String title;
    String date;

    public BucketItem(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
