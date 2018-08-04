package com.perry.reader.view.fragment;

import com.perry.reader.model.BookBean;
import com.perry.reader.view.base.IBaseDataView;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public interface IBookInfo extends IBaseDataView {
    void getBooks(List<BookBean> bookBeans,boolean isLoadMore);
}
