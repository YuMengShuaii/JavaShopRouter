package com.enation.javashop.android.jrouter.utils;

import android.support.annotation.NonNull;

import com.enation.javashop.android.jrouter.JRouter;
import com.enation.javashop.android.jrouter.JRouterReal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂类
 */

public class JRouterThreadFactory implements ThreadFactory {
    /**
     * 线程池数
     */
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    /**
     * 线程数
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 线程组
     */
    private final ThreadGroup group;
    /**
     * 线程名
     */
    private final String namePrefix;

    public JRouterThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "JRouter线程池 线程编号：." + poolNumber.getAndIncrement();
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        /**初始化线程名*/
        String threadName = namePrefix + threadNumber.getAndIncrement();
        /**日志*/
        JRouter.logger.i(Constant.LOG_TAG, "JRouter启动一个线程 [" + threadName + "]");
        /**创建一个县城*/
        Thread thread = new Thread(group, runnable, threadName, 0);
        /**设置为守护线程*/
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        /**优先级设为无*/
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        /**捕获多线程处理中的异常*/
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                JRouter.logger.i(Constant.LOG_TAG, "JRouter多线程线程异常：[" + thread.getName() + "], 原因： [" + ex.getMessage() + "]");
            }
        });
        return thread;
    }
}
