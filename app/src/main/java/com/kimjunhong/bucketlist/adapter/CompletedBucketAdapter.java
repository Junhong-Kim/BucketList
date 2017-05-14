package com.kimjunhong.bucketlist.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.kimjunhong.bucketlist.item.CompletedBucketItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 5. 13..
 */

public class CompletedBucketAdapter extends RecyclerView.Adapter<CompletedBucketAdapter.ViewHolder> {
    Context context;
    List<CompletedBucketItem> items;
    Dialog dialog;

    public CompletedBucketAdapter(Context context, List<CompletedBucketItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public CompletedBucketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CompletedBucketAdapter.ViewHolder holder, int position) {
        CompletedBucketItem item = items.get(position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, DetailActivity.class));
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
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
                        Toast.makeText(context, "삭제 되었습니다 :'(", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
        holder.title.setText(item.getTitle());
        holder.with.setText(item.getWith());
        holder.date.setText(item.getDate());
        holder.location.setText(item.getLocation());
        holder.picture.setImageResource(R.drawable.icon_picture);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.completed_bucket_layout) LinearLayout layout;
        @BindView(R.id.completed_bucket_title) TextView title;
        @BindView(R.id.completed_bucket_with) TextView with;
        @BindView(R.id.completed_bucket_date) TextView date;
        @BindView(R.id.completed_bucket_location) TextView location;
        @BindView(R.id.completed_bucket_picture) ImageView picture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
