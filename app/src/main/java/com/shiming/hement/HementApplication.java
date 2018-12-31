package com.shiming.hement;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;


import com.shiming.base.BaseApplication;
import com.shiming.base.BuildConfig;

import com.shiming.base.login.PreferenceKeys;
import com.shiming.base.ui.QMUISwipeBackActivityManager;
import com.shiming.hement.data.DataManager;
import com.shiming.hement.ui.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.shiming.base.login.PreferenceFileNames.APP_CONFIG;

/**
 * <p>
 *
 * </p>
 *
 * @author shiming
 * @version v1.0
 * @since 2018/11/28 11:31
 */

public class HementApplication extends BaseApplication {

    public static String TAG="HementApplication";
    private static DataManager mDataManager;
    private Boolean mIsMovie;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        QMUISwipeBackActivityManager.init(this);
        mDataManager = new DataManager(this);
        Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) {
                       initMovie();
                    }
                }
        ).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe();

    }
    private void initMovie() {
        Boolean mIsMovie = mDataManager.getPreferencesHelper().getBoolean(APP_CONFIG, PreferenceKeys.IS_MOIVE);
        Timber.tag(TAG).i("mkdirs 是否应该初始化视屏=%s",!mIsMovie+"");
//        if (!mIsMovie){
            //获取的手机存储路径是/storage/emulated/0
            AssetManager assetManager = getAssets();
            InputStream inputStream = null;
            FileOutputStream fos = null;
            try {
                String fileNames[] = assetManager.list("video");
                String rootPath = Environment.getExternalStorageDirectory().getPath()+"/xfxb";
                for (int i = 0; i < fileNames.length; i++) {
                    File file = new File(rootPath);
                    if (!file.exists()){
                        boolean mkdirs = file.mkdirs();
                        Timber.tag(TAG).i("mkdirs 创建文件夹成功"+mkdirs);
                    }else {
                        Timber.tag(TAG).i("文件存在");
                        continue;
                    }
                    Timber.tag(TAG).i("mkdirs fileNames  =%s",fileNames[i]);
                    inputStream = getClass().getClassLoader().getResourceAsStream("assets/video/" + fileNames[i]);
                    fos = new FileOutputStream(new File(rootPath + "/" + fileNames[i]));
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                    inputStream.close();
                    fos.close();
                }
                Timber.tag(TAG).i(Thread.currentThread().toString());
//                mDataManager.getPreferencesHelper().saveValue(APP_CONFIG, PreferenceKeys.IS_MOIVE,true);
            } catch (IOException e) {
                e.printStackTrace();
//                mDataManager.getPreferencesHelper().saveValue(APP_CONFIG, PreferenceKeys.IS_MOIVE,false);
            }
//        }
    }
    public static HementApplication get(Context context) {
        return (HementApplication) context.getApplicationContext();
    }

    public static DataManager getDataManager(){
        return mDataManager;
    }
    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets(Context context,String oldPath,String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context,oldPath + File.separator + fileName,newPath+File.separator+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
