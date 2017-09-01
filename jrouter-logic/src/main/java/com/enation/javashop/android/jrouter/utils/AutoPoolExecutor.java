package com.enation.javashop.android.jrouter.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池
 */

public class AutoPoolExecutor extends ThreadPoolExecutor {

    /**
     * Cpu核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 初始并行线程数
     */
    private static final int INIT_THREAD_COUNT = CPU_COUNT + 1;
    /**
     * 最大并行线程数
     */
    private static final int MAX_THREAD_COUNT = 20;
    /**
     * 每个线程的超时时间
     */
    private static final long LIFE_TIME = 30L;

    /**
     * 单例
     */
    private static AutoPoolExecutor instance;

    /**
     * 获取单例
     * @return  单例
     */
    public static AutoPoolExecutor getInstance(){
        if (null == instance) {
            synchronized (AutoPoolExecutor.class) {
                if (null == instance) {
                    instance = new AutoPoolExecutor(
                            INIT_THREAD_COUNT,
                            MAX_THREAD_COUNT,
                            LIFE_TIME,
                            TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(),
                            new JRouterThreadFactory());
                }
            }
        }
        return instance;
    }

    //构造
    public AutoPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }
}
