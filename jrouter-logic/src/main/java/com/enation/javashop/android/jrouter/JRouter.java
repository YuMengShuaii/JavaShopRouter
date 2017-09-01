package com.enation.javashop.android.jrouter;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.enation.javashop.android.jrouter.exception.UnInitException;
import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.listener.NavigationListener;
import com.enation.javashop.android.jrouter.utils.Constant;
import com.enation.javashop.android.jrouter.utils.LoggerHelper;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * JRouterApi
 */

public class JRouter {
    /**
     * Uri的Key
     */
    public static final String RAW_URI = "NTeRQWvye18AkPd6G";
    /**
     * uri解析数据的key
     */
    public static final String AUTO_INJECT = "wmHzgD4lOj5o4241";

    /**
     * 日志辅助类
     */
    public static LoggerHelper logger;

    /**
     * 单例
     */
    private volatile static JRouter instance = null;

    /**
     * 是否初始化
     */
    private volatile static boolean hasInit = false;

    /**
     * 参数注入服务路径
     */
    static final String AUTOWIRED_SERVICE = "/jrouter/service/autowired";

    /**
     * 拦截器处理服务路径
     */
    static final String INTERCEPTOR_SERVCE = "/jrouter/service/interceptor";

    private JRouter() {
    }

    /**
     * 初始化JRouter
     * @param application
     */
    public static void init(Application application) {
        /**当前App生命周期内 没有执行过初始化时执行以下代码*/
        if (!hasInit) {
            /**获取Logger示例*/
            logger = JRouterReal.logger;
            logger.i(Constant.LOG_TAG, "JRouter开始初始化");
            /**初始化核心逻辑*/
            hasInit = JRouterReal.init(application);
            if (hasInit) {
                /**初始化拦截器*/
                JRouterReal.afterInit();
            }
            logger.i(Constant.LOG_TAG, "JRouter初始化完毕");
        }
    }

    /**
     * 获取单例对象 双重加锁  并判断是否已经初始化
     * @return 实例
     */
    public static JRouter prepare() {
        if (!hasInit) {
            throw new UnInitException("JRouter未初始化!");
        } else {
            if (instance == null) {
                synchronized (JRouter.class) {
                    if (instance == null) {
                        instance = new JRouter();
                    }
                }
            }
            return instance;
        }
    }

    /**
     *  开启调试模式
     */
    public static synchronized void openDebug() {
        JRouterReal.openDebug();
    }

    /**
     * 获取调试状态
     * @return
     */
    public static boolean debuggable() {
        return JRouterReal.debuggable();
    }

    /**
     * 开启日志打印
     */
    public static synchronized void openLog() {
        JRouterReal.openLog();
    }

    /**
     * 设置自定义任务线程池
     * @param tpe 线程池
     */
    public static synchronized void setExecutor(ThreadPoolExecutor tpe) {
        JRouterReal.setExecutor(tpe);
    }

    /**
     * 关闭JRouter
     */
    public synchronized void destroy() {
        JRouterReal.destroy();
        hasInit = false;
    }

    public static void setLogger(LoggerHelper userLogger) {
        JRouterReal.setLogger(userLogger);
    }

    /**
     * 注入参数
     */
    public void inject(Object thiz) {
        JRouterReal.inject(thiz);
    }

    /**
     * 构建路径
     * @param path 路径
     * @return
     */
    public Postcard create(String path) {
        return JRouterReal.getInstance().create(path);
    }

    /**
     * 根据uri构建
     * @param url uri
     * @return
     */
    public Postcard create(Uri url) {
        return JRouterReal.getInstance().create(url);
    }

    /**
     * 直接根据class 获取示例
     * @param service  服务class
     * @param <T>
     * @return
     */
    public <T> T seek(Class<? extends T> service) {
        return JRouterReal.getInstance().seek(service);
    }

    /**
     * 启动
     * @param mContext        上下文
     * @param postcard        数据内容
     * @param requestCode     返回码
     * @param callback        监听
     * @return
     */
    public Object seek(Context mContext, Postcard postcard, int requestCode, NavigationListener callback) {
        return JRouterReal.getInstance().seek(mContext, postcard, requestCode, callback);
    }
}
