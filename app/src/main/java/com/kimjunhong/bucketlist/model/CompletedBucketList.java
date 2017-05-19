package com.kimjunhong.bucketlist.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by INMA on 2017. 5. 18..
 */

public class CompletedBucketList extends RealmObject {
    @SuppressWarnings("unused")
    private RealmList<CompletedBucket> completedBucketList;

    public RealmList<CompletedBucket> getCompletedBucketList() {
        return completedBucketList;
    }
}
