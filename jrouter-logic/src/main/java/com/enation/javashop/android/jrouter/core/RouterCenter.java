package com.enation.javashop.android.jrouter.core;

import android.content.Context;
import android.net.Uri;
import com.enation.javashop.android.jrouter.JRouter;
import com.enation.javashop.android.jrouter.exception.JRouterException;
import com.enation.javashop.android.jrouter.exception.RouterPathNorFoundException;
import com.enation.javashop.android.jrouter.external.enums.ValueType;
import com.enation.javashop.android.jrouter.external.model.RouterModel;
import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.template.BaseInterceptorModule;
import com.enation.javashop.android.jrouter.logic.template.BaseProvider;
import com.enation.javashop.android.jrouter.logic.template.BaseProviderModule;
import com.enation.javashop.android.jrouter.logic.template.BaseRouteModule;
import com.enation.javashop.android.jrouter.logic.template.BaseRouteRoot;
import com.enation.javashop.android.jrouter.utils.ClassUtils;
import com.enation.javashop.android.jrouter.utils.Constant;
import com.enation.javashop.android.jrouter.utils.MapUtils;
import com.enation.javashop.android.jrouter.utils.TextUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import static com.enation.javashop.android.jrouter.JRouter.logger;
import static com.enation.javashop.android.jrouter.utils.Constant.DOT;
import static com.enation.javashop.android.jrouter.utils.Constant.LOG_TAG;
import static com.enation.javashop.android.jrouter.utils.Constant.ROUTE_ROOT_PAKCAGE;
import static com.enation.javashop.android.jrouter.utils.Constant.SDK_NAME;
import static com.enation.javashop.android.jrouter.utils.Constant.SEPARATOR;
import static com.enation.javashop.android.jrouter.utils.Constant.SUFFIX_INTERCEPTORS;
import static com.enation.javashop.android.jrouter.utils.Constant.SUFFIX_PROVIDERS;
import static com.enation.javashop.android.jrouter.utils.Constant.SUFFIX_ROOT;

/**
 * JRouter初始化核心类
 */

public class RouterCenter {
    /***
     * 上下文
     */
    private static Context mContext;

    /**
     * 线程池
     */
    static ThreadPoolExecutor executor;

