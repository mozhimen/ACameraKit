package com.mozhimen.camerak.camerax.test

import android.app.Application
import com.mozhimen.kotlin.lintk.optins.OApiInit_InApplication
import com.mozhimen.crashk.java.CrashKJavaMgr

/**
 * @ClassName MainApplication
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Version 1.0
 */
@OptIn(OApiInit_InApplication::class)
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashKJavaMgr.instance.init()
    }
}