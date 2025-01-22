package com.mozhimen.camerak.dahua.helpers

import android.util.Log
import com.company.NetSDK.*
import com.mozhimen.kotlin.utilk.wrapper.UtilKRes
import com.mozhimen.camerak.dahua.R
import com.mozhimen.camerak.dahua.bases.BaseHelper
import com.mozhimen.camerak.dahua.cons.CIPLoginErrorCode
import com.mozhimen.camerak.dahua.cons.EIPLoginType
import java.util.ArrayList


/**
 * @ClassName IPLoginHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/9 14:22
 * @Version 1.0
 */
class IPLoginHelper : BaseHelper() {

    private var _loginHandle: Long = 0
    private var _deviceInfo: NET_DEVICEINFO_Ex? = null
    private var _deviceName: String = ""
    private var _loginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_MOBILE
    private var _errorCode = 0
    private var _isLogin = false

    fun setLoginType(type: EIPLoginType) {
        if (EIPLoginType.IP == type) {
            _loginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_MOBILE
        } else if (EIPLoginType.P2P == type) {
            _loginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_P2P
        }
    }

    fun getDeviceInfo(): NET_DEVICEINFO_Ex? {
        return _deviceInfo
    }

    fun getDeviceName(): String {
        return _deviceName
    }

    fun getLoginHandle(): Long {
        return _loginHandle
    }

    fun getErrorCode(): Int {
        return _errorCode
    }

    fun getErrorMsg(): String {
        return CIPLoginErrorCode.getErrorCode(_errorCode)
    }

    fun isLogin(): Boolean {
        return _isLogin
    }

    //获取通道数量
    fun getChannel(): Int {
        return _deviceInfo?.nChanNum ?: 0
    }

    //获取要显示的通道号
    fun getChannelList(): List<*> {
        val channelList = ArrayList<String>()
        for (i in 0 until getChannel()) {
            channelList.add(UtilKRes.gainString(R.string.channel) + i)
        }
        return channelList
    }

    fun login(address: String, port: String, username: String, password: String): Boolean {
        _deviceInfo = NET_DEVICEINFO_Ex()
        val stuIn = NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY()
        System.arraycopy(address.toByteArray(), 0, stuIn.szIP, 0, address.toByteArray().size)
        stuIn.nPort = port.toInt()
        System.arraycopy(username.toByteArray(), 0, stuIn.szUserName, 0, username.toByteArray().size)
        System.arraycopy(password.toByteArray(), 0, stuIn.szPassword, 0, password.toByteArray().size)
        stuIn.emSpecCap = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_TCP
        val stuOut = NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY()
        _loginHandle = INetSDK.LoginWithHighLevelSecurity(stuIn, stuOut)
        //        mLoginHandle = INetSDK.LoginEx2(address, Integer.parseInt(port), username, password, mLoginType, null, mDeviceInfo, err);

        /////////// TLS加密策略登录扩展接口 ///////////////////
//        pstLoginParam = new NET_LOGIN_PARAM();
//        System.arraycopy(address.getBytes(), 0, pstLoginParam.szDVRIP, 0, address.getBytes().length); // IP
//        pstLoginParam.wDVRPort = Integer.parseInt(port);  // 端口号
//        System.arraycopy(username.getBytes(), 0, pstLoginParam.szUserName, 0, username.getBytes().length); // 用户名
//        System.arraycopy(password.getBytes(), 0, pstLoginParam.szPassword, 0, password.getBytes().length); // 密码
//        pstLoginParam.emTlsPolicy = EM_TLS_ENCRYPTION_POLICY.EM_TLS_ENCRYPTION_COMPULSIVE;
//        pstLoginParam.emSpecCap = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_TCP;
//        mLoginHandle = INetSDK.LoginEx3(pstLoginParam, mDeviceInfo, err);
        if (0L == _loginHandle) {
            _errorCode = INetSDK.GetLastError()
            Log.e(TAG, "Failed to Login Device $address")
            return false
        }
        _deviceName = address
        _deviceInfo = stuOut.stuDeviceInfo
        // 设置监视优化模式，建议使用
//        int nPlayValue = 0x01|0x02;
//        boolean bPlayRet = INetSDK.SetLocalMode(mLoginHandle, EM_LOCAL_MODE.EM_LOCAL_PLAY_FLAG_MODE, nPlayValue);
//        if(bPlayRet == false)
//        {
//            ToolKits.writeLog("SetLocalMode-EM_LOCAL_PLAY_FLAG_MODE failed,nPlayValue:" + nPlayValue + "LastError:" + INetSDK.GetLastError());
//        }
        //设置回放优化模式，建议使用
//        int nPlaybackValue = 0x01;
//        boolean bPlaybackRet = INetSDK.SetLocalMode(mLoginHandle, EM_LOCAL_MODE.EM_LOCAL_PLAYBACK_FLAG_MODE, nPlaybackValue);
//        if(bPlaybackRet == false)
//        {
//            ToolKits.writeLog("SetLocalMode-EM_LOCAL_PLAYBACK_FLAG_MODE failed,nPlaybackValue：" + nPlaybackValue + "LastError:" + INetSDK.GetLastError());
//        }
        return true.also { _isLogin = it }
    }

    fun logout(): Boolean {
        if (0L == _loginHandle) {
            return false
        }
        val retLogout = INetSDK.Logout(_loginHandle)
        if (retLogout) {
            _loginHandle = 0
        }
        return retLogout.also { _isLogin = false }
    }
}