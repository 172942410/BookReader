package com.perry.reader.model;
import android.util.Log;
import com.perry.reader.db.entity.YellowCollBookBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.chad.library.adapter.base.listener.SimpleClickListener.TAG;

/**
 * Created by Liang_Lu on 2017/12/6.
 */
public class YellowBookBean implements Serializable {
    /**
     *
     */
    public String bookname;
    public int allpages;
    public int contentcount;
    public String bookshortname;
    public List<Content> contents = new ArrayList<>();;

    @Override
    public String toString() {
        return "YellowBookBean{" +
                "bookname='" + bookname + '\'' +
                ", allpages=" + allpages +
                ", contentcount=" + contentcount +
                ", bookshortname='" + bookshortname + '\'' +
                ", contents=" + contents +
                '}';
    }

    public Content newContent(){
        Content content = new Content();
        if(contents != null) {
        }else{
            Log.e(TAG, "newContent: 中的list异常为空了" );
            contents = new ArrayList<>();
        }
        contents.add(content);
        return content;
    }

    public class Content implements Serializable{
        /**
         *
         */
        public String id;
        public String name;
        public int startpage;
        public int pagecount;
        public int contetindex;
        public int progress;

        public YellowCollBookBean mCollBookBean;

        @Override
        public String toString() {
            return "content{" +
                    "name='" + name + '\'' +
                    ", startpage=" + startpage +
                    ", pagecount=" + pagecount +
                    ", contetindex=" + contetindex +
                    ", progress=" + progress +
                    ", mCollBookBean=" + mCollBookBean +
                    '}';
        }
    }

}
