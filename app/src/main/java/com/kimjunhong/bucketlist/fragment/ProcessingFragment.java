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
import com.kimjunhong.bucketlist.model.BucketList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by INMA on 2017. 5. 11..
 */

public class ProcessingFragment extends Fragment {
    @BindView(R.id.processing_list) RecyclerView processingList;
    BucketAdapter adapter;
    Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processing, container, false);

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
        // BucketList sequence 오름차순으로 데이터 가져오기
        adapter = new BucketAdapter(realm.where(BucketList.class).findFirst().getBucketList().sort("sequence", Sort.ASCENDING));
        // RecyclerView 설정
        processingList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        processingList.setHasFixedSize(true);
        // Adapter 연결
        processingList.setAdapter(adapter);
        // Divider 추가
        processingList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        // Helper 추가
        ItemTouchHelper.Callback callback = new BucketTouchHelper(adapter, processingList);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(processingList);
    }
}
