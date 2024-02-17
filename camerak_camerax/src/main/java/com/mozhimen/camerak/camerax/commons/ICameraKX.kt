package com.mozhimen.camerak.camerax.commons

import androidx.camera.core.ImageCapture
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.camerak.camerax.annors.ACameraKXFacing
import com.mozhimen.camerak.camerax.annors.ACameraKXRotation
import com.mozhimen.camerak.camerax.cons.ECameraKXTimer
import com.mozhimen.camerak.camerax.mos.CameraKXConfig

/**
 * @ClassName ICameraXKAction
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Date 2022/6/9 14:32
 * @Version 1.0
 */
interface ICameraKX {
    fun initCameraKX(owner: LifecycleOwner, config: CameraKXConfig)
    fun initCameraKX(owner: LifecycleOwner)
    fun restartCameraKX()
    fun stopCameraKX()
    fun startCapture()

    /////////////////////////////////////////////////////////////

    fun isCameraKXStart(): Boolean

    /////////////////////////////////////////////////////////////

    fun setCameraXListener(listener: ICameraKXListener)
    fun setCameraXCaptureListener(listener: ICameraKXCaptureListener)
    fun setCameraXFrameListener(listener: ICameraXKFrameListener)

    /////////////////////////////////////////////////////////////

    fun changeHdr(isOpen: Boolean)
    fun changeFlash(@ImageCapture.FlashMode flashMode: Int)
    fun changeCountDownTimer(timer: ECameraKXTimer)
    fun changeRotation(@ACameraKXRotation rotation: Int)
    fun changeFacing(@ACameraKXFacing facing: Int)
}