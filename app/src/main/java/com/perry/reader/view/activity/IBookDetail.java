package com.perry.reader.view.activity;

import com.perry.reader.model.BookBean;
import com.perry.reader.view.base.IBaseDataView;
import com.perry.reader.view.base.IBaseLoadView;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public interface IBookDetail extends IBaseLoadView {
    void addBookCallback();
    void getBookInfo(BookBean bookBean);
}
