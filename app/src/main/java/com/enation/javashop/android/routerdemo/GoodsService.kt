package com.enation.javashop.android.routerdemo

import com.enation.javashop.android.commpont.test.datamodel
import com.enation.javashop.android.jrouter.JRouter
import com.enation.javashop.android.jrouter.logic.template.BaseProvider

/**
 * Created by LDD on 2017/8/30.
 */
interface GoodsService :BaseProvider {

    fun todo(objec: datamodel?)

}