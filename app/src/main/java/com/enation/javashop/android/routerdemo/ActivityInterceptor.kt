package com.enation.javashop.android.routerdemo

import android.content.Context
import com.enation.javashop.android.jrouter.logic.datainfo.Postcard
import com.enation.javashop.android.jrouter.logic.listener.InterceptorListener
import com.enation.javashop.android.jrouter.logic.template.BaseInterceptor
import android.util.Log
import com.enation.javashop.android.jrouter.external.annotation.Interceptor


/**
 * Created by LDD on 2017/8/29.
 */
@Interceptor(priority = 1)
class ActivityInterceptor :BaseInterceptor {
    override fun init(context: Context?) {

    }

    override fun process(postcard: Postcard?, callback: InterceptorListener?) {
       Log.e("Interceptor",postcard?.path)
        callback?.onContinue(postcard)
    }
}