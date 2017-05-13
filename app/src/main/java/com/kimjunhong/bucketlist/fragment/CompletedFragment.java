package com.kimjunhong.bucketlist.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.adapter.CompletedBucketAdapter;
import com.kimjunhong.bucketlist.item.CompletedBucketItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 5. 11..
 */

public class CompletedFragment extends Fragment {
    @BindView(R.id.completed_list) RecyclerView completedList;
    CompletedBucketAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        ButterKnife.bind(this, view);

        completedList.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        completedList.setLayoutManager(lm);

        List<CompletedBucketItem> items = new ArrayList<>();
        CompletedBucketItem[] item = new CompletedBucketItem[5];


        item[0] = new CompletedBucketItem("번지점프", "친구", "2017년 5월 11일", "강원도", R.drawable.icon_picture);
        item[1] = new CompletedBucketItem("스카이 다이빙", "친구", "2017년 5월 11일", "세부", R.drawable.icon_picture);
        item[2] = new CompletedBucketItem("유럽 여행", "친구", "2017년 5월 11일", "영국", R.drawable.icon_picture);
        item[3] = new CompletedBucketItem("자전거 여행", "친구", "2017년 5월 11일", "부산", R.drawable.icon_picture);
        item[4] = new CompletedBucketItem("국토대장정", "친구", "2017년 5월 11일", "땅끝마을", R.drawable.icon_picture);

        for(int i = 0; i < 5; i++) {
            items.add(item[i]);
        }

        adapter = new CompletedBucketAdapter(getActivity(), items);
        completedList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }
}
