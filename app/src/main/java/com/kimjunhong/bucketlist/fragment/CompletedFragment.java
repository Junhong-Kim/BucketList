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
import com.kimjunhong.bucketlist.adapter.BucketAdapter;
import com.kimjunhong.bucketlist.item.BucketItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 5. 11..
 */

public class CompletedFragment extends Fragment {
    @BindView(R.id.completed_list) RecyclerView completedList;
    BucketAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        ButterKnife.bind(this, view);

        completedList.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        completedList.setLayoutManager(lm);

        List<BucketItem> items = new ArrayList<>();
        BucketItem[] item = new BucketItem[5];

        for(int i = 0; i < 5; i++) {
            item[i] = new BucketItem("완료 : " + i, "2017-05-11");
            items.add(item[i]);
        }

        adapter = new BucketAdapter(getActivity(), items);
        completedList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }
}
