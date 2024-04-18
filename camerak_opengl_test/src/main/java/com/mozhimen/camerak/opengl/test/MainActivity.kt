package com.mozhimen.camerak.opengl.test

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.os.Bundle
import android.view.Surface
import com.mozhimen.basick.elemk.androidx.appcompat.bases.viewbinding.BaseActivityVB
import com.mozhimen.basick.utilk.android.content.UtilKContext
import com.mozhimen.camerak.opengl.test.databinding.ActivityMainBinding

class MainActivity : BaseActivityVB<ActivityMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

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
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
            }
        }, null)
    }
}