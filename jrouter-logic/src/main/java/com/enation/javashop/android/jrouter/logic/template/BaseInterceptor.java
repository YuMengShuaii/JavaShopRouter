package com.enation.javashop.android.jrouter.logic.template;

import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.listener.InterceptorListener;

/**
 *  拦截器接口
 */

public interface BaseInterceptor extends BaseProvider {


    void process(Postcard postcard, InterceptorListener callback);
}