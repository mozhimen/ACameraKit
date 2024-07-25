package com.mozhimen.camerak.camerax.test

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.camera.core.ImageProxy
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVDB
import com.mozhimen.basick.lintk.optins.OFieldCall_Close
import com.mozhimen.basick.lintk.optins.permission.OPermission_CAMERA
import com.mozhimen.basick.utilk.android.app.UtilKActivityStart
import com.mozhimen.basick.utilk.android.view.applyVisible
import com.mozhimen.camerak.camerax.annors.AAspectRatio
import com.mozhimen.camerak.camerax.test.databinding.ActivityCameraxkBinding
import com.mozhimen.camerak.camerax.annors.ACameraKXFacing
import com.mozhimen.camerak.camerax.annors.ACameraKXFormat
import com.mozhimen.camerak.camerax.commons.ICameraKXCaptureListener
import com.mozhimen.camerak.camerax.commons.ICameraXKFrameListener
import com.mozhimen.camerak.camerax.mos.CameraKXConfig
import com.mozhimen.camerak.camerax.utils.imageProxyRgba88882bitmapRgba8888
import com.mozhimen.camerak.camerax.utils.imageProxyYuv4208882bitmapJpeg
import com.mozhimen.manifestk.xxpermissions.XXPermissionsCheckUtil
import com.mozhimen.manifestk.xxpermissions.XXPermissionsRequestUtil

@OptIn(OPermission_CAMERA::class)
class CameraKXActivity : BaseActivityVDB<ActivityCameraxkBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        initCamera()
    }

    private val _format = ACameraKXFormat.YUV_420_888

    @SuppressLint("MissingPermission")
    private fun initCamera() {
//        vb.cameraxkPreviewLayout.previewView?.scaleType = PreviewView.ScaleType.FILL_CENTER
        vdb.cameraxkPreviewLayout.apply {
//            slider?.applyVisible()
//            seekBar?.applyVisible()
            initCameraKX(this@CameraKXActivity, CameraKXConfig(_format, ACameraKXFacing.BACK, aspectRatio = AAspectRatio.RATIO_4_3, isAutoFocus = false))
            setCameraXFrameListener(_cameraKXFrameListener)
            setCameraXCaptureListener(_cameraKXCaptureListener)
            if (!XXPermissionsCheckUtil.hasCameraPermission(this@CameraKXActivity)) {
                XXPermissionsRequestUtil.requestCameraPermission(this@CameraKXActivity, onGranted = {
                    this.restartCameraKX()
                }, onDenied = {
                    UtilKActivityStart.startSettingApplicationDetailsSettings(this@CameraKXActivity)
                })
            }
        }
        vdb.cameraxkBtn.setOnClickListener {
            vdb.cameraxkPreviewLayout.startCapture()
        }
    }

    private var _outputBitmap: Bitmap? = null

    @OptIn(OFieldCall_Close::class)
    private val _cameraKXFrameListener: ICameraXKFrameListener by lazy {
        object : ICameraXKFrameListener {
            @SuppressLint("UnsafeOptInUsageError")
            override fun invoke(imageProxy: ImageProxy) {
                when (_format) {
                    ACameraKXFormat.RGBA_8888 -> _outputBitmap = imageProxy.imageProxyRgba88882bitmapRgba8888()
                    ACameraKXFormat.YUV_420_888 -> _outputBitmap = imageProxy.imageProxyYuv4208882bitmapJpeg()
                }
                _outputBitmap?.let {
                    runOnUiThread {
                        vdb.cameraxkImg1.setImageBitmap(_outputBitmap)
                    }
                }
                imageProxy.close()
            }
        }
    }

    private val _cameraKXCaptureListener = object : ICameraKXCaptureListener {
        override fun onCaptureSuccess(bitmap: Bitmap, imageRotation: Int) {
            runOnUiThread {
                vdb.cameraxkImg.setImageBitmap(bitmap)
            }
        }
    }
}