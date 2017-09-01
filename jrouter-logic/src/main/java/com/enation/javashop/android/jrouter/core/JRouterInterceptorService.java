package com.enation.javashop.android.jrouter.core;

import android.content.Context;
import android.util.Log;

import com.enation.javashop.android.jrouter.exception.JRouterException;
import com.enation.javashop.android.jrouter.external.annotation.Router;
import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.listener.InterceptorListener;
import com.enation.javashop.android.jrouter.logic.service.InterceptorService;
import com.enation.javashop.android.jrouter.logic.template.BaseInterceptor;
import com.enation.javashop.android.jrouter.utils.AtomCounter;
import com.enation.javashop.android.jrouter.utils.MapUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.enation.javashop.android.jrouter.JRouter.logger;

/**
 * 自定义拦截器
 */
@Router(path = "/jrouter/service/interceptor")
public class JRouterInterceptorService implements InterceptorService {

    /**
     * 拦截器是否已初始化
     */
    private static boolean interceptorHasInit;

    /**
     * 拦截器初始化锁
     */
    private static final Object interceptorInitLock = new Object();

    /**
     * 初始化
     * @param context 上下文
     */
    @Override
    public void init(final Context context) {
        RouterCenter.executor.execute(new Runnable() {
            @Override
            public void run() {
                /**首先判断拦截器是否为空*/
                if (MapUtils.isNotEmpty(JRouterHouse.interceptorsIndex)) {
                    /**不为空 循环取出*/
                    for (Map.Entry<Integer, Class<? extends BaseInterceptor>> entry : JRouterHouse.interceptorsIndex.entrySet()) {
                        /**获取Value*/
                        Class<? extends BaseInterceptor> interceptorClass = entry.getValue();
                        try {
                            /**反射调用*/
                            BaseInterceptor iInterceptor = interceptorClass.getConstructor().newInstance();
                            /**执行拦截器初始化方法*/
                            iInterceptor.init(context);
                            /**添加到拦截器缓存内*/
                            JRouterHouse.interceptors.add(iInterceptor);
                        } catch (Exception ex) {
                            throw new JRouterException(TAG + "JRouter 拦截器初始化错误 :[" + interceptorClass.getName() + "], reason = [" + ex.getMessage() + "]");
                        }
                    }
                    /**拦截器初始化标记设置为true*/
                    interceptorHasInit = true;

                    logger.i(TAG, "JRouter拦截器初始化完毕");

                    /**同步操作  唤醒interceptorInitLock*/
                    synchronized (interceptorInitLock) {
                        interceptorInitLock.notifyAll();
                    }
                }
            }
        });
    }

    /**
     * 拦截开始
     * @param postcard   封装的数据
     * @param callback   拦截器监听
     */
    @Override
    public void doInterceptions(final Postcard postcard, final InterceptorListener callback) {
        /**判断初始化完成的拦截器集合不为空*/
        if (null != JRouterHouse.interceptors && JRouterHouse.interceptors.size() > 0) {
            /**检查初始化状态*/
            checkInterceptorsInitStatus();
            /**如果拦截器没有初始化完成 抛出异常*/
            if (!interceptorHasInit) {
                callback.onInterrupt(new JRouterException("拦截器初始化时间太长了."));
                return;
            }
            /**执行拦截操作，因为操作较复杂，放入守护线程进行操作，此处使用自定义线程池*/
            RouterCenter.executor.execute(new Runnable() {
                @Override
                public void run() {
                    /**初始化原子计数器 初始数为拦截器数量*/
                    AtomCounter interceptorCounter = new AtomCounter(JRouterHouse.interceptors.size());
                    try {
                        /**递归执行*/
                        _excute(0, interceptorCounter, postcard);
                        /**获取timeout事件 主线程阻塞timeout长的事件*/
                        interceptorCounter.await(postcard.getTimeout(), TimeUnit.SECONDS);
                        /**阻塞结束 当阻塞结束后 线程计数器依然大于0 也就是没执行完任务 抛出异常*/
                        if (interceptorCounter.getCount() > 0) {
                            callback.onInterrupt(new JRouterException("拦截超时！"));
                            /**如果postcard的tag不为空 证明tag中存在异常 抛出*/
                        } else if (null != postcard.getTag()) {    // Maybe some exception in the tag.
                            callback.onInterrupt(new JRouterException(postcard.getTag().toString()));
                        } else {
                            /**继续执行跳转操作*/
                            callback.onContinue(postcard);
                        }
                    } catch (Exception e) {
                        /**处理异常*/
                        callback.onInterrupt(e);
                    }
                }
            });
        } else {
            /**没有拦截器 直接跳转*/
            callback.onContinue(postcard);
        }
    }

    /**
     * 执行拦截操作
     * @param index    第几个拦截器
     * @param counter  原子计数器对象
     * @param postcard 封装完成的数据
     */
    private static void _excute(final int index, final AtomCounter counter, final Postcard postcard) {
        /**下标小于拦截器的数量才会执行*/
        if (index < JRouterHouse.interceptors.size()) {
            /**获取拦截器*/
            BaseInterceptor iInterceptor = JRouterHouse.interceptors.get(index);
            /**执行拦截操作*/
            iInterceptor.process(postcard, new InterceptorListener() {
                @Override
                public void onContinue(Postcard postcard) {
                    /**拦截一层 线程计数器减少一个*/
                    counter.countDown();
                    /**递归继续执行*/
                    _excute(index + 1, counter, postcard);
                }

                @Override
                public void onInterrupt(Throwable exception) {
                    /**进入异常*/
                    postcard.setTag(null == exception ? new JRouterException("拦截出现异常！") : exception.getMessage());
                    /**线程计数器清空*/
                    counter.cancel();
                }
            });
        }
    }

    /**
     * 检查拦截器初始化状态
     */
    private static void checkInterceptorsInitStatus() {
        /**设置未同步，只能有一个线程同时操作*/
        synchronized (interceptorInitLock) {
            /**循环判断拦截器初始化状态，直到初始化完毕后退出循环*/
            while (!interceptorHasInit) {
                try {
                    /**等待十秒*/
                    interceptorInitLock.wait(10 * 1000);
                } catch (InterruptedException e) {
                    /**超时异常*/
                    throw new JRouterException(TAG + "拦截器初始化超时 原因： = [" + e.getMessage() + "]");
                }
            }
        }
    }
}
