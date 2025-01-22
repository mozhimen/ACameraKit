package com.mozhimen.camerak.dahua.helpers

import android.util.Log
import com.company.NetSDK.CB_fSnapRev
import com.company.NetSDK.INetSDK
import com.company.NetSDK.SNAP_PARAMS
import com.company.PlaySDK.Constants
import com.company.PlaySDK.IPlaySDK
import com.mozhimen.camerak.dahua.bases.BaseHelper

/**
 * @ClassName CapturePictureHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/3/9 11:08
 * @Version 1.0
 */
class CapturePictureHelper : BaseHelper() {
    /**
     * 摄像机抓图
     * @param m_hLoginHandle Long
     * @param jpgFilePathWithName String
     * @return Boolean
     */
    fun capturePicture(m_hLoginHandle: Long, jpgFilePathWithName: String): Boolean {
        if (!INetSDK.CapturePicture(m_hLoginHandle, jpgFilePathWithName)) {
            Log.w(TAG, "capturePicture: Failed!")
            return false
        }
        return true
    }

    /**
     * 本地抓图
     * @param nPort Int
     * @param jpgFilePathWithName String
     * @return Boolean
     */
    fun capturePictureWhenPlay(nPort: Int, jpgFilePathWithName: String): Boolean {
        if (IPlaySDK.PLAYCatchPicEx(nPort, jpgFilePathWithName, Constants.PicFormat_JPEG) == 0) {
            Log.d(TAG, "localCapturePicture: Failed!")
            return false
        }
        return true
    }

    /**
     * 远程抓图
     * @param m_hLoginHandle Long
     * @param chn Int
     * @return Boolean
     */
    fun remoteCapturePicture(m_hLoginHandle: Long, chn: Int): Boolean {
        return snapPicture(m_hLoginHandle, chn, 0, 0)
    }

    /**
     * 定时抓图
     * @param m_hLoginHandle Long
     * @param chn Int
     * @return Boolean
     */
    fun timerCapturePicture(m_hLoginHandle: Long, chn: Int): Boolean {
        return snapPicture(m_hLoginHandle, chn, 1, 2)
    }

    /**
     * 停止定时抓图
     * @param m_hLoginHandle Long
     * @param chn Int
     * @return Boolean
     */
    fun stopCapturePicture(m_hLoginHandle: Long, chn: Int): Boolean {
        return snapPicture(m_hLoginHandle, chn, -1, 0)
    }

    /**
     * 抓图
     * @param chn Int
     * @param mode Int
     * @param interval Int
     * @return Boolean
     */
    private fun snapPicture(m_hLoginHandle: Long, chn: Int, mode: Int, interval: Int): Boolean {
        // 发送抓图命令给前端设备，抓图的信息
        val stuSnapParams = SNAP_PARAMS()
        stuSnapParams.Channel = chn // 抓图通道
        stuSnapParams.mode = mode // 抓图模式
        stuSnapParams.Quality = 3 // 画质
        stuSnapParams.InterSnap = interval // 定时抓图时间间隔
        stuSnapParams.CmdSerial = 0 // 请求序列号，有效值范围 0~65535，超过范围会被截断为
        if (!INetSDK.SnapPictureEx(m_hLoginHandle, stuSnapParams)) {
            Log.d(TAG, "snapPicture: Failed!")
            return false
        }
        return true
    }

    /**
     * 设置抓图回调函数
     * @param snapReceiveCB CB_fSnapRev
     */
    fun setSnapRevCallBack(snapReceiveCB: CB_fSnapRev) {
        //设置抓图回调函数， 图片主要在m_SnapReceiveCB中返回
        INetSDK.SetSnapRevCallBack(snapReceiveCB)
    }
}