package com.mozhimen.camerak.camerax.annors

import androidx.annotation.IntDef

/**
 * @ClassName CameraXKFormat
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Version 1.0
 */
@IntDef(value = [ACameraKXFormat.RGBA_8888, ACameraKXFormat.YUV_420_888])
annotation class ACameraKXFormat {
    companion object {
        const val RGBA_8888 = 0
        const val YUV_420_888 = 1
    }
}
