package com.mozhimen.camerak.camerax.test

import android.view.View
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVDB
import com.mozhimen.basick.utilk.android.content.startContext
import com.mozhimen.camerak.camerax.test.databinding.ActivityMainBinding

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