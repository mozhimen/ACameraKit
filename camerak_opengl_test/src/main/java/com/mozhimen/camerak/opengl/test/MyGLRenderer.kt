package com.mozhimen.camerak.opengl.test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Handler
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @ClassName MyGLRenderer
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/4/18 23:44
 * @Version 1.0
 */
class MyGLRenderer(context: Context) : GLSurfaceView.Renderer {
    private var _handler: Handler? = null
    private var _surfaceTexture: SurfaceTexture? = null
    private var _bitmap: Bitmap? = null

    private var _bitmapWidth = 0
    private var _bitmapHeight = 0
    private var _surfaceWidth = 0
    private var _surfaceHeight = 0

    private var _MM = FloatArray(16)
    private var _MV = FloatArray(16)
    private var _projM = FloatArray(16)
    private var _viewM = FloatArray(16)
    private var _MVPM = FloatArray(16)

    init {
        Matrix.setIdentityM(_MM, 0)
        _MM[0] = -1f


    }

    fun setHandler(handler: Handler?) {
        _handler = handler
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1f)
        _triangle = TextureMVPMatrix(_bitmap)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        _surfaceWidth = width
        _surfaceHeight = height

        calculateViewport()
        GLES30.glViewport(0, 0, width, height)
    }


    override fun onDrawFrame(gl: GL10?) {
    }

    fun calculateViewport() {
        val imageRatio = _bitmapWidth / _bitmapHeight.toFloat()
        val surfaceRatio = _surfaceWidth / _surfaceHeight.toFloat()

        if (imageRatio > surfaceRatio) {
            val tb = imageRatio / surfaceRatio
            Matrix.orthoM(_projM, 0, -1f, 1f, -tb, tb, -1f, 1f)
        } else if (imageRatio < surfaceRatio) {
            val lr = imageRatio / surfaceRatio
            Matrix.orthoM(_projM, 0, -lr, lr, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(_projM, 0, -1f, 1f, -1f, 1f, -1f, 1f)
        }

        Matrix.setLookAtM(_viewM, 0, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f, 0f)
    }
}