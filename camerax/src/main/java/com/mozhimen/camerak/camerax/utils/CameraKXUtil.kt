package com.mozhimen.camerak.camerax.utils

import android.annotation.SuppressLint
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.util.Size
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat
import androidx.camera.camera2.internal.compat.quirk.CamcorderProfileResolutionQuirk
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.camerak.camerax.annors.AAspectRatio
import com.mozhimen.camerak.camerax.cons.CAspectRatio
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @ClassName CameraKXUtil
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Version 1.0
 */
object CameraKXUtil : IUtilK {
    /**
     *  检测当前尺寸的最合适的长宽比
     */
    @JvmStatic
    fun getFitAspectRatio(@AAspectRatio ratio: Int, width: Int, height: Int): Int {
        if (ratio == AAspectRatio.RATIO_DEFAULT) {
            val previewRatio = max(width, height).toDouble() / min(width, height)
            if (abs(previewRatio - CAspectRatio.RATIO_VAL_4_3) <= abs(previewRatio - CAspectRatio.RATIO_VAL_16_9))
                return AAspectRatio.RATIO_4_3
            return AAspectRatio.RATIO_16_9
        } else return ratio
    }

    @JvmStatic
    @SuppressLint("RestrictedApi", "VisibleForTests")
    @androidx.annotation.OptIn(androidx.camera.camera2.interop.ExperimentalCamera2Interop::class)
    fun getFocusDistanceRange(cameraInfo: CameraInfo) :Pair<Float,Float>?{
        val characteristics = CameraCharacteristicsCompat.toCameraCharacteristicsCompat(Camera2CameraInfo.extractCameraCharacteristics(cameraInfo))
        val lensInfoMinimumFocusDistance = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
        val lensInfoHyperfocalDistance = characteristics.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE)
        UtilKLogWrapper.d(TAG, "min focus is $lensInfoMinimumFocusDistance lensInfoHyperfocalDistance $lensInfoHyperfocalDistance")
        return if (lensInfoMinimumFocusDistance!=null&&lensInfoHyperfocalDistance!=null){
            lensInfoMinimumFocusDistance to lensInfoHyperfocalDistance
        }else
            null
    }

    @JvmStatic
    @androidx.annotation.OptIn(androidx.camera.camera2.interop.ExperimentalCamera2Interop::class)
    fun disableAutofocus(cameraControl: CameraControl) {
        val camera2CameraControl: Camera2CameraControl = Camera2CameraControl.from(cameraControl)

        //Then you can set the focus mode you need like this
        val captureRequestOptions = CaptureRequestOptions.Builder()
            .setCaptureRequestOption(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF)
            .build()
        camera2CameraControl.captureRequestOptions = captureRequestOptions
    }

    @JvmStatic
    @androidx.annotation.OptIn(androidx.camera.camera2.interop.ExperimentalCamera2Interop::class)
    fun changeFocusDistance(cameraControl: CameraControl, distance: Float) {
        val camera2CameraControl: Camera2CameraControl = Camera2CameraControl.from(cameraControl)

        //Then you can set the focus mode you need like this
        val captureRequestOptions = CaptureRequestOptions.Builder()
            .setCaptureRequestOption(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF)
            .setCaptureRequestOption(CaptureRequest.LENS_FOCUS_DISTANCE, distance)
            .build()
        camera2CameraControl.captureRequestOptions = captureRequestOptions
    }

    @androidx.annotation.OptIn(androidx.camera.camera2.interop.ExperimentalCamera2Interop::class)
    @SuppressLint("RestrictedApi", "VisibleForTests")
    @JvmStatic
    fun getSupportedResolutions(camera: Camera): List<Size> {
        val characteristics = CameraCharacteristicsCompat.toCameraCharacteristicsCompat(Camera2CameraInfo.extractCameraCharacteristics(camera.cameraInfo))
        return CamcorderProfileResolutionQuirk(characteristics).supportedResolutions
    }
}