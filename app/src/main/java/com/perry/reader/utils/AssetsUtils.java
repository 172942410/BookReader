package com.perry.reader.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.perry.reader.model.YellowBookBean;
import com.perry.reader.utils.threadpool.ThreadPoolProxyFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AssetsUtils {

    private static final String TAG = "AssetsUtils";
    private static AssetsUtils instance;
    private static Application application;
    private AssetManager assetManager;
//    private String asstesPathStart = "file:///android_asset/";//file:///android_asset/文件名
    private Handler handler;
    private AssetsUtils(){
        handler = new Handler();
        checkInitialize();
        assetManager = application.getAssets();
    }

    public static void init(Application app) {
        application = app;
    }
    public static Context getApplicationContext() {
        checkInitialize();
        return application;
    }

    private static void checkInitialize() {
        if (application == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 RxHttpUtils.init() 初始化！");
        }
    }
    public static synchronized AssetsUtils getInstance() {
        if(instance == null) {
            instance = new AssetsUtils();
        }
        return instance;
    }

    public InputStream open(String fileName) {
//        if(fileName.startsWith(asstesPathStart)){
//            fileName = fileName.substring(asstesPathStart.length());
//        }
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }
    /**
     * 判断assets下的某个路径中是否存在 文件名
     * @param filePath assets 某个文件的绝对路径
     * @return false 不存在 ; true 存在 ;
     */
    public boolean isFileExists(String filePath){
//        if(!filePath.startsWith(asstesPathStart)){
//            return false;
//        }
        String pathSplit[]  = filePath.split("\\/");
        String fileName = pathSplit[pathSplit.length-1];
        String path = filePath.substring(0,filePath.length()-fileName.length());
        return isFileExists(path,fileName);
    }
    /**
     * 判断assets下的某个路径中是否存在 文件名
     * @param path assets 某个文件夹的路径
     * @param fileName 文件名
     * @return false 不存在 ; true 存在 ;
     */
    public boolean isFileExists (String path,String fileName){
//        if(path.startsWith(asstesPathStart)){
//            path = path.substring(asstesPathStart.length(),path.length()-1);
//        }
        if(TextUtils.isEmpty(fileName)){
            return false;
        }
        String []name = null;
        try {
             name = assetManager.list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(name != null){
            for(int i=0;i<name.length;i++){
                if(fileName.equals(name[i])){
                    return true;
                }
            }
        }
        return false;
    }
    public void readXMLTask(String fileName,ParserListener parserListener){
        if(TextUtils.isEmpty(fileName)){
            return ;
        }
        if(fileName.startsWith("file:///android_asset/") && fileName.endsWith(".xml") ){

        }else {
//            fileName = fileName+".xml";
//            fileName = "assets/yellow/"+fileName+"/"+fileName+".xml";
            fileName = "yellow/"+fileName+"/"+fileName+".xml";
//            fileName = path+"yellow/"+fileName+"/"+fileName+".xml";
        }
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(new ReadXMLTask(fileName,parserListener));
    }

    public void setParserListener(String fileName,ParserListener parserListener) {
        readXMLTask(fileName,parserListener);
    }
    public interface ParserListener {
        /**
         * 失败回调
         *
         * @param errorMsg 错误信息
         */
          void onError(String errorMsg);

        /**
         * 成功回调
         *
         * @param data 结果
         */
          void onSuccess(YellowBookBean data);
    }
    private class ReadXMLTask implements Runnable{
        String fileName;
        ParserListener parserListener;
        ReadXMLTask(String fileName){
            this.fileName = fileName;
        }
        ReadXMLTask(String fileName,ParserListener parserListener){
            this.fileName = fileName;
            this.parserListener = parserListener;
        }
        public void setParserListener(ParserListener parserListener){
            this.parserListener = parserListener;
        }
        @Override
        public void run() {
            readXML(fileName,parserListener);
        }
    }

    private void readXML(String fileName,ParserListener parserListener){
        XmlPullParser xmlParser = null;
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            if(parserListener != null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        parserListener.onError(e.getMessage());
                    }
                });
            }
        }
        xmlParser = Xml.newPullParser();
            try {
                xmlParser.setInput(inputStream,"utf-8");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                if(parserListener != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            parserListener.onError(e.getMessage());
                        }
                    });
                }
            }
        if(xmlParser != null){
            try {
                int event = xmlParser.getEventType();   //先获取当前解析器光标在哪
                YellowBookBean yellowBookBean = new YellowBookBean();
                YellowBookBean.Content content = null;
                String xmlName = "";
                while (event != XmlPullParser.END_DOCUMENT) {    //如果还没到文档的结束标志，那么就继续往下处理

                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            Log.i(TAG, "xml解析开始");
                            break;
                        case XmlPullParser.START_TAG:
                            //一般都是获取标签的属性值，所以在这里数据你需要的数据
                             xmlName = xmlParser.getName();
                            Log.d(TAG, "当前标签是：" + xmlName);
                            if (xmlName.equals("Node")) {
                                //两种方法获取属性值
                                Log.d(TAG, "第一个属性：" + xmlParser.getAttributeName(0)
                                        + ": " + xmlParser.getAttributeValue(0));
                                Log.d(TAG, "第二个属性：" + xmlParser.getAttributeName(1) + ": "
                                        + xmlParser.getAttributeValue(null, "att2"));
                            }
                            else if(xmlName.equals("contents")){
                                if( yellowBookBean.contents == null) {
                                    yellowBookBean.contents = new ArrayList<>();
                                }
                            }
                            else if(xmlName.equals("content")){
                                content =  yellowBookBean.newContent();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            String xmlText = xmlParser.getText().trim();
                            if(TextUtils.isEmpty(xmlText) || xmlText.equals("\\n")){
                                break;
                            }
                            Log.d(TAG, "Text:" + xmlText);
                            if(xmlName.equals("bookname")){
                                yellowBookBean.bookname = xmlText;
                            }
                            else if(xmlName.equals("allpages")){
                                yellowBookBean.allpages = Integer.parseInt(xmlText);
                            }
                            else if(xmlName.equals("contentcount")){
                                yellowBookBean.contentcount = Integer.parseInt(xmlText);
                            }
                            else if(xmlName.equals("bookshortname")){
                                yellowBookBean.bookshortname = xmlText;
                            }
                            else if(xmlName.equals("contents")){
//                                yellowBookBean.contents = new ArrayList<>();
                            }
                            else if(xmlName.equals("content")){
//                                if(yellowBookBean.contents != null && content != null){
//
//                                }else {
//                                    Log.e(TAG, "readXML: 解析目录异常啦" );
//                                }
                            }
                            else if(xmlName.equals("name")){
                                content.name = xmlText;
                            }
                            else if(xmlName.equals("contetindex")){
                                content.contetindex = Integer.parseInt(xmlText);
                            }
                            else if(xmlName.equals("pagecount")){
                                content.pagecount = Integer.parseInt(xmlText);
                            }
                            else if(xmlName.equals("startpage")){
//                                content.startpage = Integer.parseInt(xmlText);
                                content.startpage = xmlText;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            String endTag = xmlParser.getName();
                            Log.e(TAG, "readXML:  end tag 解析结束了:"+endTag );
                            if(endTag.equals("book")) {
                                if (parserListener != null) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            parserListener.onSuccess(yellowBookBean);
                                        }
                                    });

                                }
                            }else if( endTag.equals("content")){
                                //写入一个id吧 绝对路径吧
//                                content.id = content.startpage+""+content.pagecount+""+content.contetindex+"";
                                String temp = "";
                                if(fileName.endsWith(".xml")){
                                     String fileSplit[] = fileName.split("\\/");
                                     String last = fileSplit[fileSplit.length-1];
//                                     if(fileName.startsWith(asstesPathStart)){
//                                         temp = fileName.substring(asstesPathStart.length(), fileName.length() - last.length());
//                                     }else {
                                         temp = fileName.substring(0, fileName.length() - last.length());
//                                     }
                                }
//                                content.id = asstesPathStart+temp+"txt/"+content.startpage+".txt";
                                content.id = temp+"txt/"+content.startpage+".txt";
                            }
                            break;
                        default:
                            break;
                    }
                    event = xmlParser.next();   //将当前解析器光标往下一步移
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                if(parserListener != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            parserListener.onError(e.getMessage());
                        }
                    });
                }
            }catch (IOException e){
                e.printStackTrace();
                if(parserListener != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            parserListener.onError(e.getMessage());
                        }
                    });
                }
            }
        }
    }

}
