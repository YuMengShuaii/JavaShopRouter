package com.enation.javashop.android.commpont.test

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.enation.javashop.android.jrouter.JRouter
import com.enation.javashop.android.jrouter.external.annotation.Autowired
import com.enation.javashop.android.jrouter.external.annotation.Router

/**
 * 测试Moudle跳转 ACTIVITY
 */
@Router(path = "/kotlin/m2")
class KotlinActivity :Activity() {
    @Autowired(name = "obj")
    @JvmField var t :datamodel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        JRouter.prepare().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        Toast.makeText(baseContext, t?.name,Toast.LENGTH_SHORT).show()
    }
}