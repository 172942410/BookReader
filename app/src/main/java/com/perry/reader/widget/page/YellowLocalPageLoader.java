package com.perry.reader.widget.page;


import android.text.TextUtils;
import android.util.Log;

import com.perry.reader.db.entity.BookChapterBean;
import com.perry.reader.db.entity.YellowCollBookBean;
import com.perry.reader.db.helper.YellowCollBookHelper;
import com.perry.reader.model.Void;
import com.perry.reader.utils.AppFileUtils;
import com.perry.reader.utils.AssetsUtils;
import com.perry.reader.utils.Charset;
import com.perry.reader.utils.Constant;
import com.perry.reader.utils.FileUtils;
import com.perry.reader.utils.IOUtils;
import com.perry.reader.utils.StringUtils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.utils.rxhelper.RxUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * Created by newbiechen on 17-7-1.
 * 问题:
 * 1. 异常处理没有做好
 */

public class YellowLocalPageLoader extends YellowPageLoader {
    private static final String TAG = "LocalPageLoader";
    //默认从文件中获取数据的长度
    private final static int BUFFER_SIZE = 512 * 1024;
    //没有标题的时候，每个章节的最大长度
    private final static int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;

    // "序(章)|前言"
    private final static Pattern mPreChapterPattern = Pattern.compile("^(\\s{0,10})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50))(\\s{0,10})$", Pattern.MULTILINE);

    //正则表达式章节匹配模式
    // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
    private static final String[] CHAPTER_PATTERNS = new String[]{"^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\f\t])(.{0,30})$",
            "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
            "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
            "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"};

    //章节解析模式
    private Pattern mChapterPattern = null;
    //编码类型
    private Charset mCharset;

    private Disposable mChapterDisp = null;

    public YellowLocalPageLoader(YellowPageView pageView) {
        super(pageView);
        mStatus = STATUS_PARSE;
    }

