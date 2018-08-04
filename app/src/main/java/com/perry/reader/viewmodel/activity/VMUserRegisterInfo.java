package com.perry.reader.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.perry.reader.api.UserService;
import com.perry.reader.utils.MD5Utils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.utils.rxhelper.RxObserver;
import com.perry.reader.view.activity.IUserRegister;
import com.perry.reader.viewmodel.BaseViewModel;

/**
 * Created by Liang_Lu on 2018/1/5.
 */

public class VMUserRegisterInfo extends BaseViewModel {
    IUserRegister userRegister;

    public VMUserRegisterInfo(Context mContext, IUserRegister userRegister) {
        super(mContext);
        this.userRegister = userRegister;
    }


    public void register(String username, String password) {
        //对密码进行md5加密
        String md5Pass = MD5Utils.encrypt(password);
        RxHttpUtils.getSInstance().addHeaders(tokenMap())
                .createSApi(UserService.class)
                .register(username, md5Pass)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {
                        ToastUtils.show(data);
                        if (data.equals("注册成功")) {
                            userRegister.registerSuccess();
                        }
                    }
                });
    }
}
