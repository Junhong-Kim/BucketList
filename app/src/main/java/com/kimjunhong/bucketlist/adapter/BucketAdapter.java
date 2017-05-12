package com.kimjunhong.bucketlist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.item.BucketItem;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 5. 12..
 */

public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.ViewHolder> {
    Context context;
    List<BucketItem> items;

    public BucketAdapter(Context context, List<BucketItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public BucketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BucketAdapter.ViewHolder holder, int position) {
        BucketItem item = items.get(position);
        holder.bucketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.bucketTitle.setText(item.getTitle());
        holder.bucketDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void addItem(int position) {
        items.add(new BucketItem("New Title", "New Date"));
        notifyItemInserted(items.size());
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    public void swapItem(int fromposition, int toPosition) {
        Collections.swap(items, fromposition, toPosition);
        notifyItemMoved(fromposition, toPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bucket_layout) LinearLayout bucketLayout;
        @BindView(R.id.bucket_title) TextView bucketTitle;
        @BindView(R.id.bucket_date) TextView bucketDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
