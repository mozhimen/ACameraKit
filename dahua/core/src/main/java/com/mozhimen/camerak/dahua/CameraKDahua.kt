package com.mozhimen.camerak.dahua

import com.mozhimen.camerak.dahua.commons.ICameraKDahua
import com.mozhimen.camerak.dahua.helpers.CameraKDahuaProvider
import java.util.concurrent.ConcurrentHashMap


/**
 * @ClassName CameraKDahuaMgr
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/8 17:12
 * @Version 1.0
 */
class CameraKDahua : ICameraKDahua<CameraKDahuaProvider> {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    ///////////////////////////////////////////////////////////////

    private val _cameraKDahuaMap = ConcurrentHashMap<String, CameraKDahuaProvider>()

    /////////////////////////////////////////////////////////////////////////////////////

    override fun with(name: String): CameraKDahuaProvider {
        var cd = _cameraKDahuaMap[name]
        if (cd == null) {
            cd = CameraKDahuaProvider()
            _cameraKDahuaMap[name] = cd
        }
        return cd
    }

    ///////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = CameraKDahua()
    }
}