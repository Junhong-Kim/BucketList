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
import com.kimjunhong.bucketlist.common.SimpleDividerItemDecoration;
import com.kimjunhong.bucketlist.model.CompletedBucketList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by INMA on 2017. 5. 11..
 */

public class CompletedFragment extends Fragment {
    @BindView(R.id.completed_list) RecyclerView completedList;
    CompletedBucketAdapter adapter;
    Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        initRecyclerView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    private void initRecyclerView() {
        adapter = new CompletedBucketAdapter(getActivity(), realm.where(CompletedBucketList.class).findFirst().getCompletedBucketList().sort("sequence", Sort.ASCENDING));
        completedList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        completedList.setHasFixedSize(true);
        completedList.setAdapter(adapter);
        completedList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    }
}
