package com.kimjunhong.bucketlist.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.activity.DetailActivity;
import com.kimjunhong.bucketlist.activity.MainActivity;
import com.kimjunhong.bucketlist.model.CompletedBucket;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by INMA on 2017. 5. 13..
 */

public class CompletedBucketAdapter extends RealmRecyclerViewAdapter<CompletedBucket, CompletedBucketAdapter.ViewHolder> {
    Context context;
    Dialog dialog;
    Realm realm;

    public CompletedBucketAdapter(Context context, OrderedRealmCollection<CompletedBucket> data) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
    }

    @Override
    public CompletedBucketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CompletedBucketAdapter.ViewHolder holder, final int position) {
        CompletedBucket completedBucket = getItem(position);
        holder.data = completedBucket;
        holder.completedBucketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // completedBucket 데이터 detailActivity로 넘기기
                            Intent intent = new Intent(context, DetailActivity.class);
                            CompletedBucket completedBucket = CompletedBucket.findOne(realm, position + 1);
                            intent.putExtra("id", completedBucket.getId());

                            context.startActivity(intent);
                            ((MainActivity)context).finish();
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        });
        holder.completedBucketLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 커스텀 다이얼로그 가져오기
                View customLayout = View.inflate(context, R.layout.dialog_delete,null);
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

                //삭제, 취소 클릭
                customLayout.findViewById(R.id.dialog_button_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // sequence 값으로 position 값이 들어왔기 때문에 position + 1
                                    int deletePosition = position + 1;
                                    CompletedBucket.delete(realm, deletePosition);

                                    // 지운 위치의 뒷 순서 버킷 sequence 값을 -1 씩해서 업데이트
                                    RealmResults<CompletedBucket> results = realm.where(CompletedBucket.class)
                                                                                 .greaterThan("sequence", position)
                                                                                 .findAll();
                                    for (CompletedBucket completedBucket : results) {
                                        completedBucket.setSequence(completedBucket.getSequence() - 1);
                                    }

                                    Toast.makeText(context, "삭제 되었습니다 :'(", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } finally {
                            dialog.dismiss();
                            realm.close();
                        }
                    }
                });
                customLayout.findViewById(R.id.dialog_button_delete_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "잘 생각하셨어요 :D", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });

        holder.title.setText(completedBucket.getTitle());
        // 날짜 형식(Date -> String)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        holder.date.setText(sdf.format(completedBucket.getDate()));
        holder.location.setText(completedBucket.getLocation());
        if(completedBucket.getWith().equals("")) {
            // DB에 with 데이터가 없을 경우
            holder.hyphen.setVisibility(View.INVISIBLE);
            holder.with.setVisibility(View.INVISIBLE);
            holder.withSub.setVisibility(View.INVISIBLE);
        } else {
            // DB에 with 데이터가 있을 경우
            holder.hyphen.setVisibility(View.VISIBLE);
            holder.with.setVisibility(View.VISIBLE);
            holder.withSub.setVisibility(View.VISIBLE);
            holder.with.setText(completedBucket.getWith());
        }

        if(completedBucket.getPicture() == null) {
            // DB에 picture 데이터가 없을 경우
            holder.picture.setImageResource(R.drawable.icon_picture);
        } else {
            // DB에 picture 데이터가 있을 경우
            Bitmap bitmap = BitmapFactory.decodeByteArray(completedBucket.getPicture(), 0, completedBucket.getPicture().length);
            holder.picture.setImageBitmap(bitmap);
        }
    }

    @Override
    public long getItemId(int index) {
        return  getItem(index).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.completed_bucket_layout) LinearLayout completedBucketLayout;
        @BindView(R.id.completed_bucket_title) TextView title;
        @BindView(R.id.completed_bucket_with) TextView with;
        @BindView(R.id.completed_bucket_date) TextView date;
        @BindView(R.id.completed_bucket_location) TextView location;
        @BindView(R.id.completed_bucket_picture) ImageView picture;

        @BindView(R.id.completed_bucket_hyphen) TextView hyphen;
        @BindView(R.id.completed_bucket_with_sub) TextView withSub;

        public CompletedBucket data;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
