package com.kimjunhong.bucketlist.model;

import com.kimjunhong.bucketlist.R;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by INMA on 2017. 5. 18..
 */

public class CompletedBucket extends RealmObject {
    @PrimaryKey
    private int id;
    private int sequence;
    private String title;
    private Date date;
    private String location;
    private String with;
    private String memo;
    private int picture;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    // CREATE Completed Bucket
    public static void create(Realm realm, String title) {
        CompletedBucketList completedBucketList = realm.where(CompletedBucketList.class).findFirst();
        RealmList<CompletedBucket> completedBuckets = completedBucketList.getCompletedBucketList();

        // Primary Key Auto Increment
        Number currentId = realm.where(CompletedBucket.class).max("id");
        // 현재 sequence 최대 값 찾아서, 다음 sequence 값 결정
        Number currentSequence = realm.where(CompletedBucket.class).max("sequence");
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
        CompletedBucket completedBucket = realm.createObject(CompletedBucket.class, nextId);

        // 데이터 추가
        completedBucket.setSequence(nextSequence);
        completedBucket.setTitle(title);
        completedBucket.setDate(new Date(System.currentTimeMillis()));
        completedBucket.setLocation("어딘가");
        completedBucket.setWith("친구");
        completedBucket.setMemo(null);
        completedBucket.setPicture(R.drawable.icon_picture);

        // CompletedBucketList에 CompletedBucket 추가
        completedBuckets.add(completedBucket);
    }

    // DELETE Completed Bucket
    public static void delete(Realm realm, int position) {
        findOne(realm, position).deleteFromRealm();
    }

    // FIND Bucket
    private static CompletedBucket findOne(Realm realm, int position) {
        return realm.where(CompletedBucket.class).equalTo("sequence", position).findFirst();
    }
}
