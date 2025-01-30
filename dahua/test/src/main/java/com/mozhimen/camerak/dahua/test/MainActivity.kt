package com.mozhimen.camerak.dahua.test

import android.Manifest
import android.os.Bundle
import com.mozhimen.bindk.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.camerak.dahua.CameraKDahua
import com.mozhimen.camerak.dahua.test.databinding.ActivityMainBinding
import com.mozhimen.kotlin.utilk.android.content.startContext
import com.mozhimen.permissionk.PermissionK
import com.mozhimen.permissionk.annors.APermissionCheck

@APermissionCheck(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
class MainActivity : BaseActivityVDB<ActivityMainBinding>() {

    override fun initData(savedInstanceState: Bundle?) {
        PermissionK.requestPermissions(this) {
            super.initData(savedInstanceState)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        CameraKDahua.instance.with("xxx").init { _, _, _ -> }

        vdb.btnIPLogin.setOnClickListener {
            startContext<IPLoginActivity>()
        }
    }

    override fun onDestroy() {
        CameraKDahua.instance.with("xxx").destroy()
        super.onDestroy()
    }
}


