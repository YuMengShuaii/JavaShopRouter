package com.enation.javashop.android.jrouter.core;

import java.util.TreeMap;

/**
 * Treemap Map实现类 可以自动排序  用来存储拦截器  可以根据拦截器等级排序
 */

public class UniqueKeyTreeMap<K, V> extends TreeMap<K, V> {
    private String tipText;

    /**
     * 设置错误提示文字
     * @param exceptionText  异常提示
     */
    public UniqueKeyTreeMap(String exceptionText) {
        super();

        tipText = exceptionText;
    }

    /**
     * 赋值 且key不可重复
     * @param key    键
     * @param value  值
     * @return       值
     */
    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            throw new RuntimeException(String.format(tipText, key));
        } else {
            return super.put(key, value);
        }
    }
}
