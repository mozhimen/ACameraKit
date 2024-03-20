package com.mozhimen.camerak.camerax.test

import com.mozhimen.basick.elemk.android.app.bases.BaseApplication
import com.mozhimen.basick.lintk.optins.OApiInit_InApplication
import com.mozhimen.basick.lintk.optins.OApiMultiDex_InApplication
import com.mozhimen.basick.lintk.optins.permission.OPermission_READ_PHONE_STATE
import com.mozhimen.basick.lintk.optins.permission.OPermission_READ_PRIVILEGED_PHONE_STATE
import com.mozhimen.crashk.CrashKMgr

/**
 * @ClassName MainApplication
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/2/16 11:43
 * @Version 1.0
 */
@OptIn(OApiMultiDex_InApplication::class, OApiInit_InApplication::class)
class MainApplication:BaseApplication() {
    @OptIn(OPermission_READ_PHONE_STATE::class, OPermission_READ_PRIVILEGED_PHONE_STATE::class)
    override fun onCreate() {
        super.onCreate()
        CrashKMgr.instance.init()
    }
}