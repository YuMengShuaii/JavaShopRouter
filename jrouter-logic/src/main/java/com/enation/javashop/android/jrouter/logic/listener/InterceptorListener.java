package com.enation.javashop.android.jrouter.logic.listener;

import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;

/**
 *  拦截器监听
 */

public interface InterceptorListener {

    /**
     * 跳转成功
     */
    void onContinue(Postcard postcard);


    /**
     * 处理异常
     * @param exception 异常
     */
    void onInterrupt(Throwable exception);
}
