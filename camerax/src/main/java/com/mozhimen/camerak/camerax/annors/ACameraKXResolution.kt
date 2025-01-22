package com.mozhimen.camerak.camerax.annors

import androidx.annotation.IntRange

/**
 * @ClassName ACameraXKResolution
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/2/10 20:52
 * @Version 1.0
 */
@IntRange(from = ACameraKXResolution.DEFAULT.toLong())
annotation class ACameraKXResolution {
    companion object {
        const val DEFAULT = 0
    }
}
