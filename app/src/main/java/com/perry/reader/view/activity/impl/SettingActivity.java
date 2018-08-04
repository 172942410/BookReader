package com.perry.reader.view.activity.impl;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.RxHttpUtils;
import com.allen.library.download.DownloadObserver;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.perry.reader.R;
import com.perry.reader.WYApplication;
import com.perry.reader.db.helper.UserHelper;
import com.perry.reader.model.AppUpdateBean;
import com.perry.reader.utils.AppUpdateUtils;
import com.perry.reader.utils.Constant;
import com.perry.reader.utils.LoadingHelper;
import com.perry.reader.utils.LogUtils;
import com.perry.reader.utils.SharedPreUtils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.view.activity.ISetting;
import com.perry.reader.view.base.BaseActivity;
import com.perry.reader.viewmodel.BaseViewModel;
import com.perry.reader.viewmodel.activity.VMSettingInfo;

import java.io.File;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class SettingActivity extends BaseActivity implements ISetting {

    @BindView(R.id.btn_out)
    Button mBtnOut;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    private VMSettingInfo mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new VMSettingInfo(this, this);
        setBinddingView(R.layout.activity_setting, NO_BINDDING, mModel);
    }


    @Override
    protected void initView() {
        super.initView();
        initThemeToolBar("设置");
        mTvVersion.setText("版本号：v." + WYApplication.packageInfo.versionName);
    }

    @OnClick({R.id.btn_out, R.id.rl_version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_out:
                new MaterialDialog.Builder(this)
                        .title("退出登录")
                        .content("是否退出登录?")
                        .positiveText("确定")
                        .onPositive((dialog, which) -> {
                            UserHelper.getsInstance().removeUser();
                            SharedPreUtils.getInstance().sharedPreRemove("username");
                            finish();
                        })
                        .negativeText("取消")
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                break;
            case R.id.rl_version:
                mModel.appUpdate(true);
                break;
        }

    }

    @Override
    public void appUpdate(AppUpdateBean appUpdateBean) {
        AppUpdateUtils.getInstance().appUpdate(this, appUpdateBean);
    }


    @Override
    public void showLoading() {
        LoadingHelper.getInstance().showLoading(mContext);
    }

    @Override
    public void stopLoading() {
        LoadingHelper.getInstance().hideLoading();
    }
}
