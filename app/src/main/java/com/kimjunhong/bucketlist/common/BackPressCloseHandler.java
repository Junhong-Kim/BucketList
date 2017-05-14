package com.kimjunhong.bucketlist.common;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by INMA on 2017. 5. 14..
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, "한번 더 누르면 종료됩니다 :D", Toast.LENGTH_SHORT).show();
            return;
        }

        if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
