package com.enation.javashop.android.jrouter.logic.service;

import android.net.Uri;

import com.enation.javashop.android.jrouter.logic.template.BaseProvider;

/**
 * 路径处理接口
 */

public interface PathReplaceService extends BaseProvider {

    String forString(String path);


    Uri forUri(Uri uri);
}
