package com.enation.javashop.android.jrouter.core;

import com.enation.javashop.android.jrouter.external.model.RouterModel;
import com.enation.javashop.android.jrouter.logic.template.BaseInterceptor;
import com.enation.javashop.android.jrouter.logic.template.BaseProvider;
import com.enation.javashop.android.jrouter.logic.template.BaseRouteModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 整个JRouter信息的存储中心
 */

public class JRouterHouse {
    /**
     * 存储各个Moudle的Router注入类
     */
    static Map<String, Class<? extends BaseRouteModule>> groupsIndex = new HashMap<>();

    /**
     * 存储router注入类
     */
    static Map<String, RouterModel> routes = new HashMap<>();

    /**
     * 存储provider类
     */
    static Map<Class, BaseProvider> providers = new HashMap<>();
    static Map<String, RouterModel> providersIndex = new HashMap<>();

    /**
     * 存储拦截器 内部使用treemap 根据key自动排序
     */
    static Map<Integer, Class<? extends BaseInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("多个拦截器使用一个优先级 [%s]");
    static List<BaseInterceptor> interceptors = new ArrayList<>();

    /**
     * 清空所有数据
     */
    static void clear() {
        routes.clear();
        groupsIndex.clear();
        providers.clear();
        providersIndex.clear();
        interceptors.clear();
        interceptorsIndex.clear();
    }
}
