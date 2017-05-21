package com.kimjunhong.bucketlist.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.model.CompletedBucket;

import java.io.File;
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

    boolean editFlag = true;
    static int REQUEST_PICTURE = 1;
    static int REQUEST_PHOTO_ALBUM = 2;
    static String SAMPLE_IMAGE = "ic_launcher.png";

    Dialog dialog;
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

        initView();
        initDatePicker();

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.imageView_picture) {
                    // 다이얼로그 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    // 커스텀 다이얼로그 가져오기
                    View customLayout = View.inflate(DetailActivity.this, R.layout.dialog_picture,null);
                    // 빌더에 다이얼로그 적용
                    builder.setView(customLayout);

                    dialog = builder.create();
                    // 빌더 크기 적용
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = 1000;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialog.show();

                    // 디폴트 다이얼로그 투명화
                    Window w = dialog.getWindow();
                    w.setAttributes(lp);
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    //카메라, 앨범 클릭
                    customLayout.findViewById(R.id.dialog_button_camera).setOnClickListener(this);
                    customLayout.findViewById(R.id.dialog_button_album).setOnClickListener(this);
                } else if(view.getId() == R.id.dialog_button_camera){
                    //다이얼로그 끄기 및 카메라 메소드
                    dialog.dismiss();
                    takePicture();
                } else if(view.getId() == R.id.dialog_button_album){
                    //다이얼로그 끄기 및 앨범 메소드
                    dialog.dismiss();
                    takeAlbum();
                }
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
                                    // TODO: 이미지 정보 가져오기
                                    completedBucket.setPicture(R.drawable.icon_picture);

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

                    if(location.getText().equals("어딘가")) {
                        // location 값이 없을 경우
                        location.setText("");
                    } else {
                        // location 값이 있을 경우
                        location.setText(completedBucket.getLocation());
                    }

                    with.setText(completedBucket.getWith());
                    memo.setText(completedBucket.getMemo());
                    picture.setImageDrawable(getResources().getDrawable(R.drawable.icon_picture));
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

    private void takePicture(){
        // 사진 찍기 인텐트, MediaStore.ACTION_IMAGE_CAPTURE 활용
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 그 후 파일을 지정해야하는데 File의 앞 부분 매개변수에는 파일의 절대경로를 붙여야 한다.
        // 그러나 직접 경로를 써넣으면 sdcard 접근이 안되므로
        // Environment.getExternalStorageDirectory()로 접근해서 경로를 가져오고
        // 두번째 매개 변수에 파일 이름을 넣어서 활용
        File file = new File(Environment.getExternalStorageDirectory(), SAMPLE_IMAGE);
        // 그 다음에 사진을 찍을때 그 파일을 현재 우리가 갖고있는 SAMPLE_IMAGE 저장해야한다.
        // 그래서 경로를 putExtra를 이용해서 파일 형태로 넣는다.
        // 그리고 실제로 이 파일이 가리키는 경로는 /mnt/sdcard/ic_launcher)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        // 사진 찍기 인텐트를 불러온다.
        startActivityForResult(intent, REQUEST_PICTURE);
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

    private Bitmap loadPicture(){
        // 사진 찍은것을 로드 해오는데 사이즈를 조절, 일단 파일을 가져오고
        File file = new File(Environment.getExternalStorageDirectory(), SAMPLE_IMAGE);
        // 현재 찍은 사진을 조절, 조절하는 클래스를 만듬
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 사이즈 설정
        options.inSampleSize = 4;
        // 조정한 사진 돌려보내기
        return BitmapFactory.decodeFile(file.getAbsolutePath(),options);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_PICTURE){
                // 사진을 찍은경우 그 사진을 로드
                picture.setImageBitmap(loadPicture());
            }
            if(requestCode==REQUEST_PHOTO_ALBUM){
                // 앨범에서 호출한 경우 data는 이전 인텐트(사진갤러리)에서 선택한 영역을 가져옴
                picture.setImageURI(data.getData());
            }
        }
    }
}
