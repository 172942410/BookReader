package com.perry.reader.view.base;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/11/29.
 */

public class BaseViewPageAdapter extends FragmentPagerAdapter {
    private String[] titleArray;
    private List<Fragment> fragments;

    public BaseViewPageAdapter(FragmentManager fm, String[] titleArray, List<Fragment> fragments) {
        super(fm);
        this.titleArray = titleArray;
        this.fragments=fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    @Override
    public int getCount() {
        return titleArray.length;
    }

}
