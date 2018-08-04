package com.perry.reader.viewmodel.fragment;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.perry.reader.api.BookService;
import com.perry.reader.utils.rxhelper.RxObserver;
import com.perry.reader.model.BookClassifyBean;
import com.perry.reader.utils.NetworkUtils;
import com.perry.reader.view.fragment.IClassifyBook;
import com.perry.reader.viewmodel.BaseViewModel;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public class VMBookClassify extends BaseViewModel {
    IClassifyBook mIBookClassify;

    public VMBookClassify(Context mContext, IClassifyBook iClassifyBook) {
        super(mContext);
        mIBookClassify = iClassifyBook;
    }

    public void bookClassify() {
        if (!NetworkUtils.isConnected()) {
            if (mIBookClassify != null) {
                mIBookClassify.NetWorkError();
            }
            return;
        }

        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
       /* RxHttpUtils.createApi(BookService.class)*/
                .bookClassify()
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<BookClassifyBean>() {
                    @Override
                    protected void onError(String errorMsg) {
                        if (mIBookClassify != null) {
                            mIBookClassify.stopLoading();
                            mIBookClassify.errorData(errorMsg);
                        }
                    }

                    @Override
                    protected void onSuccess(BookClassifyBean data) {
                        if (mIBookClassify != null) {
                            mIBookClassify.stopLoading();
                            if (data == null) {
                                mIBookClassify.emptyData();
                                return;
                            }
                            mIBookClassify.getBookClassify(data);
                        }


                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposadle(d);
                    }
                });
    }


}
