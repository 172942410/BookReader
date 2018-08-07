package com.perry.reader.db.entity;

import com.perry.reader.db.gen.BookChapterBeanDao;
import com.perry.reader.db.gen.DaoSession;
import com.perry.reader.db.gen.YellowCollBookBeanDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Liang_Lu on 2017/11/21.
 */
@Entity
public class YellowCollBookBean implements Serializable {
    private static final long serialVersionUID = 56423411312L;
    @Id
    private String yellowId;//如果是本地文件，那么id为所在的地址 就是asstes下的文件的绝对路径

    private String title;
    private String author;
    private String shortIntro;
    private String cover;
    private boolean hasCp;
    private int latelyFollower;
    private double retentionRatio;
    //最新更新日期
    private String updated;
    //最新阅读日期
    private String lastRead;
    private int chaptersCount;
    private String lastChapter;
    //是否更新或未阅读
    private boolean isUpdate = true;
    //是否是本地文件
    private boolean isLocal = true;
    @ToMany(referencedJoinProperty = "bookId")
    private List<BookChapterBean> bookChapterList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 694779093)
    private transient YellowCollBookBeanDao myDao;

    @Generated(hash = 543864579)
    public YellowCollBookBean(String yellowId, String title, String author, String shortIntro, String cover, boolean hasCp,
            int latelyFollower, double retentionRatio, String updated, String lastRead, int chaptersCount, String lastChapter,
            boolean isUpdate, boolean isLocal, String startpage, int pagecount, int contetindex) {
        this.yellowId = yellowId;
        this.title = title;
        this.author = author;
        this.shortIntro = shortIntro;
        this.cover = cover;
        this.hasCp = hasCp;
        this.latelyFollower = latelyFollower;
        this.retentionRatio = retentionRatio;
        this.updated = updated;
        this.lastRead = lastRead;
        this.chaptersCount = chaptersCount;
        this.lastChapter = lastChapter;
        this.isUpdate = isUpdate;
        this.isLocal = isLocal;
        this.startpage = startpage;
        this.pagecount = pagecount;
        this.contetindex = contetindex;
    }

    @Generated(hash = 1873109672)
    public YellowCollBookBean() {
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1732460544)
    public List<BookChapterBean> getBookChapterList() {
        if (bookChapterList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookChapterBeanDao targetDao = daoSession.getBookChapterBeanDao();
            List<BookChapterBean> bookChapterListNew = targetDao
                    ._queryYellowCollBookBean_BookChapterList(yellowId);
            synchronized (this) {
                if (bookChapterList == null) {
                    bookChapterList = bookChapterListNew;
                }
            }
        }
        return bookChapterList;
    }

    public void setBookChapterList(List<BookChapterBean> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }


    public void setBookChapters(List<BookChapterBean> beans){
        bookChapterList = beans;
//        for (BookChapterBean bean : bookChapterList){
//            bean.setBookId(get_id());
//        }
    }

    public List<BookChapterBean> getBookChapters(){
        if (daoSession == null){
            return bookChapterList;
        }
        else {
            return getBookChapterList();
        }
    }

    public String getYellowId() {
        return yellowId;
    }

    public void setYellowId(String yellowId) {
        this.yellowId = yellowId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShortIntro() {
        return shortIntro;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isHasCp() {
        return hasCp;
    }

    public void setHasCp(boolean hasCp) {
        this.hasCp = hasCp;
    }

    public int getLatelyFollower() {
        return latelyFollower;
    }

    public void setLatelyFollower(int latelyFollower) {
        this.latelyFollower = latelyFollower;
    }

    public double getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(double retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean getHasCp() {
        return this.hasCp;
    }

    public boolean getIsUpdate() {
        return this.isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean getIsLocal() {
        return this.isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1077762221)
    public synchronized void resetBookChapterList() {
        bookChapterList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1144355279)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getYellowCollBookBeanDao() : null;
    }

    @Override
    public String toString() {
        return "YellowCollBookBean{" +
                "yellowId='" + yellowId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", shortIntro='" + shortIntro + '\'' +
                ", cover='" + cover + '\'' +
                ", hasCp=" + hasCp +
                ", latelyFollower=" + latelyFollower +
                ", retentionRatio=" + retentionRatio +
                ", updated='" + updated + '\'' +
                ", lastRead='" + lastRead + '\'' +
                ", chaptersCount=" + chaptersCount +
                ", lastChapter='" + lastChapter + '\'' +
                ", isUpdate=" + isUpdate +
                ", isLocal=" + isLocal +
                ", bookChapterList=" + bookChapterList +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                ", startpage=" + startpage +
                ", pagecount=" + pagecount +
                ", contetindex=" + contetindex +
                '}';
    }
    private String startpage;
    public void setPageCount(String startpage) {
        this.startpage = startpage;
    }
    private int pagecount;
    public void setStartPage(int pagecount) {
        this.pagecount = pagecount;
    }
    private int contetindex;
    public void setContetIndex(int contetindex) {
        this.contetindex = contetindex;
    }

    public String getStartpage() {
        return this.startpage;
    }

    public void setStartpage(String startpage) {
        this.startpage = startpage;
    }

    public int getPagecount() {
        return this.pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public int getContetindex() {
        return this.contetindex;
    }

    public void setContetindex(int contetindex) {
        this.contetindex = contetindex;
    }
}
