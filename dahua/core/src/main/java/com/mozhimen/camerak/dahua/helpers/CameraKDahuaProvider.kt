package com.mozhimen.camerak.dahua.helpers

import com.company.netsdk.NetSDKLib
import com.company.netsdk.commons.ICameraKDisconnectListener
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.kotlin.utilk.java.util.UtilKDateWrapper
import com.mozhimen.kotlin.utilk.kotlin.UtilKStrFile
import com.mozhimen.camerak.dahua.commons.ICameraKDahuaProvider


/**
 * @ClassName CameraKDahuaMgr
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/8 17:12
 * @Version 1.0
 */
class CameraKDahuaProvider : BaseUtilK(), ICameraKDahuaProvider {
    private val _ipLoginHelper by lazy { IPLoginHelper() }
    private val _livePreviewHelper by lazy { LivePreviewHelper() }
    private val _capturePictureHelper by lazy { CapturePictureHelper() }
    private val _cameraLogPath by lazy { _context.cacheDir.absolutePath + "/camerak_dahua/${UtilKDateWrapper.getNowLong()}.log" }

    override fun init(listener: ICameraKDisconnectListener) {
        NetSDKLib.getInstance().init(listener)
        UtilKStrFile.createFile(_cameraLogPath)
        NetSDKLib.getInstance().openLog(_cameraLogPath)
    }

    override fun getIPLogin(): IPLoginHelper {
        return _ipLoginHelper
    }

    override fun getLivePreview(): LivePreviewHelper {
        return _livePreviewHelper
    }

    override fun getCapturePicture(): CapturePictureHelper {
        return _capturePictureHelper
    }

    override fun destroy() {
        NetSDKLib.getInstance().cleanup()
    }
}