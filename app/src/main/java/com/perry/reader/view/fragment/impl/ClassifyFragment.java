package com.perry.reader.view.fragment.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.perry.reader.R;
import com.perry.reader.model.BookClassifyBean;
import com.perry.reader.view.activity.impl.BookListActivity;
import com.perry.reader.view.adapter.ClassifyAdapter;
import com.perry.reader.view.base.BaseFragment;
import com.perry.reader.view.fragment.IClassifyBook;
import com.perry.reader.viewmodel.fragment.VMBookClassify;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.weavey.loading.lib.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public class ClassifyFragment extends BaseFragment implements IClassifyBook {

    private static final String TAG = "ClassifyFragment";
    @BindView(R.id.rv_classify)
    RecyclerView mRvClassify;
    @BindView(R.id.loadinglayout)
    LoadingLayout mLoadinglayout;

    String tabName;
    ClassifyAdapter mClassifyAdapter;
    private VMBookClassify mModel;
    List<BookClassifyBean.ClassifyBean> mClassifyBeans = new ArrayList<>();
    String getder = "male";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModel = new VMBookClassify(mContext, this);
        View view = setContentView(container, R.layout.fragment_classify, mModel);
        return view;
    }

    public static ClassifyFragment newInstance(String tabName) {
        Bundle args = new Bundle();
        args.putString("tabName", tabName);
        ClassifyFragment fragment = new ClassifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();
        tabName = getArguments().getString("tabName");
        mModel.bookClassify();

        mClassifyAdapter = new ClassifyAdapter(mClassifyBeans);
        mRvClassify.setLayoutManager(new LinearLayoutManager(mContext));
        mClassifyAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRvClassify.setAdapter(mClassifyAdapter);
        mClassifyAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString("getder", getder);
            bundle.putString("titleName", mClassifyBeans.get(position).getName());
            startActivity(BookListActivity.class, bundle);
        });


    }

    @Override
    public void getBookClassify(BookClassifyBean bookClassifyBean) {
        mLoadinglayout.setStatus(LoadingLayout.Success);
        mClassifyBeans.clear();
        switch (tabName) {
            case "男生":
                getder = "male";
                List<BookClassifyBean.ClassifyBean> classifyBeanList = bookClassifyBean.getMale();
                Log.e(TAG,"classifyBeanList:"+classifyBeanList.toString());
                BookClassifyBean.ClassifyBean classifyBean = new BookClassifyBean.ClassifyBean();
                classifyBean.setBookCount("未知");
                classifyBean.setIcon("也没有图标的");
                classifyBean.setName("情色");
                classifyBean.setMonthlyCount("这是啥");
                classifyBeanList.add(0,classifyBean);
                mClassifyBeans.addAll(classifyBeanList);
                break;
            case "女生":
                getder = "female";
                mClassifyBeans.addAll(bookClassifyBean.getFemale());
                break;
            case "出版":
                getder = "press";
                mClassifyBeans.addAll(bookClassifyBean.getPress());
                break;
        }
        mClassifyAdapter.notifyDataSetChanged();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {
    }

    @Override
    public void emptyData() {
        mLoadinglayout.setStatus(LoadingLayout.Empty);
    }

    @Override
    public void errorData(String error) {
        mLoadinglayout.setEmptyText(error).setStatus(LoadingLayout.Error);
    }

    @Override
    public void NetWorkError() {
        mLoadinglayout.setStatus(LoadingLayout.No_Network);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
