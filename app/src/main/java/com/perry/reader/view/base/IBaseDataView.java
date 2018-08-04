package com.perry.reader.view.base;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public interface IBaseDataView extends IBaseLoadView{

    void emptyData();
    void errorData(String error);
    void NetWorkError();
}
