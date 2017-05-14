package com.kimjunhong.bucketlist.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.adapter.BucketAdapter;
import com.kimjunhong.bucketlist.common.BucketTouchHelper;
import com.kimjunhong.bucketlist.common.SimpleDividerItemDecoration;
import com.kimjunhong.bucketlist.item.BucketItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 5. 11..
 */

public class ProcessingFragment extends Fragment {
    @BindView(R.id.processing_list) RecyclerView processingList;
    BucketAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processing, container, false);
        ButterKnife.bind(this, view);

        processingList.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        processingList.setLayoutManager(lm);

        List<BucketItem> items = new ArrayList<>();
        BucketItem[] item = new BucketItem[5];

        for(int i = 0; i < 5; i++) {
            item[i] = new BucketItem("우주 여행", "2017년 5월 11일");
            items.add(item[i]);
        }

        processingList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        adapter = new BucketAdapter(getActivity(), items);
        processingList.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new BucketTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(processingList);

        adapter.notifyDataSetChanged();

        return view;
    }
}
