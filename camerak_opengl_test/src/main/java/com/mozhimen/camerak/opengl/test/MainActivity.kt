package com.mozhimen.camerak.opengl.test

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.mozhimen.kotlin.utilk.android.content.UtilKContext
import com.mozhimen.kotlin.utilk.commons.IUtilK

class MainActivity : AppCompatActivity(), IUtilK {
    private var _handler: Handler? = null
    private var _surfaceTexture: SurfaceTexture? = null

    private var _myGLSurfaceView: MyGLSurfaceView? = null
    private var _myGLRenderer: MyGLRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (_myGLSurfaceView == null) {
            _myGLSurfaceView = MyGLSurfaceView(this)
            setContentView(_myGLSurfaceView)

            _myGLRenderer = MyGLRenderer(this)
            _myGLSurfaceView?.setRenderer(_myGLRenderer)
            _myGLSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }

        if (_handler == null) {
            _handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        1 -> {
                            val texture = msg.obj as SurfaceTexture
                            _surfaceTexture = texture
                        }

                        else -> {
                            Log.w(TAG, "handleMessage: ${msg.what}")
                        }
                    }
                }
            }
        }
    }

    private var _cameraDevice: CameraDevice? = null
    private var _cameraCaptureSession: CameraCaptureSession? = null

    private fun captureVideo() {
        _cameraDevice ?: return
        val requestBuilder = _cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surfaceTexture = SurfaceTexture(0)
        val surface = Surface(surfaceTexture)
        val outputs = listOf(surface)
        _cameraDevice!!.createCaptureSession(outputs, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                _cameraCaptureSession = session
                //capture
                session.setRepeatingRequest(requestBuilder.build(), null, null)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
            }
        }, null)
    }

    @SuppressLint("MissingPermission")
    private fun startCamera() {
        val cameraManager = UtilKContext.getCameraManager(this)
        val cameraId = cameraManager.cameraIdList[0]
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                _cameraDevice = camera
                _myGLRenderer?.setHandler(_handler)
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
            }
        }, null)
    }
}