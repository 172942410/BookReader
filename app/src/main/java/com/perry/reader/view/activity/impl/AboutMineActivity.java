package com.perry.reader.view.activity.impl;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.perry.reader.R;
import com.perry.reader.view.base.BaseActivity;
import com.perry.reader.viewmodel.BaseViewModel;

import butterknife.BindView;

public class AboutMineActivity extends BaseActivity {

    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinddingView(R.layout.activity_about_mine, NO_BINDDING, new BaseViewModel(this));
        initThemeToolBar("关于作者");

        Glide.with(mContext).load(R.mipmap.avatar).apply(new RequestOptions().transform(new CircleCrop())).into(mIvAvatar);
    }
}
