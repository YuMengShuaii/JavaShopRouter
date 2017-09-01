package com.enation.javashop.android.jrouter.logic.template;

import java.util.Map;

/**
 * 加载一个模块下所有组
 */

public interface BaseRouteRoot {

    void loadInto(Map<String, Class<? extends BaseRouteModule>> routes);

}
