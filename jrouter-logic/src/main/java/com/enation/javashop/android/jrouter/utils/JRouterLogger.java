package com.enation.javashop.android.jrouter.utils;

import android.util.Log;

/**
 * 日志辅助类
 */

public class JRouterLogger implements LoggerHelper {

    private boolean openFlag = false;

    @Override
    public void e(String tag, String message) {
        if (!openFlag) return;
        Log.e(tag,message);
    }

    @Override
    public void d(String tag, String message) {
        if (!openFlag) return;
        Log.d(tag,message);
    }

    @Override
    public void i(String tag, String message) {
        if (!openFlag) return;
        Log.i(tag,message);
    }

    @Override
    public LoggerHelper openLogHelper(boolean isopen) {
        openFlag = true;
        return this;
    }
}
