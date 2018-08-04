package com.perry.reader.viewmodel.fragment;

import android.content.Context;
import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.perry.reader.api.BookService;
import com.perry.reader.model.BookBean;
import com.perry.reader.model.YellowBookBean;
import com.perry.reader.utils.AssetsUtils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.utils.rxhelper.RxObserver;
import com.perry.reader.view.fragment.IBookInfo;
import com.perry.reader.view.fragment.IYellowBookInfo;
import com.perry.reader.viewmodel.BaseViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;


/**
 * Created by Liang_Lu on 2017/12/6.
 */

public class YellowBooksInfo extends BaseViewModel {
    private final static String TAG = "YellowBooksInfo";
    IYellowBookInfo iBookInfo;

    public YellowBooksInfo(Context mContext, IYellowBookInfo iBookInfo) {
        super(mContext);
        this.iBookInfo = iBookInfo;
    }

    /**
     * @param type  类型 {"狂暴", "乱伦", "淫乱"}
     * @param major 名称
     * @param page  页数 （对于本地黄色小说没用）
     */
    public void getBooks(String type, String major, int page) {
        //读取本地 assets 文件
        AssetsUtils.ParserListener parserListener = new AssetsUtils.ParserListener() {
            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, "onError: " + errorMsg);
                if (iBookInfo != null) {
                    iBookInfo.stopLoading();
                }
            }

            @Override
            public void onSuccess(YellowBookBean yellowBookBean) {
                Log.e(TAG, "onSuccess: " + yellowBookBean.toString());
                if (iBookInfo != null) {
                    iBookInfo.stopLoading();
                }
                if (yellowBookBean.contents!= null && yellowBookBean.contents.size() > 0) {
                    if (iBookInfo != null) {
                        iBookInfo.getBooks(yellowBookBean.contents, page > 1 ? true : false);
                    }
                } else {
                    ToastUtils.show("无更多书籍");
                }

            }
        };
        AssetsUtils.getInstance().setParserListener(type, parserListener);
    }


}
