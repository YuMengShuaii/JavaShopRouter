package com.enation.javashop.android.routerdemo;

import android.content.Context;
import com.enation.javashop.android.jrouter.external.annotation.Router;
import com.enation.javashop.android.jrouter.logic.service.JsonTransforService;
import com.google.gson.Gson

/**
 * Created by LDD on 2017/8/29.
 */
@Router(path = "/plugin/jsonService")
class JsonOrObject : JsonTransforService {
    override fun init(context: Context?) {

    }

    override fun <T : Any?> json2Object(json: String, clazz: Class<T>): T {

        return JsonHelper.toObject(json,clazz)
    }

    override fun object2Json(instance: Any): String {
        return JsonHelper.toJson(instance)
    }
}
