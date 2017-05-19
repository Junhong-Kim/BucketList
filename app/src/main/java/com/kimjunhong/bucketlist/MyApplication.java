package com.kimjunhong.bucketlist;

import android.app.Application;

import com.kimjunhong.bucketlist.model.BucketList;
import com.kimjunhong.bucketlist.model.CompletedBucketList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by INMA on 2017. 5. 16..
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // Realm 초기화
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.createObject(BucketList.class);
                        realm.createObject(CompletedBucketList.class);
                    }})
                .build();

        Realm.setDefaultConfiguration(realmConfig);
    }
}
