package com.kimjunhong.bucketlist.model;

import android.util.Log;

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

    // CREATE Bucket
    public static void create(Realm realm, String title) {
        BucketList bucketList = realm.where(BucketList.class).findFirst();
        RealmList<Bucket> buckets = bucketList.getBucketList();

        // Primary Key Auto Increment
        Number currentId = realm.where(Bucket.class).max("id");
        // 현재 sequence 최대 값 찾아서, 다음 sequence 값 결정
        Number currentSequence = realm.where(Bucket.class).max("sequence");
        int nextId;
        int nextSequence;

        if(currentId == null && currentSequence == null) {
            // List가 비어있을 때
            nextId = 1;
            nextSequence = 1;
        } else {
            // List가 비어있지 않을 때
            nextId = currentId.intValue() + 1;
            nextSequence = currentSequence.intValue() + 1;
        }

        // 두번째 인자로 Primary Key Value
        Bucket bucket = realm.createObject(Bucket.class, nextId);

        // 데이터 추가
        bucket.setDate(new Date(System.currentTimeMillis()));
        bucket.setSequence(nextSequence);
        bucket.setTitle(title);

        // BucketList에 Bucket 추가
        buckets.add(bucket);
    }

    // DELETE Bucket
    public static void delete(Realm realm, int position) {
        Bucket bucket = realm.where(Bucket.class) .equalTo("sequence", position).findFirst();

        bucket.deleteFromRealm();
        Log.v("log", "deleteBucket position : " + position);
    }

    // SWAP Bucket
    public static void swap(Realm realm, int fromPosition, int toPosition) {
        // position에 해당하는 버킷을 가져옴, sequence 값은 항상 position + 1
        Bucket fromBucket = realm.where(Bucket.class).equalTo("sequence", fromPosition + 1).findFirst();
        Bucket toBucket = realm.where(Bucket.class).equalTo("sequence", toPosition + 1).findFirst();

        // 버킷 스왑
        fromBucket.setSequence(toPosition + 1);
        toBucket.setSequence(fromPosition + 1);
    }
}