package com.perry.reader.view.fragment;

import com.perry.reader.db.entity.CollBookBean;
import com.perry.reader.model.BookBean;
import com.perry.reader.view.base.IBaseLoadView;

import java.util.List;

/**
 * Created by Liang_Lu on 2018/1/19.
 */

public interface IBookShelf extends IBaseLoadView {
    void booksShelfInfo(List<CollBookBean> beans);

    void bookInfo(CollBookBean bean);

    void deleteSuccess();
}
