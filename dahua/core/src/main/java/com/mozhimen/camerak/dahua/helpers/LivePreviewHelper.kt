package com.mozhimen.camerak.dahua.helpers

import android.util.Log
import android.view.SurfaceView
import com.company.NetSDK.CB_fRealDataCallBackEx
import com.company.NetSDK.INetSDK
import com.company.NetSDK.SDK_RealPlayType
import com.company.PlaySDK.IPlaySDK
import com.company.PlaySDK.IPlaySDKCallBack.fDrawCBFun
import com.mozhimen.kotlin.utilk.android.widget.showToastOnMain
import com.mozhimen.kotlin.utilk.java.util.UtilKDateWrapper
import com.mozhimen.kotlin.utilk.wrapper.UtilKRes
import com.mozhimen.camerak.dahua.R
import com.mozhimen.camerak.dahua.bases.BaseHelper


/**
 * @ClassName LivePreviewHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/9 14:44
 * @Date 2023/3/9 11:06
 * @Version 1.0
 */
class LivePreviewHelper : BaseHelper() {

    companion object {
        private const val STREAM_BUF_SIZE = 1024 * 1024 * 2
        private const val RAW_AUDIO_VIDEO_MIX_DATA = 0 //原始音视频混合数据;  //Raw audio and video mixing data.
    }

    private var _realHandle: Long = 0
    private var _playPort: Int = 0
    private var _curVolume = -1
    private var _isRecording = false
    private var _isOpenSound = true
    private var _isDelayPlay = false
    private var _streamTypeMap: MutableMap<Int, Int> = HashMap()
    private var _realDataCallBackEx: CB_fRealDataCallBackEx? = null// for preview date callback

    init {
        _playPort = IPlaySDK.PLAYGetFreePort()
        ///码流类型的hash
        _streamTypeMap[0] = SDK_RealPlayType.SDK_RType_Realplay_0
        _streamTypeMap[1] = SDK_RealPlayType.SDK_RType_Realplay_1
    }

    /**
     * 视频预览前设置
     * @param sv SurfaceView?
     * @return Boolean
     */
    fun prePlay(sv: SurfaceView?, fDrawCBFun: fDrawCBFun?): Boolean {
        val code = IPlaySDK.PLAYOpenStream(_playPort, null, 0, STREAM_BUF_SIZE)
        val isOpened = code != 0
        if (!isOpened) {
            Log.d(TAG, "OpenStream Failed code $code")
            return false
        }
        val isPlaying = IPlaySDK.PLAYPlay(_playPort, sv) != 0
        if (!isPlaying) {
            Log.d(TAG, "PLAYPlay Failed")
            IPlaySDK.PLAYCloseStream(_playPort)
            return false
        }
        fDrawCBFun?.let {
            IPlaySDK.PLAYRigisterDrawFun(_playPort, 0, it, 0)
        }
        if (_isOpenSound) {
            val isSuccess = IPlaySDK.PLAYPlaySoundShare(_playPort) != 0
            if (!isSuccess) {
                Log.d(TAG, "SoundShare Failed")
                IPlaySDK.PLAYStop(_playPort)
                IPlaySDK.PLAYCloseStream(_playPort)
                return false
            }
            if (-1 == _curVolume) {
                _curVolume = IPlaySDK.PLAYGetVolume(_playPort)
            } else {
                IPlaySDK.PLAYSetVolume(_playPort, _curVolume)
            }
        }
        if (_isDelayPlay) {
            if (IPlaySDK.PLAYSetDelayTime(_playPort, 500 /*ms*/, 1000 /*ms*/) == 0) {
                Log.d(TAG, "SetDelayTime Failed")
            }
        }
        return true
    }

    fun getHandle(): Long {
        return _realHandle
    }

    fun getPlayPort(): Int {
        return _playPort
    }

    fun setOpenSound(isOpenSound: Boolean) {
        this._isOpenSound = isOpenSound
    }

