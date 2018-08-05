package com.perry.reader.db.helper;

import android.util.Log;

import com.perry.reader.db.entity.CollBookBean;
import com.perry.reader.db.entity.YellowCollBookBean;
import com.perry.reader.db.gen.CollBookBeanDao;
import com.perry.reader.db.gen.DaoSession;
import com.perry.reader.db.gen.YellowCollBookBeanDao;
import com.perry.reader.utils.Constant;
import com.perry.reader.utils.FileUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Liang_Lu on 2017/12/1.
 * 书架数据库操作工具类
 */

public class YellowCollBookHelper {
    private static final String TAG = "YellowCollBookHelper";
    private static volatile YellowCollBookHelper sInstance;
    private static DaoSession daoSession;
    private static YellowCollBookBeanDao collBookBeanDao;

    public static YellowCollBookHelper getsInstance() {
        if (sInstance == null) {
            synchronized (YellowCollBookHelper.class) {
                if (sInstance == null) {
                    sInstance = new YellowCollBookHelper();
                    daoSession = DaoDbHelper.getInstance().getSession();
                    collBookBeanDao = daoSession.getYellowCollBookBeanDao();
                }
            }
        }
        return sInstance;
    }

    /**
     * 保存一本书籍 同步
     *
     * @param collBookBean
     */
    public void saveBook(YellowCollBookBean collBookBean) {
        collBookBeanDao.insertOrReplace(collBookBean);
    }

    /**
     * 保存多本书籍 同步
     *
     * @param collBookBeans
     */
    public void saveBooks(List<YellowCollBookBean> collBookBeans) {
        collBookBeanDao.insertOrReplaceInTx(collBookBeans);
    }


    /**
     * 保存一本书籍 异步
     *
     * @param collBookBean
     */
    public void saveBookWithAsync(YellowCollBookBean collBookBean) {
        daoSession.startAsyncSession().runInTx(() -> {
            if (collBookBean.getBookChapters() != null) {
                //存储BookChapterBean(需要找个免更新的方式)
                daoSession.getBookChapterBeanDao()
                        .insertOrReplaceInTx(collBookBean.getBookChapters());
            }
            //存储CollBook (确保先后顺序，否则出错)
            collBookBeanDao.insertOrReplace(collBookBean);
        });
    }

    /**
     * 保存多本书籍 异步
     *
     * @param collBookBeans
     */
    public void saveBooksWithAsync(List<YellowCollBookBean> collBookBeans) {
        daoSession.startAsyncSession()
                .runInTx(
                        () -> {
                            for (YellowCollBookBean bean : collBookBeans) {
                                if (bean.getBookChapters() != null) {
                                    //存储BookChapterBean(需要修改，如果存在id相同的则无视)
                                    daoSession.getBookChapterBeanDao()
                                            .insertOrReplaceInTx(bean.getBookChapters());
                                }
                            }
                            //存储CollBook (确保先后顺序，否则出错)
                            collBookBeanDao.insertOrReplaceInTx(collBookBeans);
                        }
                );

    }

    /**
     * 删除书籍
     *
     * @param collBookBean
     */
    public Observable<String> removeBookInRx(YellowCollBookBean collBookBean) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //查看文本中是否存在删除的数据
                FileUtils.deleteFile(Constant.BOOK_CACHE_PATH + collBookBean.getYellowId());
                //删除任务
                BookDownloadHelper.getsInstance().removeDownloadTask(collBookBean.getYellowId());
                //删除目录
                BookChapterHelper.getsInstance().removeBookChapters(collBookBean.getYellowId());
                //删除CollBook
                collBookBeanDao.delete(collBookBean);
                e.onNext("删除成功");
            }
        });
    }

    /**
     * 删除所有书籍
     */
    public void removeAllBook() {
        for (YellowCollBookBean collBookBean : findAllBooks()) {
            removeBookInRx(collBookBean);
        }
    }

    /**
     * 查询一本书籍
     */
    public YellowCollBookBean findBookById(String id) {
        Log.e(TAG,"findBookById id:"+id);
        YellowCollBookBean bookBean = collBookBeanDao.queryBuilder().where(YellowCollBookBeanDao.Properties.YellowId.eq(id)).unique();
        return bookBean;
    }

    /**
     * 查询所有书籍
     */
    public List<YellowCollBookBean> findAllBooks() {
        return collBookBeanDao
                .queryBuilder()
                .orderDesc(CollBookBeanDao.Properties.LastRead)
                .list();
    }


}
