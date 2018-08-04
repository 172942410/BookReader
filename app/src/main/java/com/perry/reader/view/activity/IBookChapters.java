package com.perry.reader.view.activity;

import com.perry.reader.model.BookChaptersBean;
import com.perry.reader.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2017/12/11.
 */

public interface IBookChapters extends IBaseLoadView {
    void bookChapters(BookChaptersBean bookChaptersBean);

    void finishChapters();

    void errorChapters();

}
