package com.enation.javashop.android.jrouter.logic.template;

import com.enation.javashop.android.jrouter.external.model.RouterModel;

import java.util.Map;

/**
 * 加载一个模块下所有Provider假哭
 */

public interface BaseProviderModule {
    void loadInto(Map<String, RouterModel> providers);
}
