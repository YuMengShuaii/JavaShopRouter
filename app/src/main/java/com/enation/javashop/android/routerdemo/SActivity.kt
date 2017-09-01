package com.enation.javashop.android.routerdemo

import android.app.Activity
import android.os.Bundle
import com.enation.javashop.android.commpont.test.datamodel
import com.enation.javashop.android.jrouter.JRouter
import com.enation.javashop.android.jrouter.external.annotation.Autowired
import com.enation.javashop.android.jrouter.external.annotation.Router
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by LDD on 2017/8/28.
 */
@Router(path = "/main/ss")
class SActivity :Activity() {

    @Autowired(name= "s1")
    @JvmField var name: String? = null

    @Autowired(name = "i1",required = true)
    @JvmField var age:  Int? = 0

    @Autowired(name ="testObjec")
    @JvmField var objec: datamodel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        JRouter.prepare().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textview.text = "asdadadadada"+name+age+objec?.age+objec?.content+objec?.name
        textview.setOnClickListener {
            (JRouter.prepare().create("/service/goods").seek() as GoodsService)
                    .todo(objec)
        }
    }
}