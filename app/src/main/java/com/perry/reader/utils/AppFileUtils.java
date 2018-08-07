package com.perry.reader.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppFileUtils {

    private static final String TAG = "AppFileUtils";
    private static AppFileUtils instance;
    private static Application application;
//    private Handler handler;
    private AppFileUtils(){
//        handler = new Handler();
        checkInitialize();
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
    public static AppFileUtils getInstance() {
        if(instance == null) {
            instance = new AppFileUtils();
        }
        return instance;
    }

    public File open(String fileName) {
        if(TextUtils.isEmpty(fileName)){
            return null;
        }
        File file = null;
        if(isFileExist(fileName)) {
             file = application.getFileStreamPath(fileName);
        }else{
            downloadFromAssets(fileName);
        }
        return file;
    }
    public String isExistOrCreate(String fileName){
        if(isFileExist(fileName)){
            return fileName;
        }else{
//            long lenght = downloadFromAssets(fileName);
//            if(lenght>0){
//                return true;
//            }
            String filePath = downloadFromAssets(fileName);
            if(!TextUtils.isEmpty(filePath)){
                return filePath;
            }
        }
        return "";
    }
    /**
     * 判断 app下的某个路径中是否存在 文件名
     * @param fileName assets 某个文件的绝对路径
     * @return false 不存在 ; true 存在 ;
     */
    private boolean isFileExist(String fileName){
        if(TextUtils.isEmpty(fileName)){
            return false;
        }
        String files[] = application.fileList();
        String [] urlArray = fileName.split("/");
        if(urlArray.length>0){
            fileName = urlArray[urlArray.length-1];
        }
        if(files != null &&files.length>0){
            for (String tempFileName:files){
                if(tempFileName.equals(fileName)){
                    log("发现此文件；不用再去创建下载此文件了:"+fileName);
                    return true;
                }
            }
        }
        return false;
    }

    private void log(String msg) {
        Log.w(TAG,msg);
    }

    private String downloadFromAssets(String fileName) {
        if ( TextUtils.isEmpty(fileName)) {
            return "";
        }
        File file = null;
        String localFileName = "";
        InputStream inputStream = null;
        String[] urlArray = fileName.split("/");
        if (urlArray.length > 0) {
            localFileName = urlArray[urlArray.length - 1];
        }
        log("用到的名字是从url最后一个字段截取的：" + localFileName);
        if (!TextUtils.isEmpty(localFileName)) {
            try {
                inputStream = application.getAssets().open(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        if (inputStream == null) {
            log("本地assets 里的 文件 未找到."+fileName);
        } else {
            FileOutputStream mOutputStream = null;
            try {
                File fileDir = application.getFilesDir();
                File fileDirChild = new File(fileDir,fileName.substring(0,fileName.length()-localFileName.length()));
                if(fileDirChild.exists()){

                }else{
                    fileDirChild.mkdirs();
                }
                file = new File(fileDirChild,localFileName);
                if(file.exists()){

                }else {
                    try {
                        file.createNewFile();
                        log("文件的路径："+file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mOutputStream = new FileOutputStream(file);
//                mOutputStream = application.openFileOutput(fileName,Context. MODE_PRIVATE);
//                mOutputStream = application.openFileOutput(localFileName,Context. MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
                log("这是啥情况了");
            }
            int bytesCopied = copy(inputStream, mOutputStream);
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            if(file!= null && bytesCopied >0){
               return file.getAbsolutePath();
            }
//            return bytesCopied;
        }
        return "";
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

}
