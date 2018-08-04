package com.perry.reader.view.fragment;

import com.perry.reader.model.BookClassifyBean;
import com.perry.reader.view.base.IBaseDataView;
import com.perry.reader.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public interface IClassifyBook extends IBaseDataView {

    void getBookClassify(BookClassifyBean bookClassifyBean);

}
