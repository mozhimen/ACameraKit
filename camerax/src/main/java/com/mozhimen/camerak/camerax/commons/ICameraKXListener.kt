package com.mozhimen.camerak.camerax.commons

/**
 * @ClassName CameraXKListener
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Version 1.0
 */
interface ICameraKXListener {
    fun onCameraStartFail(e: String){}
    fun onCameraFlashOn(){}
    fun onCameraFlashAuto(){}
    fun onCameraFlashOff(){}
    fun onCameraHDRCheck(available: Boolean){}
    fun onCameraHDROpen(){}
}