package com.perry.reader.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.perry.reader.api.UserService;
import com.perry.reader.db.entity.UserBean;
import com.perry.reader.db.helper.UserHelper;
import com.perry.reader.utils.SharedPreUtils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.utils.rxhelper.RxObserver;
import com.perry.reader.view.base.BaseActivity;
import com.perry.reader.viewmodel.BaseViewModel;

/**
 * Created by Liang_Lu on 2018/1/5.
 */

public class VMUseLoginInfo extends BaseViewModel {

    public VMUseLoginInfo(Context mContext) {
        super(mContext);
    }


    public void login(String username, String password) {
        RxHttpUtils.getSInstance().addHeaders(tokenMap())
                .createSApi(UserService.class)
                .login(username, password)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<UserBean>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(UserBean userBean) {
                        ToastUtils.show("登录成功");
                        UserHelper.getsInstance().saveUser(userBean);
                        SharedPreUtils.getInstance().putString("token", userBean.getToken());
                        SharedPreUtils.getInstance().putString("username", userBean.name);
                        ((BaseActivity) mContext).finish();
                    }
                });
    }
}
