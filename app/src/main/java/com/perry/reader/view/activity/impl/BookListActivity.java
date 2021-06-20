package com.perry.reader.view.activity.impl;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.perry.reader.R;
import com.perry.reader.utils.AppFileUtils;
import com.perry.reader.utils.AssetsUtils;
import com.perry.reader.view.base.BaseActivity;
import com.perry.reader.view.base.BaseViewPageAdapter;
import com.perry.reader.view.fragment.impl.BooksInfoFragment;
import com.perry.reader.view.fragment.impl.YellowBooksFragment;
import com.perry.reader.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookListActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tabStrip)
    NavigationTabStrip mTabStrip;
    private String mTitleName;
    private String mGetder;

    String[] titles = {"热门", "新书", "好评"/*, "完结"*/};
    String[] types = {"hot", "new", "reputation", "over"};
    private List<Fragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinddingView(R.layout.activity_book_list, NO_BINDDING, new BaseViewModel(mContext));
    }


    @Override
    protected void initView() {
        super.initView();
        AssetsUtils.init(getApplication());
        AppFileUtils.init(getApplication());
        mTitleName = getIntent().getStringExtra("titleName");
        mGetder = getIntent().getStringExtra("getder");
        initThemeToolBar(mTitleName);

        mFragments = new ArrayList<>();


        if(!TextUtils.isEmpty(mTitleName)&& mTitleName.equals("情色")){
            titles = new String[]{"狂暴587本", "乱伦293本", "淫乱599本"};
            types = new String[]{"kuangbao", "luanlun", "yinluan"};
//            titles = new String[]{"乱伦"};
//            types = new String[]{"luanlun"};
            for (String type : types) {
                mFragments.add(YellowBooksFragment.newInstance(mTitleName, mGetder, type));
            }
        }else{
            for (String type : types) {
                mFragments.add(BooksInfoFragment.newInstance(mTitleName, mGetder, type));
            }
        }
        mViewpager.setAdapter(new BaseViewPageAdapter(getSupportFragmentManager(), titles, mFragments));
        mViewpager.setOffscreenPageLimit(4);
        mTabStrip.setTitles(titles);
        mTabStrip.setViewPager(mViewpager);

    }
}
