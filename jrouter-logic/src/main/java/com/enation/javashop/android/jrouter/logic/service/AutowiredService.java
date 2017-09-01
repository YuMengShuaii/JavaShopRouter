package com.enation.javashop.android.jrouter.logic.service;

import com.enation.javashop.android.jrouter.logic.template.BaseProvider;

/**
 * 参数注入接口
 */

public interface AutowiredService extends BaseProvider {
    /**
     * 初始化参数
     * @param instance
     */
    void autowire(Object instance);
}
