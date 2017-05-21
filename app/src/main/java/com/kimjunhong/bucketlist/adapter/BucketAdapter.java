package com.kimjunhong.bucketlist.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.model.Bucket;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by INMA on 2017. 5. 12..
 */

public class BucketAdapter extends RealmRecyclerViewAdapter<Bucket, BucketAdapter.ViewHolder> {
    Context context;
    Dialog dialog;
    Realm realm;

    public BucketAdapter(Context context, OrderedRealmCollection<Bucket> data) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
    }

    @Override
    public BucketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BucketAdapter.ViewHolder holder, final int position) {
        final Bucket bucket = getItem(position);
        holder.data = bucket;
        holder.bucketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 커스텀 다이얼로그 가져오기
                final View customLayout = View.inflate(context, R.layout.dialog_edit, null);
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

                //수정, 취소 클릭
                customLayout.findViewById(R.id.dialog_button_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            realm = Realm.getDefaultInstance();
                            final EditText title = (EditText) customLayout.findViewById(R.id.editText_title);

                            if(title.getText().toString().equals("")) {
                                Toast.makeText(context, "버킷을 입력해주세요!", Toast.LENGTH_SHORT).show();
                            } else {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // sequence 값으로 position 값이 들어왔기 때문에 position + 1
                                        int updatePosition = position + 1;
                                        Bucket.update(realm, title.getText().toString(), updatePosition);

                                        Toast.makeText(context, "수정 되었습니다 :')", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } finally {
                            dialog.dismiss();
                            realm.close();
                        }
                    }
                });
                customLayout.findViewById(R.id.dialog_button_edit_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "취소 :^)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.bucketTitle.setText(bucket.getTitle());
        // 날짜 형식 (Date -> String)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        holder.bucketDate.setText(sdf.format(bucket.getDate()));
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }

    // 버킷 완료 & 취소 Snackbar
    public void completeBucket(RecyclerView recyclerView, final int position) {
        // Snackbar 텍스트 설정
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("버킷을 ");
        int emphasisStart = spanText.length();
        spanText.append("완료");
        // 강조 텍스트 색상 (16진수 ARGB)
        spanText.setSpan(new ForegroundColorSpan(0xFF4CAF50), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 강조 텍스트 bold
        spanText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanText.append(" 했습니다");

        // 2초(LENGTH_SHORT) 뒤 완료 처리
        final Handler mHandler = new Handler();
        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                // 버킷 완료
                realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Bucket.complete(realm, position);

                            // 완료 위치의 뒷 순서 버킷 sequence 값을 -1 씩해서 업데이트
                            RealmResults<Bucket> results = realm.where(Bucket.class)
                                                                .greaterThan("sequence", position)
                                                                .findAll();
                            for (Bucket bucket : results) {
                                bucket.setSequence(bucket.getSequence() - 1);
                            }
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        };

        Snackbar s = Snackbar.make(recyclerView, spanText, Snackbar.LENGTH_SHORT).setAction("취소", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버킷 완료 취소
                cancelAction(mHandler, mRunnable, position);
            }
        });

        View v = s.getView();
        //v.setBackgroundColor(Color.parseColor("#4CAF50"));

        TextView text = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);

        TextView action = (TextView) v.findViewById(android.support.design.R.id.snackbar_action);
        action.setTextColor(Color.WHITE);

        mHandler.postDelayed(mRunnable, 2000);
        s.show();
    }

    // 버킷 삭제 & 취소 Snackbar
    public void deleteBucket(RecyclerView recyclerView, final int position) {
        // Snackbar 텍스트 설정
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("버킷을 ");
        int emphasisStart = spanText.length();
        spanText.append("삭제");
        // 강조 텍스트 색상 (16진수 ARGB)
        spanText.setSpan(new ForegroundColorSpan(0xFFF44336), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 강조 텍스트 bold
        spanText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanText.append(" 했습니다");

        // 2초(LENGTH_SHORT) 뒤 데이터 삭제 처리
        final Handler mHandler = new Handler();
        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                // 버킷 삭제
                realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Log.v("log", "execute position: " + position);
                            Bucket.delete(realm, position);

                            // 지운 위치의 뒷 순서 버킷 sequence 값을 -1 씩해서 업데이트
                            RealmResults<Bucket> results = realm.where(Bucket.class)
                                    .greaterThan("sequence", position)
                                    .findAll();
                            for (Bucket bucket : results) {
                                bucket.setSequence(bucket.getSequence() - 1);
                            }
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        };

        Snackbar s = Snackbar.make(recyclerView, spanText, Snackbar.LENGTH_SHORT).setAction("취소", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버킷 삭제 취소
                cancelAction(mHandler, mRunnable, position);
            }
        });

        View v = s.getView();
        //v.setBackgroundColor(Color.parseColor("#F44336"));

        TextView text = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);

        TextView action = (TextView) v.findViewById(android.support.design.R.id.snackbar_action);
        action.setTextColor(Color.WHITE);

        mHandler.postDelayed(mRunnable, 2000);
        s.show();
    }

    // 버킷 스왑
    public void swapBucket(final int fromPosition, final int toPosition) {
        realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // sequence 값은 항상 position + 1
                    Bucket.swap(realm, fromPosition + 1, toPosition + 1);
                }
            });
        } finally {
            realm.close();
        }
    }

    // 버킷 취소
    private void cancelAction(Handler handler, Runnable runnable, int position) {
        // position이 final 변수이기 때문에 새로운 변수에서 값을 재정의
        // View 상태를 변경해야 하는데 position 값으로 sequence 값이 들어왔기 때문에 position - 1
        handler.removeCallbacks(runnable);
        int cancelPosition = position - 1;

        notifyItemChanged(cancelPosition);
        Log.v("log", "cancel position : " + cancelPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bucket_layout) LinearLayout bucketLayout;
        @BindView(R.id.bucket_title) TextView bucketTitle;
        @BindView(R.id.bucket_date) TextView bucketDate;
        public Bucket data;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
