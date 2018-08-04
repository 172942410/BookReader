package com.perry.reader.view.activity;

import com.perry.reader.db.entity.UserBean;
import com.perry.reader.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2018/1/11.
 */

public interface IUserInfo extends IBaseLoadView{

    void uploadSuccess(String imageUrl);
    void userInfo(UserBean userBean);

}
