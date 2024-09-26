package com.mozhimen.camerak.camerax.test

import android.app.Application
import com.mozhimen.kotlin.lintk.optins.OApiInit_InApplication
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_READ_PHONE_STATE
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_READ_PRIVILEGED_PHONE_STATE
import com.mozhimen.crashk.CrashKMgr

/**
 * @ClassName MainApplication
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Version 1.0
 */
@OptIn(OApiInit_InApplication::class)
class MainApplication : Application() {
    @OptIn(OPermission_READ_PHONE_STATE::class, OPermission_READ_PRIVILEGED_PHONE_STATE::class)
    override fun onCreate() {
        super.onCreate()
        CrashKMgr.instance.init()
    }
}