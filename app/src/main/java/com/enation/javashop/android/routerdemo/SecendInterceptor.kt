package com.enation.javashop.android.routerdemo;

import android.content.Context
import android.util.Log
import com.enation.javashop.android.jrouter.external.annotation.Interceptor;
import com.enation.javashop.android.jrouter.logic.datainfo.Postcard
import com.enation.javashop.android.jrouter.logic.listener.InterceptorListener
import com.enation.javashop.android.jrouter.logic.template.BaseInterceptor

/**
 * Created by LDD on 2017/8/30.
 */

@Interceptor(priority = 2)
class SecendInterceptor : BaseInterceptor {
    override fun init(context: Context?) {

    }

    override fun process(postcard: Postcard?, callback: InterceptorListener?) {
        Log.e("Interceptor", postcard?.path)
        callback?.onContinue(postcard)
    }
}