    /**
     * 初始化 在守护线程加载所有的数据
     */
    public synchronized static void init(Context context, ThreadPoolExecutor tpe) throws JRouterException {
        mContext = context;
        executor = tpe;

        try {
            /**获取Router注解处理包下所有class*/
            List<String> classFileNames = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);

            /**循环初始化*/
            for (String className : classFileNames) {
                    /**判断是否是Root 是的话执行loadinfo*/
                if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                    ((BaseRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto(JRouterHouse.groupsIndex);
                    /**判断是否是拦截器 是的话执行loadinfo*/
                } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTORS)) {
                    ((BaseInterceptorModule) (Class.forName(className).getConstructor().newInstance())).loadInto(JRouterHouse.interceptorsIndex);
                    /**判断是否是Provider 是的话执行loadinfo*/
                } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_PROVIDERS)) {
                    ((BaseProviderModule) (Class.forName(className).getConstructor().newInstance())).loadInto(JRouterHouse.providersIndex);
                }
            }

            /**判断组集合是否为空 为空的话打印提示*/
            if (JRouterHouse.groupsIndex.size() == 0) {
                logger.e(LOG_TAG, "没有需要初始化的类，请检查配置!");
            }
            /**检查是否开启Debug模式 开启Debug模式打印相关信息*/
            if (JRouter.debuggable()) {
                logger.d(LOG_TAG, String.format(Locale.getDefault(), "初始化完毕, GroupIndex[%d], 拦截器[%d]个, 内容提供者[%d]个", JRouterHouse.groupsIndex.size(), JRouterHouse.interceptorsIndex.size(), JRouterHouse.providersIndex.size()));
            }
        } catch (Exception e) {
            throw new JRouterException(LOG_TAG + "JRouter初始化异常 [" + e.getMessage() + "]");
        }
    }

    /**
     * 初始化Provider
     * @param serviceName 根据服务名获取服务路径
     * @return  构造完成的Postcard
     */
    public static Postcard buildProvider(String serviceName) {
        RouterModel meta = JRouterHouse.providersIndex.get(serviceName);
        if (null == meta) {
            return null;
        } else {
            return new Postcard(meta.getPath(), meta.getGroup());
        }
    }

    /**
     * 操作postcard
     * @param postcard
     */
    public synchronized static void completion(Postcard postcard) {
        /**判断postcard是否为空 为空抛出异常*/
        if (null == postcard) {
            throw new RouterPathNorFoundException(LOG_TAG + "postcard is null");
        }

        /**根据path获取router*/
        RouterModel routeMeta = JRouterHouse.routes.get(postcard.getPath());
        /**判断获取的router数据是否为空*/
        if (null == routeMeta) {
            /**如果router为空，查看router的组class是否为空*/
            Class<? extends BaseRouteModule> groupMeta = JRouterHouse.groupsIndex.get(postcard.getGroup());  // Load route meta.
            /**组class也为空 抛出异常*/
            if (null == groupMeta) {
                throw new RouterPathNorFoundException(LOG_TAG + "找不到该路径： [" + postcard.getPath() + "], 组： [" + postcard.getGroup() + "]");
            } else {
                /**懒加载 用到一个组加在一个组*/
                try {
                    if (JRouter.debuggable()) {
                        logger.d(LOG_TAG, String.format(Locale.getDefault(), "找不到路径 重新加载组 [%s] , 路径： [%s]", postcard.getGroup(), postcard.getPath()));
                    }
                    /**获取组class示例*/
                    BaseRouteModule iGroupInstance = groupMeta.getConstructor().newInstance();
                    /**初始化组内数据*/
                    iGroupInstance.loadInto(JRouterHouse.routes);
                    /**移除该组 防止递归进入死循环 如果执行完依然查找不到 直接在上一步抛出异常*/
                    JRouterHouse.groupsIndex.remove(postcard.getGroup());
                    if (JRouter.debuggable()) {
                        logger.d(LOG_TAG, String.format(Locale.getDefault(), " [%s] 初始化, path： [%s]", postcard.getGroup(), postcard.getPath()));
                    }
                } catch (Exception e) {
                    throw new JRouterException(LOG_TAG + "找不到目标数据 [" + e.getMessage() + "]");
                }
                /**重新载入*/
                completion(postcard);
            }
        } else {
            /**放入clas*/
            postcard.setDestination(routeMeta.getDestination());
            /**放入类型*/
            postcard.setType(routeMeta.getType());
            /**放入优先级*/
            postcard.setPriority(routeMeta.getPriority());
            /**放入附加数据*/
            postcard.setExtra(routeMeta.getExtra());
            /**获取uri*/
            Uri rawUri = postcard.getUri();
            /**uri不为空 处理uri*/
            if (null != rawUri) {
                /**获取uri拼接的参数*/
                Map<String, String> resultMap = TextUtils.splitQueryParameters(rawUri);
                /**获取参数类型*/
                Map<String, Integer> paramsType = routeMeta.getParamsType();
                /**判断参数类型是否为空*/
                if (MapUtils.isNotEmpty(paramsType)) {
                    /**不为空循环赋值*/
                    // Set value by its type, just for params which annotation by @Param
                    for (Map.Entry<String, Integer> params : paramsType.entrySet()) {
                        setValue(postcard,
                                params.getValue(),
                                params.getKey(),
                                resultMap.get(params.getKey()));
                    }

                    /**保存至card*/
                    postcard.getExtras().putStringArray(JRouter.AUTO_INJECT, paramsType.keySet().toArray(new String[]{}));
                }

                /**保存Uri*/
                postcard.withString(JRouter.RAW_URI, rawUri.toString());
            }
            /**routemeta不为空 也没有uri时 进行类型判断*/
            switch (routeMeta.getType()) {
                /**为Provider*/
                case PROVIDER:
                    /**获取provider class*/
                    Class<? extends BaseProvider> providerMeta = (Class<? extends BaseProvider>) routeMeta.getDestination();
                    /**缓存中获取provider实例*/
                    BaseProvider instance = JRouterHouse.providers.get(providerMeta);
                    /**provider缓存中是否存在*/
                    if (null == instance) {
                        /**缓存中不存在时*/
                        BaseProvider provider;
                        try {
                            /**反射获取实例*/
                            provider = providerMeta.getConstructor().newInstance();
                            /**初始化*/
                            provider.init(mContext);
                            /**放入缓存*/
                            JRouterHouse.providers.put(providerMeta, provider);
                            /**赋值*/
                            instance = provider;
                        } catch (Exception e) {
                            throw new JRouterException("Init provider failed! " + e.getMessage());
                        }
                    }
                    /**赋值*/
                    postcard.setProvider(instance);
                    /**provider无需使用拦截器 故开启绿色通道*/
                    postcard.greenChannel();
                    break;
                case FRAGMENT:
                    /**fragment无需使用拦截器 故开启绿色通道*/
                    postcard.greenChannel();
                default:
                    break;
            }
        }
    }

    /**
     * 设置Value 并制定类型
     *
     * @param postcard postcard  数据封装
     * @param typeDef  type      类型
     * @param key      key       key
     * @param value    value     value
     */
    private static void setValue(Postcard postcard, Integer typeDef, String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }

        try {
            if (null != typeDef) {
                if (typeDef == ValueType.BOOLEAN.ordinal()) {
                    postcard.withBoolean(key, Boolean.parseBoolean(value));
                } else if (typeDef == ValueType.BYTE.ordinal()) {
                    postcard.withByte(key, Byte.valueOf(value));
                } else if (typeDef == ValueType.SHORT.ordinal()) {
                    postcard.withShort(key, Short.valueOf(value));
                } else if (typeDef == ValueType.INT.ordinal()) {
                    postcard.withInt(key, Integer.valueOf(value));
                } else if (typeDef == ValueType.LONG.ordinal()) {
                    postcard.withLong(key, Long.valueOf(value));
                } else if (typeDef == ValueType.FLOAT.ordinal()) {
                    postcard.withFloat(key, Float.valueOf(value));
                } else if (typeDef == ValueType.DOUBLE.ordinal()) {
                    postcard.withDouble(key, Double.valueOf(value));
                } else if (typeDef == ValueType.STRING.ordinal()) {
                    postcard.withString(key, value);
                } else if (typeDef == ValueType.PARCELABLE.ordinal()) {
                    // TODO : How to description parcelable value with string?
                } else if (typeDef == ValueType.OBJECT.ordinal()) {
                    postcard.withString(key, value);
                } else {    // Compatible compiler sdk 1.0.3, in that version, the string type = 18
                    postcard.withString(key, value);
                }
            } else {
                postcard.withString(key, value);
            }
        } catch (Throwable ex) {
            logger.e(Constant.LOG_TAG, "RouterCenter setValue failed! " + ex.getMessage());
        }
    }

    /**
     * JRouter停止服务
     */
    public static void suspend() {
        JRouterHouse.clear();
    }
}
