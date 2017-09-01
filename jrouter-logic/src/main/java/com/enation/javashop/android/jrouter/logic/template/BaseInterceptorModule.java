package com.enation.javashop.android.jrouter.logic.template;

import java.util.Map;

/**
 *  加载一个模块中所有拦截器 接口
 */

public interface BaseInterceptorModule {

    void loadInto(Map<Integer, Class<? extends BaseInterceptor>> interceptor);
}
