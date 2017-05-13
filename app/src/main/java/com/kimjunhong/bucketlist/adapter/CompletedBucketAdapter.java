package com.kimjunhong.bucketlist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, DetailActivity.class));
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
