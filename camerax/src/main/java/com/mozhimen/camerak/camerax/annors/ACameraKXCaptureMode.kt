package com.mozhimen.camerak.camerax.annors

import android.annotation.SuppressLint
import androidx.annotation.IntDef
import androidx.camera.core.ImageCapture
import com.mozhimen.libk.jetpack.camera.cons.CImageCapture

/**
 * @ClassName ACameraKXCaptureMode
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/9/13 1:51
 * @Version 1.0
 */
@SuppressLint("UnsafeOptInUsageError")
@IntDef(CImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY, CImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY, CImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG)
annotation class ACameraKXCaptureMode
