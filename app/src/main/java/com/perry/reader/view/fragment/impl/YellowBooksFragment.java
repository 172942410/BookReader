package com.perry.reader.view.fragment.impl;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.perry.reader.R;
import com.perry.reader.model.YellowBookBean;
import com.perry.reader.view.activity.impl.YellowReadActivity;
import com.perry.reader.view.adapter.YellowBookInfoAdapter;
import com.perry.reader.view.base.BaseFragment;
import com.perry.reader.view.fragment.IYellowBookInfo;
import com.perry.reader.viewmodel.fragment.YellowBooksInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.weavey.loading.lib.LoadingLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public class YellowBooksFragment extends BaseFragment implements IYellowBookInfo {


    @BindView(R.id.rv_bookinfo)
    RecyclerView mRvBookinfo;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.loadinglayout)
    LoadingLayout mLoadinglayout;

    String titleName;
//    String getder;//男生、女生
    String type;//热门、完结
    private YellowBooksInfo mModel;
    List<YellowBookBean.Content> mBookBeans = new ArrayList<>();
    private YellowBookInfoAdapter mBookInfoAdapter;
    private int loadPage = 1;

//    private YellowCollBookBean mCollBookBean;
//    private YellowBookBean mBookBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModel = new YellowBooksInfo(mContext, this);
        View view = setContentView(container, R.layout.fragment_book_info, mModel);

//        AdView mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        
        return view;
    }

    public static YellowBooksFragment newInstance(String titleName, String getder, String type) {
        Bundle args = new Bundle();
        args.putString("titleName", titleName);
        args.putString("getder", getder);
        args.putString("type", type);
        YellowBooksFragment fragment = new YellowBooksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();
        titleName = getArguments().getString("titleName");
//        getder = getArguments().getString("getder");
        type = getArguments().getString("type");
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                ++loadPage;
//                mModel.getBooks(type, titleName, loadPage);
                stopLoading();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadPage = 1;
                mModel.getBooks(type, titleName, 1);
            }
        });

        mRefreshLayout.autoRefresh();

        mLoadinglayout.setOnReloadListener(v -> mModel.getBooks(type, titleName, 1));

        mBookInfoAdapter = new YellowBookInfoAdapter(mBookBeans);
        mRvBookinfo.setLayoutManager(new LinearLayoutManager(mContext));
        mBookInfoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRvBookinfo.setAdapter(mBookInfoAdapter);

        mBookInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
//            Intent intent = new Intent();
//            intent.setClass(mContext, BookDetailActivity.class);
//            intent.putExtra("bookid", mBookBeans.get(position).startpage);
            //TODO 直接跳转阅读页面吧
//            if (android.os.Build.VERSION.SDK_INT > 20) {
//                ImageView imageView = view.findViewById(R.id.book_brief_iv_portrait);
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), imageView, "bookImage").toBundle());
//            } else {
//                startActivity(intent);
//            }

            Bundle bundle = new Bundle();
            bundle.putSerializable(YellowReadActivity.EXTRA_COLL_BOOK, mBookBeans.get(position));
            bundle.putBoolean(YellowReadActivity.EXTRA_IS_COLLECTED, false);
            startActivity(YellowReadActivity.class, bundle);

        });

    }

    @Override
    public void getBooks(List<YellowBookBean.Content>  bookBeans, boolean isLoadMore) {
        if (!isLoadMore) {
            mBookBeans.clear();
        }
        mBookBeans.addAll(bookBeans);
        mBookInfoAdapter.notifyDataSetChanged();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
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


}
