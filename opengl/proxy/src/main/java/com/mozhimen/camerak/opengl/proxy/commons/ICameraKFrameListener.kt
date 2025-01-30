package com.mozhimen.camerak.opengl.proxy.commons

/**
 * @ClassName ICamera2KFrameListener
 * @Description TODO
 * @Author Kolin Zhao / Mozhimen
 * @Date 2022/6/16 12:16
 * @Version 1.0
 */
interface ICameraKFrameListener {
    fun onFrame(bytes: ByteArray, time: Long)
}