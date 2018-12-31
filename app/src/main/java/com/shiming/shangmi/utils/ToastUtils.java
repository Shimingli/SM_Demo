package com.shiming.shangmi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import timber.log.Timber;

/**
 * Toast工具类
 */
public class ToastUtils {

    private static Toast sToast = null;

    /**
     * 显示一个Toast
     *
     * @param context  Context
     * @param strResId 字符串资源ID
     */
    public static void toast(Context context, int strResId) {
        toast(context, strResId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示一个Toast
     *
     * @param context Context
     * @param msg     信息
     */
    public static void toast(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * 显示一个Toast
     *
     * @param context  Context
     * @param strResId 字符串资源ID
     * @param time     显示时间
     */
    public static void toast(Context context, int strResId, int time) {
        toast(context, context.getString(strResId), time);
    }

    /**
     * 显示一个Toast
     *
     * @param context Context
     * @param msg     信息
     * @param time    显示时间
     */
    @SuppressLint("ShowToast")
    public static void toast(Context context, String msg, int time) {
        if (context == null ||
                ((context instanceof Activity) &&
                        ((Activity) context).isFinishing())) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(context, msg, time);
        }
        sToast.setText(msg);
        sToast.setDuration(time);
        sToast.setGravity(Gravity.CENTER, 0, 0);
        sToast.show();
        Timber.tag("SMMainActivity").i("Toast 开始show 了 ");
    }
}
