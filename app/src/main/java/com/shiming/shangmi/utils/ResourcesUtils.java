package com.shiming.shangmi.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.shiming.hement.HementApplication;

/**
 * Created by zhicheng.liu on 2018/3/26
 * address :liuzhicheng@sunmi.com
 * description :
 */

public class ResourcesUtils {
    @NonNull
    public static String getString(Context context, @StringRes int id){
        if(context==null){
            return HementApplication.getInstance().getResources().getString(id);
        }
        return context.getResources().getString(id);
    }
    @NonNull
    public static String getString(@StringRes int id){
        return getString(null,id);
    }
}
