package com.kimjunhong.bucketlist.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.model.CompletedBucket;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by INMA on 2017. 5. 12..
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.textView_title) TextView title;
    @BindView(R.id.textView_date) TextView date;
    @BindView(R.id.editText_location) EditText location;
    @BindView(R.id.editText_with) EditText with;
    @BindView(R.id.editText_memo) EditText memo;
    @BindView(R.id.imageView_picture) ImageView picture;
    @BindView(R.id.adView_banner) AdView banner;

    boolean editFlag = true;
    static int REQUEST_PHOTO_ALBUM = 0;

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.icon_back);

        AdRequest adRequest = new AdRequest.Builder()
                                           .addTestDevice("6D55F1109C483970A5FC7E68FF5D5A34") // 갤럭시 노트2
                                           .addTestDevice("49DD6674EE06C76088446D108A914D1C") // 갤럭시 노트4
                                           .build();
        banner.loadAd(adRequest);

        initView();
        initDatePicker();

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAlbum();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 기본 타이틀 제거
        this.setTitle(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;

            case R.id.edit:
                if (editFlag) {
                    // 완료한 버킷 수정
                    try {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    CompletedBucket completedBucket = new CompletedBucket();

                                    completedBucket.setTitle(title.getText().toString());
                                    // 날짜 형식 (String -> Date)
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                                    completedBucket.setDate(sdf.parse(date.getText().toString()));
                                    completedBucket.setLocation(location.getText().toString());
                                    completedBucket.setWith(with.getText().toString());
                                    completedBucket.setMemo(memo.getText().toString());
                                    // 이미지 정보 가져오기
                                    if(picture.getDrawable() != null) {
                                        // picture의 Drawable 가져오기
                                        Drawable drawable = picture.getDrawable();
                                        // picture Drawable의 bitmap 추출
                                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                                        // Stream 생성
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        // PNG로 변환(JPEG로 변환할 경우 검정색 화면만 나옴)
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        // Stream에 있는 정보를 byte[]로 변환
                                        byte[] bitMapData = stream.toByteArray();
                                        // DB에 이미지 저장
                                        completedBucket.setPicture(bitMapData);
                                    }

                                    CompletedBucket.update(realm, completedBucket);
                                    Toast.makeText(getApplicationContext(), "수정 되었습니다", Toast.LENGTH_SHORT).show();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "에러", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } finally {
                        realm.close();
                        bucketData(false);
                    }
                } else {
                    bucketData(true);
                }

                editFlag = !editFlag;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Intent intent = getIntent();
                    int id = intent.getIntExtra("id", 0);
                    CompletedBucket completedBucket = realm.where(CompletedBucket.class).equalTo("id", id).findFirst();

                    title.setText(completedBucket.getTitle());
                    // 날짜 형식 (Date -> String)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                    date.setText(sdf.format(completedBucket.getDate()));
                    location.setText(completedBucket.getLocation());
                    with.setText(completedBucket.getWith());
                    memo.setText(completedBucket.getMemo());

                    if(completedBucket.getPicture() != null) {
                        // byte[]로 저장되어 있는 데이터를 bitmap으로 변환하여 가져오기
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(completedBucket.getPicture(), 0, completedBucket.getPicture().length);

                        // picture에 image 정보 설정
                        //picture.setImageBitmap(bitmap);
                        Glide.with(getApplicationContext())
                             .load(completedBucket.getPicture()).asBitmap()
                             .into(picture);
                    }
                }
            });
        } finally {
            realm.close();
        }
    }

    private void bucketData(boolean flag) {
        date.setEnabled(flag);
        location.setEnabled(flag);
        with.setEnabled(flag);
        memo.setEnabled(flag);
        picture.setEnabled(flag);
    }

    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                        Toast.makeText(getApplicationContext(), year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
                    }
                };

                new DatePickerDialog(DetailActivity.this, datePicker, year, month, day).show();
            }
        });
    }

    private void takeAlbum(){
        // 사진을 불러오는 인텐트, ACTION_PICK 활용
        Intent intent = new Intent(Intent.ACTION_PICK);
        // 갤러리리의 기본 설정, 아래는 이미지와 그 경로를 표준 타입으로 설정
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        // 사진이 저장된 위치(sdcard)에 데이터가 있다고 지정
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_PHOTO_ALBUM){
                // 앨범에서 호출한 경우 data는 이전 인텐트(사진갤러리)에서 선택한 영역을 가져옴
                picture.setImageURI(data.getData());
            }
        }
    }
}
