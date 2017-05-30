package com.kimjunhong.bucketlist.activity;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.adapter.TabPagerAdapter;
import com.kimjunhong.bucketlist.common.BackPressCloseHandler;
import com.kimjunhong.bucketlist.fragment.CompletedFragment;
import com.kimjunhong.bucketlist.fragment.ProcessingFragment;
import com.kimjunhong.bucketlist.model.Bucket;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.button_add) ImageView addButton;
    @BindView(R.id.speech_bubble) LinearLayout speechBubble;
    @BindView(R.id.editText_bucket) EditText newBucket;

    private BackPressCloseHandler backPressCloseHandler;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initViewPager();
        initView();
        permissionCheck();
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 기본 타이틀 제거
        this.setTitle(null);
    }

    private void initViewPager() {
        List<Fragment> listFragments = new ArrayList<>();
        listFragments.add(new ProcessingFragment());
        listFragments.add(new CompletedFragment());

        TabPagerAdapter fragmentPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(fragmentPagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("진행중 버킷 :<"));
        tabLayout.addTab(tabLayout.newTab().setText("완료한 버킷 :D"));
        tabLayout.setTabTextColors(Color.LTGRAY, Color.parseColor("#3F51B5"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                    case 1:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView() {
        // Back 버튼으로 앱 종료
        backPressCloseHandler = new BackPressCloseHandler(this);

        // Bucket 추가 버튼
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBucket();
            }
        });

        // 키보드 완료 버튼
        newBucket.setImeOptions(EditorInfo.IME_ACTION_DONE); // 완료 버튼 클릭시
        newBucket.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    createNewBucket();
                    return true;
                }
                return false;
            }
        });
    }

    private void createNewBucket() {
        if(newBucket.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "버킷을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // 말풍선 보이기
                        showSpeechBubble();
                        // Bucket 생성
                        Bucket.create(realm, String.valueOf(newBucket.getText()));
                        // EditText 초기화
                        newBucket.setText("");
                    }
                });
            } finally {
                realm.close();
            }
        }
    }

    private void showSpeechBubble() {
        // fade in Animation load
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        // fade in 효과 & 말풍선 보이기
        speechBubble.startAnimation(fadeIn);
        speechBubble.setVisibility(View.VISIBLE);

        // 2초 뒤 사라지기
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                speechBubble.startAnimation(fadeOut);
                speechBubble.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    private void permissionCheck() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "거부된 권한\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("원활한 서비스 이용을 위해\n앱의 권한이 필요합니다!\n\n- 개발자 올림 -")
                .setDeniedMessage("앗.. 권한을 거부하셨어요!\n[설정] ► [권한]에서 권한을 허용하시면\n원활한 서비스 이용이 가능합니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
