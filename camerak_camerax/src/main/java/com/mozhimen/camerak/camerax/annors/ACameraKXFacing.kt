package com.mozhimen.camerak.camerax.annors

import androidx.annotation.IntDef
import androidx.camera.core.CameraSelector

/**
 * @ClassName CameraXKFacing
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Version 1.0
 */
@IntDef(value = [ACameraKXFacing.FRONT, ACameraKXFacing.BACK])
annotation class ACameraKXFacing {
    companion object {
        const val FRONT = CameraSelector.LENS_FACING_FRONT
        const val BACK = CameraSelector.LENS_FACING_BACK
    }
}