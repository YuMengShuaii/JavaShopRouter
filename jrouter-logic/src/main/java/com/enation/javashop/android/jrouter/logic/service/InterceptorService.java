package com.enation.javashop.android.jrouter.logic.service;

import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.listener.InterceptorListener;
import com.enation.javashop.android.jrouter.logic.template.BaseProvider;

/**
 *  拦截器处理接口
 */

public interface InterceptorService extends BaseProvider {
    void doInterceptions(Postcard postcard, InterceptorListener callback);
}
