package com.enation.javashop.android.jrouter.logic.listener;

import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;

/**
 *  导航监听
 */

public interface NavigationListener {

    /**
     * 找到了目标类
     * @param postcard 数据
     */
    void onFound(Postcard postcard);

    /**
     * 跳转失败
     *
     * @param postcard meta
     */
    void onLost(Postcard postcard);

    /**
     * 跳转完成
     *
     * @param postcard meta
     */
    void onArrival(Postcard postcard);

    /**
     * 被拦截
     *
     * @param postcard meta
     */
    void onInterrupt(Postcard postcard);
}
