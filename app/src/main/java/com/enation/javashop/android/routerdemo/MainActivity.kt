package com.enation.javashop.android.routerdemo

import android.app.Activity
import android.os.Bundle
import com.enation.javashop.android.aspect.LoginHelper
import com.enation.javashop.android.aspect.TimeLog
import com.enation.javashop.android.commpont.test.datamodel
import com.enation.javashop.android.jrouter.JRouter
import com.enation.javashop.android.jrouter.external.annotation.Router
import kotlinx.android.synthetic.main.activity_main.*

@Router(path = "/main/home")
class MainActivity : Activity() {

    @TimeLog
    override public fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //JRouter.openDebug()
        JRouter.init(application)
        //JRouter.openLog()
        setContentView(R.layout.activity_main)
        AopTest()
        LoginHelper.Login()
        textview.setOnClickListener {
            JRouter.prepare()
                    .create("/main/ss")
                    .withString("s1","你猜我猜你猜不猜")
                    .withInt("i1",1232)
                    .withObject("testObjec", datamodel("12313", 111, "aadasdad"))
                    .seek()
        }
    }

}
