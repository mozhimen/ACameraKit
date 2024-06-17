package com.mozhimen.camerak.camerax.annors

import androidx.annotation.IntDef
import androidx.camera.core.AspectRatio

/**
 * @ClassName AAspectRatio
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/6/16 22:45
 * @Version 1.0
 */
@IntDef(value = [AAspectRatio.RATIO_4_3, AAspectRatio.RATIO_16_9, AAspectRatio.RATIO_DEFAULT])
annotation class AAspectRatio {
    companion object {
        const val RATIO_4_3 = AspectRatio.RATIO_4_3
        const val RATIO_16_9 = AspectRatio.RATIO_16_9
        const val RATIO_DEFAULT = -1
    }
}
