package com.perry.reader.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.perry.reader.WYApplication;
import com.perry.reader.api.UserService;
import com.perry.reader.model.AppUpdateBean;
import com.perry.reader.utils.SnackBarUtils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.utils.rxhelper.RxObserver;
import com.perry.reader.view.activity.ISetting;
import com.perry.reader.viewmodel.BaseViewModel;

/**
 * Created by Liang_Lu on 2018/1/22.
 */

public class VMSettingInfo extends BaseViewModel {
    ISetting mISetting;

    public VMSettingInfo(Context mContext, ISetting iSetting) {
        super(mContext);
        mISetting = iSetting;
    }

    /**
     * 版本更新
     */
    public void appUpdate(boolean isTip) {
        mISetting.showLoading();
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(UserService.class)
                .appUpdate()
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<AppUpdateBean>() {
                    @Override
                    protected void onError(String errorMsg) {
                        mISetting.stopLoading();
                    }

                    @Override
                    protected void onSuccess(AppUpdateBean data) {
                        mISetting.stopLoading();
                        if (WYApplication.packageInfo.versionCode < data.getVersioncode()) {
                            mISetting.appUpdate(data);
                        } else {
                            if (isTip) {
                                ToastUtils.show("当前是最新版本");
                            }
                        }
                    }
                });
    }
}
