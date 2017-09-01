package com.enation.javashop.android.routerdemo

import android.content.Context
import android.util.Log
import com.enation.javashop.android.commpont.test.datamodel
import com.enation.javashop.android.jrouter.JRouter
import com.enation.javashop.android.jrouter.external.annotation.Router

/**
 * Created by LDD on 2017/8/30.
 */
@Router(path = "/service/goods")
class GoodsServiceImpl :GoodsService {
    override fun init(context: Context?) {
        Log.e("GoodsService","初始化")
    }

    override fun todo(objec:datamodel?) {
        JRouter.prepare().create("/kotlin/m2")
                .withObject("obj",objec)
                .seek()
    }
}