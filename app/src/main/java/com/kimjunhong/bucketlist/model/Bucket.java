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
        bucket.setSequence(nextSequence);
        bucket.setTitle(title);
        bucket.setDate(new Date(System.currentTimeMillis()));

        // BucketList에 Bucket 추가
        buckets.add(bucket);
    }

    // DELETE Bucket
    public static void delete(Realm realm, int position) {
        Log.v("log", "deleteBucket position : " + position);
        Bucket bucket = findOne(realm, position);
        bucket.deleteFromRealm();
    }

    // UPDATE Bucket
    public static void update(Realm realm, String title, int position) {
        Log.v("log", "updateBucket position : " + position);
        Bucket bucket = findOne(realm, position);
        bucket.setTitle(title);
    }

    // SWAP Bucket
    public static void swap(Realm realm, int fromPosition, int toPosition) {
        // position에 해당하는 버킷을 가져옴
        Bucket fromBucket = findOne(realm, fromPosition);
        Bucket toBucket = findOne(realm, toPosition);

        // 버킷 스왑
        fromBucket.setSequence(toPosition);
        toBucket.setSequence(fromPosition);
    }

    // COMPLETE Bucket
    public static void complete(Realm realm, int position) {
        // 완료한 버킷의 title을 CompletedBucket.create 매개변수로 넘겨준다
        Bucket bucket = findOne(realm, position);
        String completedTitle = bucket.getTitle();

        // 완료한 버킷 리스트에 항목을 추가하고 진행중 버킷 리스트에서 항목을 삭제한다
        CompletedBucket.create(realm, completedTitle);
        Bucket.delete(realm, position);
    }

    // FIND Bucket
    private static Bucket findOne(Realm realm, int position) {
        return realm.where(Bucket.class).equalTo("sequence", position).findFirst();
    }
}
