package com.mozhimen.camerak_gl.helpers

import com.mozhimen.camerak_gl.bases.BaseFilter

/**
 * @ClassName AFilter
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Date 2022/6/27 17:29
 * @Version 1.0
 */
abstract class AFilter : BaseFilter() {
    override var coordPos: FloatArray = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )
}