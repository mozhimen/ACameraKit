package com.mozhimen.camerak_gl.helpers

import com.mozhimen.camerak_gl.bases.BaseFilter

/**
 * @ClassName CameraAFilter
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Date 2022/6/27 15:58
 * @Version 1.0
 */
abstract class FrameAFilter : BaseFilter() {
    override var coordPos: FloatArray = floatArrayOf(
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
    )
}