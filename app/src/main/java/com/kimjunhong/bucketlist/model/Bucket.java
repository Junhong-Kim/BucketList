package com.kimjunhong.bucketlist.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by INMA on 2017. 5. 16..
 */

public class Bucket extends RealmObject {

    @PrimaryKey
    private int id;
    private int sequence;
    private String title;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Create Bucket
    public static void create(Realm realm, String title) {
        BucketList bucketList = realm.where(BucketList.class).findFirst();
        RealmList<Bucket> buckets = bucketList.getBucketList();

        // Primary Key Auto Increment
        Number currentId = realm.where(Bucket.class).max("id");
        int nextId;

        if(currentId == null) {
            // List가 비어있을 때
            nextId = 1;
        } else {
            // List가 비어있지 않을 때
            nextId = currentId.intValue() + 1;
        }

        // 두번째 인자로 Primary Key Value
        Bucket bucket = realm.createObject(Bucket.class, nextId);

        // 데이터 추가
        bucket.setDate(new Date(System.currentTimeMillis()));
        bucket.setSequence(nextId);
        bucket.setTitle(title);

        // BucketList에 Bucket 추가
        buckets.add(bucket);
    }
}
