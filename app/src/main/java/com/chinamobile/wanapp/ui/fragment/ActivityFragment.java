package com.chinamobile.wanapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinamobile.wanapp.R;
import com.chinamobile.wanapp.baen.BaseItem;
import com.chinamobile.wanapp.baen.BaseTaskList;
import com.chinamobile.wanapp.baen.TaskData;
import com.chinamobile.wanapp.http.ApiServiceManager;
import com.chinamobile.wanapp.http.HttpResponse;
import com.chinamobile.wanapp.ui.viewitem.BigPicItem;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

public class ActivityFragment extends BaseFragment {


    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_find, null);
        ButterKnife.bind(this, mRootView);
        refreshlayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });

        refreshlayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        getData();
        return mRootView;
    }

    private MultiItemTypeAdapter adapter;
    private List<TaskData> mDatas;
    private int count = 1;
    private void getData() {
        ApiServiceManager.getDataList("1000",0, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskList taskList = gson.fromJson(json, BaseTaskList.class);
                    mDatas = new ArrayList<TaskData>();
                    mDatas.addAll(taskList.getTaskDatas());
                    setData();
                    setList();
                    count = 1;
                    if (refreshlayout!=null){
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (refreshlayout!=null){
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (refreshlayout!=null){
                    refreshlayout.finishRefresh(WAITE_TIME);
                }
            }
        });

    }


    private void loadMore(){
        ApiServiceManager.getDataList("1000",count, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskList taskList = gson.fromJson(json, BaseTaskList.class);
                    if (mDatas==null){
                        mDatas = new ArrayList<>();
                    }
                    mDatas.addAll(taskList.getTaskDatas());
                    setData();
                    adapter.notifyDataSetChanged();
                    count++;
                    if (refreshlayout!=null){
                        refreshlayout.finishLoadMore(WAITE_TIME);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if (refreshlayout!=null){
                        refreshlayout.finishLoadMore(WAITE_TIME);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (refreshlayout!=null){
                    refreshlayout.finishLoadMore(WAITE_TIME);
                }
            }
        });
    };


    private void setData() {
        if (mDatas == null) {
            return;
        }
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setType(BaseItem.ITEM_BIG_PIC);
        }


    }

    private void setList() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new MultiItemTypeAdapter(getContext(), mDatas);
        adapter.addItemViewDelegate(new BigPicItem());
        EmptyWrapper wrapper = new EmptyWrapper(adapter);
        wrapper.setEmptyView(R.layout.empty_view);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(wrapper);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
