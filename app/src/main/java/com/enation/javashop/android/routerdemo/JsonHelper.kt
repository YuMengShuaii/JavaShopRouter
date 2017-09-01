package com.enation.javashop.android.routerdemo

import com.google.gson.Gson

/**
 * Json辅助类
 */
object JsonHelper {

    private var gson = Gson()

    fun toJson(instance:Any):String{
        return gson.toJson(instance)
    }


    fun <T> toObject(json : String,cls:Class<T>):T{
        return gson.fromJson(json,cls)
    }

}