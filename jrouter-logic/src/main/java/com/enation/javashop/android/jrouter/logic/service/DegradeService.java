package com.enation.javashop.android.jrouter.logic.service;

import android.content.Context;

import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.template.BaseProvider;

/**
 * 全局跳转错误处理接口
 */

public interface DegradeService extends BaseProvider {
    void onLost(Context context, Postcard postcard);
}
