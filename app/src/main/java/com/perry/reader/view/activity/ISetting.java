package com.perry.reader.view.activity;

import com.perry.reader.model.AppUpdateBean;
import com.perry.reader.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2018/1/22.
 */

public interface ISetting extends IBaseLoadView{
    void appUpdate(AppUpdateBean appUpdateBean);
}
