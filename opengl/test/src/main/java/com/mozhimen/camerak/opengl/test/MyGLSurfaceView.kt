package com.mozhimen.camerak.opengl.test

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @ClassName MyGLSurfaceView
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/4/18 23:44
 * @Version 1.0
 */
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(3)
    }
}