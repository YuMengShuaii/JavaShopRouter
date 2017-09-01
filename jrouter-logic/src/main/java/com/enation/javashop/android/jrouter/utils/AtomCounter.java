package com.enation.javashop.android.jrouter.utils;

import java.util.concurrent.CountDownLatch;

/**
 * 线程计数器
 */

public class AtomCounter extends CountDownLatch {

    public AtomCounter(int count) {
        super(count);
    }

    /**
     * 清空计数器 并停止阻塞主线程
     */
    public void cancel() {
        while (getCount() > 0) {
            countDown();
        }
    }
}