    @Override
    public void openBook(YellowCollBookBean collBookBean) {
        super.openBook(collBookBean);
        //这里id表示本地文件的绝对路径
        boolean isExists = AssetsUtils.getInstance().isFileExists(collBookBean.getYellowId());
        //判断是否文件存在
        if (!isExists) {
            mStatus = STATUS_PARSE_ERROR;
            return;
        }
        isBookOpen = false;
        //通过RxJava异步处理分章事件
        Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> e) throws Exception {
                loadBook(mCollBook.getYellowId());
                e.onSuccess(new Void());
            }
        }).compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mChapterDisp = d;
                    }

                    @Override
                    public void onSuccess(Void value) {
                        mChapterDisp = null;
                        //提示目录加载完成
                        if (mPageChangeListener != null) {
                            mPageChangeListener.onCategoryFinish(mChapterList);
                        }
                        //打开章节，并加载当前章节
                        openChapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //数据读取错误(弄个文章解析错误的Tip,会不会好一点)
                        mStatus = STATUS_ERROR;
                        ToastUtils.show("数据解析错误");
                    }
                });
    }

    //采用的是随机读取
    private void loadBook(String fileName) throws IOException {
        //获取文件编码
//        mCharset = FileUtils.getCharset(fileName,true);
        mCharset = Charset.GBK;
        //查找章节，分配章节
        loadChapters();
    }

    /**
     * 未完成的部分:
     * 1. 序章的添加
     * 2. 章节存在的书本的虚拟分章效果
     *
     * @throws IOException
     */
    private void loadChapters() throws IOException {
        List<TxtChapter> chapters = new ArrayList<>();
        //TODO 这里需要重写
        //获取文件流
//        RandomAccessFile bookStream = new RandomAccessFile(mCollBook.getYellowId(), "r");
        RandomAccessFile bookStream =  getRandomFile(mCollBook.getYellowId());
        //寻找匹配文章标题的正则表达式，判断是否存在章节名
        boolean hasChapter = checkChapterType(bookStream);
        //加载章节
        byte[] buffer = new byte[BUFFER_SIZE];
        //获取到的块起始点，在文件中的位置
        long curOffset = 0;
        //block的个数
        int blockPos = 0;
        //读取的长度
        int length;

        //获取文件中的数据到buffer，直到没有数据为止
        while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
            ++blockPos;
            //如果存在Chapter
            if (hasChapter) {
                //将数据转换成String
                String blockContent = new String(buffer, 0, length, mCharset.getName());
                //当前Block下使过的String的指针
                int seekPos = 0;
                //进行正则匹配
                Matcher matcher = mChapterPattern.matcher(blockContent);
                //如果存在相应章节
                while (matcher.find()) {
                    //获取匹配到的字符在字符串中的起始位置
                    int chapterStart = matcher.start();

                    //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                    //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                    if (seekPos == 0 && chapterStart != 0) {
                        //获取当前章节的内容
                        String chapterContent = blockContent.substring(seekPos, chapterStart);
                        //设置指针偏移
                        seekPos += chapterContent.length();

                        //如果当前对整个文件的偏移位置为0的话，那么就是序章
                        if (curOffset == 0) {
                            //创建序章
                            TxtChapter preChapter = new TxtChapter();
                            preChapter.title = "序章";
                            preChapter.start = 0;
                            preChapter.end = chapterContent.getBytes(mCharset.getName()).length; //获取String的byte值,作为最终值

                            //如果序章大小大于30才添加进去
                            if (preChapter.end - preChapter.start > 30) {
                                chapters.add(preChapter);
                            }

                            //创建当前章节
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = preChapter.end;
                            chapters.add(curChapter);
                        }
                        //否则就block分割之后，上一个章节的剩余内容
                        else {
                            //获取上一章节
                            TxtChapter lastChapter = chapters.get(chapters.size() - 1);
                            //将当前段落添加上一章去
                            lastChapter.end += chapterContent.getBytes(mCharset.getName()).length;

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter);
                            }

                            //创建当前章节
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = lastChapter.end;
                            chapters.add(curChapter);
                        }
                    } else {
                        //是否存在章节
                        if (chapters.size() != 0) {
                            //获取章节内容
                            String chapterContent = blockContent.substring(seekPos, matcher.start());
                            seekPos += chapterContent.length();

                            //获取上一章节
                            TxtChapter lastChapter = chapters.get(chapters.size() - 1);
                            lastChapter.end = lastChapter.start + chapterContent.getBytes(mCharset.getName()).length;

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter);
                            }

                            //创建当前章节
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = lastChapter.end;
                            chapters.add(curChapter);
                        }
                        //如果章节不存在则创建章节
                        else {
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = 0;
                            chapters.add(curChapter);
                        }
                    }
                }
            }
            //进行本地虚拟分章
            else {
                //章节在buffer的偏移量
                int chapterOffset = 0;
                //当前剩余可分配的长度
                int strLength = length;
                //分章的位置
                int chapterPos = 0;

                while (strLength > 0) {
                    ++chapterPos;
                    //是否长度超过一章
                    if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                        //在buffer中一章的终止点
                        int end = length;
                        //寻找换行符作为终止点
                        for (int i = chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER; i < length; ++i) {
                            if (buffer[i] == Charset.BLANK) {
                                end = i;
                                break;
                            }
                        }
                        TxtChapter chapter = new TxtChapter();
                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
                        chapter.start = curOffset + chapterOffset + 1;
                        chapter.end = curOffset + end;
                        chapters.add(chapter);
                        //减去已经被分配的长度
                        strLength = strLength - (end - chapterOffset);
                        //设置偏移的位置
                        chapterOffset = end;
                    } else {
                        TxtChapter chapter = new TxtChapter();
                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
//                        chapter.start = curOffset + chapterOffset + 1;
                        chapter.start = curOffset + chapterOffset;
                        chapter.end = curOffset + length;
                        chapters.add(chapter);
                        strLength = 0;
                    }
                }
            }

            //block的偏移点
            curOffset += length;

            if (hasChapter) {
                //设置上一章的结尾
                TxtChapter lastChapter = chapters.get(chapters.size() - 1);
                lastChapter.end = curOffset;
            }

            //当添加的block太多的时候，执行GC
            if (blockPos % 15 == 0) {
                System.gc();
                System.runFinalization();
            }
        }

        mChapterList = chapters;
        IOUtils.close(bookStream);

        System.gc();
        System.runFinalization();
    }

    @Override
    protected List<TxtPage> loadPageList(int chapterPos) {
        if (mChapterList == null) {
            throw new IllegalArgumentException("Chapter list must not null");
        }

        TxtChapter chapter = mChapterList.get(chapterPos);
        //从文件中获取数据
        byte[] content = getChapterContent(chapter);
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(bais, mCharset.getName()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return loadPages(chapter, br);
    }

    /**
     * 从文件中提取一章的内容
     *
     * @param chapter
     * @return
     */
    private byte[] getChapterContent(TxtChapter chapter) {
        RandomAccessFile bookStream = null;
        try {
//            bookStream = new RandomAccessFile(mCollBook.getYellowId(), "r");
            bookStream =  getRandomFile(mCollBook.getYellowId());
            bookStream.seek(chapter.start);
            int extent = (int) (chapter.end - chapter.start);
            byte[] content = new byte[extent];
            bookStream.read(content, 0, extent);
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(bookStream);
        }

        return new byte[0];
    }

    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    private boolean checkChapterType(RandomAccessFile bookStream) throws IOException {
        //首先获取128k的数据
        byte[] buffer = new byte[BUFFER_SIZE / 4];
        int length = bookStream.read(buffer, 0, buffer.length);
        //进行章节匹配
        for (String str : CHAPTER_PATTERNS) {
            Pattern pattern = Pattern.compile(str, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(new String(buffer, 0, length, mCharset.getName()));
            //如果匹配存在，那么就表示当前章节使用这种匹配方式
            if (matcher.find()) {
                mChapterPattern = pattern;
                //重置指针位置
                bookStream.seek(0);
                return true;
            }
        }

        //重置指针位置
        bookStream.seek(0);
        return false;
    }

    @Override
    boolean prevChapter() {
        if (mStatus == STATUS_PARSE_ERROR) return false;
        return super.prevChapter();
    }

    @Override
    boolean nextChapter() {
        if (mStatus == STATUS_PARSE_ERROR) return false;
        return super.nextChapter();
    }

    @Override
    public void skipToChapter(int pos) {
        super.skipToChapter(pos);
        //加载章节
        openChapter();
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener(listener);
        //额，写的不太优雅，之后再改
        if (mChapterList != null && mChapterList.size() != 0) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }
    }

    /*空实现*/
    @Override
    public void setChapterList(List<BookChapterBean> bookChapters) {
    }

    @Override
    public void saveRecord() {
        super.saveRecord();
        //修改当前COllBook记录
        if (mCollBook != null && isBookOpen) {
            //表示当前CollBook已经阅读
            mCollBook.setUpdate(false);
            mCollBook.setLastChapter(mChapterList.get(mCurChapterPos).getTitle());
            mCollBook.setLastRead(StringUtils.
                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            //直接更新
            YellowCollBookHelper.getsInstance().saveBook(mCollBook);
        }
    }

    @Override
    public void closeBook() {
        super.closeBook();
        if (mChapterDisp != null) {
            mChapterDisp.dispose();
            mChapterDisp = null;
        }
    }

    public RandomAccessFile getRandomFile(String filePath){
        RandomAccessFile accessFile = null;
        try {
            String isFileExist = AppFileUtils.getInstance().isExistOrCreate(filePath);
            if(TextUtils.isEmpty(isFileExist)){
                Log.e(TAG, "getRandomFile: 没创建成功或没找到文件:"+isFileExist );
                return null;
            }
//            File file = new File(getContext().getFilesDir(),java.lang.System.currentTimeMillis() + ".txt");
            accessFile = new RandomAccessFile(isFileExist, "rwd");
//            InputStream is = AssetsUtils.getInstance().open("test.txt");
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            while((len = is.read(buffer))!=-1){
//                accessFile.write(buffer,0,len);
//            }
//            accessFile.close();
//            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessFile;
    }
}
