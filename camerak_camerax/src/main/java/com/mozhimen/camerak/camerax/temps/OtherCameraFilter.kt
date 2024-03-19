package com.mozhimen.camerak.camerax.temps

import android.annotation.SuppressLint
import androidx.camera.core.CameraInfo
import androidx.camera.core.impl.CameraInfoInternal
import androidx.core.util.Preconditions
import com.mozhimen.basick.utilk.android.util.d
import com.mozhimen.camerak.camerax.commons.ICameraKXFilter


/**
 * @ClassName OtherCameraSelectorFilter
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/12/15 14:50
 * @Version 1.0
 */
class OtherCameraFilter(private val _id: String) : ICameraKXFilter {

    @SuppressLint("RestrictedApi")
    override fun filter(cameraInfos: MutableList<CameraInfo>): MutableList<CameraInfo> {
        "filter: _id $_id cameraInfos $cameraInfos".d(TAG)
        val tempCameraInfos = ArrayList<CameraInfo>()
        cameraInfos.forEach {
            Preconditions.checkArgument(it is CameraInfoInternal, "The camera info doesn't contain internal implementation.")
            it as CameraInfoInternal
            val id = it.cameraId
            if (id.contains(_id) || id == _id) tempCameraInfos.add(it)
        }
        return tempCameraInfos.also { "filter: cameraInfos ${it.joinToString { list-> (list as CameraInfoInternal).cameraId }}".d(TAG) }
    }
}
