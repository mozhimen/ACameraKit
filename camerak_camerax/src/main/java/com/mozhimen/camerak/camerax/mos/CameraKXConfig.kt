package com.mozhimen.camerak.camerax.mos

import com.mozhimen.camerak.camerax.annors.AAspectRatio
import com.mozhimen.camerak.camerax.annors.ACameraKXCaptureMode
import com.mozhimen.camerak.camerax.annors.ACameraKXFacing
import com.mozhimen.camerak.camerax.annors.ACameraKXFormat
import com.mozhimen.camerak.camerax.annors.ACameraKXRotation
import com.mozhimen.camerak.camerax.annors.ACameraKXResolution

/**
 * @ClassName CameraXKConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
data class CameraKXConfig(
    @ACameraKXFormat val format: Int = ACameraKXFormat.YUV_420_888,
    @ACameraKXFacing val facing: Int = ACameraKXFacing.BACK,
    @ACameraKXRotation val rotation: Int = ACameraKXRotation.ROTATION_90,
    @ACameraKXCaptureMode val captureMode: Int = ACameraKXCaptureMode.MAXIMIZE_QUALITY,
    @ACameraKXResolution val resolutionWidth: Int = ACameraKXResolution.DEFAULT,
    @ACameraKXResolution val resolutionHeight: Int = ACameraKXResolution.DEFAULT,
    @AAspectRatio val aspectRatio: Int = AAspectRatio.RATIO_DEFAULT
)