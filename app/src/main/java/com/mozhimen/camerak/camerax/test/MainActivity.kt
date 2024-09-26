package com.mozhimen.camerak.camerax.test

import android.view.View
import com.mozhimen.kotlin.utilk.android.content.startContext
import com.mozhimen.camerak.camerax.test.databinding.ActivityMainBinding
import com.mozhimen.mvvmk.bases.activity.databinding.BaseActivityVDB

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/1/30 23:08
 * @Version 1.0
 */
class MainActivity : BaseActivityVDB<ActivityMainBinding>() {
    fun goCameraKUVC(view: View) {
        startContext<CameraKUVCActivity>()
    }

    fun goCameraKX(view: View) {
        startContext<CameraKXActivity>()
    }
}