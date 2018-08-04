package com.perry.reader.view.fragment;

import com.perry.reader.model.BookBean;
import com.perry.reader.view.base.IBaseDataView;
import com.perry.reader.view.base.IBaseLoadView;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public interface IBookSearchInfo extends IBaseLoadView {
    void getSearchBooks(List<BookBean> bookBeans);
}