    fun setDelayPlay(isDelayPlay: Boolean) {
        this._isDelayPlay = isDelayPlay
    }

    fun isReadyPlay(): Boolean =
        _realHandle == 0L

    //开始预览视频
    fun startPlay(loginHandle: Long, channel: Int, streamType: Int, surfaceView: SurfaceView?, fDrawCBFun: fDrawCBFun?) {
        if (surfaceView==null)return
        Log.d(TAG, "startPlay StreamType: " + _streamTypeMap[streamType])
        _realHandle = INetSDK.RealPlayEx(loginHandle, channel, _streamTypeMap[streamType]!!)
        if (_realHandle == 0L) {
            Log.e(TAG, "startPlay: RealPlayEx failed!")
            return
        }
        if (!prePlay(surfaceView, fDrawCBFun)) {
            Log.d(TAG, "prePlay returned false..")
            INetSDK.StopRealPlayEx(_realHandle)
            return
        }
        if (_realHandle != 0L) {
            _realDataCallBackEx = CB_fRealDataCallBackEx { _, dataType, buffer, _, _ ->
                if (RAW_AUDIO_VIDEO_MIX_DATA == dataType) {
                    IPlaySDK.PLAYInputData(_playPort, buffer, buffer.size)
                }
            }
            INetSDK.SetRealDataCallBackEx(_realHandle, _realDataCallBackEx, 1)
        }
    }

    ///停止预览视频
    fun stopPlay() {
        if (_realHandle == 0L) return
        try {
            IPlaySDK.PLAYStop(_playPort)
            if (_isOpenSound) {
                IPlaySDK.PLAYStopSoundShare(_playPort)
            }
            IPlaySDK.PLAYCloseStream(_playPort)
            INetSDK.StopRealPlayEx(_realHandle)
            if (_isRecording) {
                INetSDK.StopSaveRealData(_realHandle)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _realHandle = 0
        _isRecording = false
    }

    /**
     * 初始化视频窗口
     * @param sv SurfaceView?
     */
    fun initSurfaceView(sv: SurfaceView?) {
        if (sv == null) return
        IPlaySDK.InitSurface(_playPort, sv)
    }

    /**
     * 是否在播放
     * @return Boolean
     */
    fun isRealPlaying(): Boolean {
        return _realHandle != 0L
    }

    /**
     * 获取要显示的码流类型
     * @param channel Int
     * @return List<*>
     */
    fun getStreamTypeList(channel: Int): List<*> {
        val list = ArrayList<String>()
        val streamNames = UtilKRes.gainStringArray(R.array.stream_type_array)
        for (i in 0..1) {
            list.add(streamNames[i])
        }
        return list
    }

    /**
     * 录制
     * @param recordFlag Boolean
     * @return Boolean
     */
    fun startRecord(recordFlag: Boolean): Boolean {
        if (_realHandle == 0L) {
            UtilKRes.gainString(R.string.live_preview_not_open).showToastOnMain()
            return false
        }
        Log.d(TAG, "record: ExternalFilesDir:\" + mContext!!.getExternalFilesDir(null)!!.absolutePath")
        _isRecording = recordFlag
        if (_isRecording) {
            val recordFile = getInnerAppFileName("dav")
            if (!INetSDK.SaveRealData(_realHandle, recordFile)) {
                Log.e(TAG, "record: file:$recordFile")
                UtilKRes.gainString(R.string.local_record_failed).showToastOnMain()
                return false
            }
        } else {
            INetSDK.StopSaveRealData(_realHandle)
        }
        return true
    }

    /**
     * 获取录屏文件名
     * @param suffix String
     * @return String
     */
    @Synchronized
    private fun getInnerAppFileName(suffix: String): String {
        return "${_context.getExternalFilesDir(null)!!.absolutePath}/camerak_dahua_live_${UtilKDateWrapper.getNowLong()}.${suffix}"
    }
}