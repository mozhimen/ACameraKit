package com.mozhimen.camerak.camerax.commons

import androidx.camera.core.ImageProxy
import com.mozhimen.kotlin.elemk.commons.IA_Listener

/**
 * @ClassName ICameraXKFrameListener
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2023/1/4 0:36
 * @Version 1.0
 */
typealias ICameraXKFrameListener = IA_Listener<ImageProxy>
/*
interface ICameraXKFrameListener {
    fun onFrame(image: ImageProxy)
}*/
