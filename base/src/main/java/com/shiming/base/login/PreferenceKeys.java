package com.shiming.base.login;


/**
 *  Created by shiming on 16/11/19.
 */

public enum PreferenceKeys {

    APP_DEFAULT("app_default"),
    USER_INFO("user_info"),
    //是否加载了视屏到sd中
    IS_MOIVE("is_moive"),
    TEXT("text");

    private String value;

    PreferenceKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PreferenceKeys typeOfValue(String value) {
        for (PreferenceKeys e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return APP_DEFAULT;
    }
}
