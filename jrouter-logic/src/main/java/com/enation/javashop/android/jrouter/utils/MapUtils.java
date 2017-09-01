package com.enation.javashop.android.jrouter.utils;

import java.util.Map;

/**
 * Map工具类
 */

public class MapUtils {
    /**
     * 非空
     * @param map  map对象
     * @return     是否非空
     */
    public static boolean isNotEmpty(final Map<?,?> map) {
        return !isEmpty(map);
    }

    /**
     * 判空
     * @param map map对象
     * @return    是否为空
     */
    public static boolean isEmpty(final Map<?,?> map) {
        return map == null || map.isEmpty();
    }

}
