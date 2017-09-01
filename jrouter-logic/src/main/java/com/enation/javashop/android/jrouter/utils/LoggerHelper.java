package com.enation.javashop.android.jrouter.utils;

/**
 * 日志接口
 */

public interface LoggerHelper {
    void e(String tag,String message);
    void d(String tag,String message);
    void i(String tag,String message);
    LoggerHelper openLogHelper(boolean isopen);
}
