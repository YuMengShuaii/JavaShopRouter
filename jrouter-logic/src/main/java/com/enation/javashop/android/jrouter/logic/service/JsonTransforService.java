package com.enation.javashop.android.jrouter.logic.service;

import com.enation.javashop.android.jrouter.logic.template.BaseProvider;

/**
 * JsonCastObject接口
 */

public interface JsonTransforService extends BaseProvider {

    /**
     * Json转换对象
     * @param json   json
     * @param clazz  转换对象的class
     * @param <T>    对象泛型
     * @return       转换完成的对象
     */
    <T> T json2Object(String json, Class<T> clazz);


    /**
     * 对象转换为Json字符串
     * @param instance  对象
     * @return          Json字符串
     */
    String object2Json(Object instance);
}
