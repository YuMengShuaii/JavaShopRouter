package com.enation.javashop.android.jrouter.logic.listener;

import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;

/**
 * 实现导航监听接口  子类可以随意重写方法
 */

public abstract class NavListener implements NavigationListener {
    @Override
    public void onFound(Postcard postcard) {

    }

    @Override
    public void onLost(Postcard postcard) {

    }

    @Override
    public void onArrival(Postcard postcard) {

    }

    @Override
    public void onInterrupt(Postcard postcard) {

    }
}
