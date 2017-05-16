package com.kimjunhong.bucketlist.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by INMA on 2017. 5. 16..
 */

public class BucketList extends RealmObject {
    @SuppressWarnings("unused")
    private RealmList<Bucket> bucketList;

    public RealmList<Bucket> getBucketList() {
        return bucketList;
    }
}
