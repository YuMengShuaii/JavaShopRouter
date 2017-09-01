package com.enation.javashop.android.jrouter.core;

import android.content.Context;
import android.util.LruCache;

import com.enation.javashop.android.jrouter.external.annotation.Router;
import com.enation.javashop.android.jrouter.logic.service.AutowiredService;
import com.enation.javashop.android.jrouter.logic.template.BaseSyringe;

import java.util.ArrayList;
import java.util.List;

import static com.enation.javashop.android.jrouter.utils.Constant.SUFFIX_AUTOWIRED;

/**
 * 内置参数注入服务
 * @author LDD
 */
@Router(path = "/jrouter/service/autowired")
public class JRouterAutowiredService implements AutowiredService {
    /**
     *   参数注入辅助类缓存
     */
    private LruCache<String, BaseSyringe> classCache;
    /**
     *   参数注入异常的类名集合
     */
    private List<String> blackList;

    @Override
    public void init(Context context) {
        classCache = new LruCache<>(66);
        blackList = new ArrayList<>();
    }

    /**
     * 执行参数注入
     * @param instance  需要注入的对象
     */
    @Override
    public void autowire(Object instance) {
        /**类名*/
        String className = instance.getClass().getName();
        try {
            /**检查异常列表中是否存在*/
            if (!blackList.contains(className)) {
                /**先从缓存中取*/
                BaseSyringe autowiredHelper = classCache.get(className);
                if (null == autowiredHelper) {
                    /**没有缓存则反射获取*/
                    autowiredHelper = (BaseSyringe) Class.forName(instance.getClass().getName() + SUFFIX_AUTOWIRED).getConstructor().newInstance();
                    /**缓存*/
                    classCache.put(className, autowiredHelper);
                }
                /**执行注入方法*/
                autowiredHelper.inject(instance);
            }
        } catch (Exception ex) {
            /**发生异常，加入异常集合*/
            blackList.add(className);    // This instance need not autowired.
        }
    }
}
