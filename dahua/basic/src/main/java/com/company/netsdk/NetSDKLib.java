package com.company.netsdk;

import android.util.Log;

import com.company.NetSDK.*;
import com.company.netsdk.commons.ICameraKDisconnectListener;
import com.mozhimen.kotlin.utilk.java.io.UtilKFile;
import com.mozhimen.kotlin.utilk.kotlin.UtilKStrFile;

/**
 * Created by 29779 on 2017/4/10.
 * This Class is Created for init INetSDK.jar
 */
public final class NetSDKLib {
    private final static String TAG = "NetSDKLib>>>>>";
    private static NetSDKLib instance = new NetSDKLib();
    private boolean mbInit = false;

    //private DeviceDisConnect mDisconnect;
    //private DeviceReConnect mReconnect;

    /// Timeout of NetSDK API
    /// INetSDK 接口超时时间
    public static final int TIMEOUT_5S = 5000;      // 5 second
    public static final int TIMEOUT_10S = 10000;    // 10 second
    public static final int TIMEOUT_30S = 30000;    // 30 second

    private NetSDKLib() {
        //mDisconnect = new DeviceDisConnect();
        //mReconnect = new DeviceReConnect();
    }

    public static NetSDKLib getInstance() {
        return instance;
    }

    /// Init NetSDK library's resources.
    /// 初始化 NETSDK
    public synchronized void init(ICameraKDisconnectListener listener) {
        INetSDK.LoadLibrarys();
        if (mbInit) {
            Log.d(TAG, "Already init.");
            return;
        }
        mbInit = true;

        /// Init NetSDK, and set disconnect callback.
        /// 初始化接口在所有的SDK函数之前调用 并设置断线回调 :当app和设备端网络断开时，会触发回调
        /// 该接口仅需调用一次
        boolean zRet = INetSDK.Init(listener);
        if (!zRet) {
            Log.e(TAG, "init NetSDK error!");
            return;
        }

        // 设置登录前优化模式,建议使用
        int pParam = EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_DEVICE_ATTR|EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_DEVICE_SN|EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_DISK_INFO|EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_DIGITAL_NUM|
                EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_ALARM_IO|EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_DEVICE_ABILITY|EM_OPTTYPE_MOBILE_TYPE.OPTTYPE_MOBILE_STATE_SOFTWARE;

        //boolean bRet = INetSDK.SetOptimizeMode(EM_OPTIMIZE_TYPE.EM_OPT_TYPE_MOBILE_OPTION, pParam);
//        if(bRet == false)
//        {
//            ToolKits.writeLog("SetOptimizeMode-EM_OPT_TYPE_MOBILE_OPTION failed,pParam:" + pParam + "LastError:" + INetSDK.GetLastError());
//        }
        /// Set Reconnect callback.
        /// 设置断线重连回调 : 当app重新连接上设备时，会触发该回调;
        /// 此处默认不使用
        /// INetSDK.SetAutoReconnect(mReconnect);

        /// Close the SDK Log
        // closeSDKLog();

        /// Set global parameters of NetSDK.
        NET_PARAM stNetParam = new NET_PARAM();
        stNetParam.nConnectTime = TIMEOUT_10S;
        stNetParam.nWaittime = TIMEOUT_10S; // Time out of common Interface.
        stNetParam.nSearchRecordTime = TIMEOUT_30S; // Time out of Playback interface.
        INetSDK.SetNetworkParam(stNetParam);
    }

    /// Cleanup NetSDK library's resources.
    /// 清理 INetSDK.jar 资源
    public synchronized void cleanup() {
        /// only be invoked for once
        if (mbInit) {
            INetSDK.Cleanup();
            mbInit = false;
        }
    }

//    public boolean isFileExist(String fileName) {
//        if (fileName == null) {
//            return false;
//        }
//
//        File file = new File(fileName);
//        return file.exists();
//    }

    /// Open SDK log
    /// 打开 SDK 日志
    public boolean openLog(String logFile) {
        Log.d(TAG, "log file -> " + logFile);
        if (!UtilKStrFile.isFileExist(logFile)) {
            return false;
        }

        LOG_SET_PRINT_INFO logInfo = new LOG_SET_PRINT_INFO();
        logInfo.bSetPrintStrategy = true;
        logInfo.nPrintStrategy = 0; // 0 - Saved as file. 1 - show log in the console.
        logInfo.bSetFilePath = true;
        System.arraycopy(logFile.getBytes(), 0, logInfo.szLogFilePath, 0, logFile.length());

        return INetSDK.LogOpen(logInfo);
    }

    /// Close SDK log
    /// 关闭日志
    public boolean closeLog() {
        return INetSDK.LogClose();
    }

    /// while app disconnect with device, the interface will be invoked.
    /// 断线回调
//    public class DeviceDisConnect implements CB_fDisConnect {
//        @Override
//        public void invoke(long loginHandle, String deviceIp, int devicePort) {
//            Log.d(TAG, "Device " + deviceIp + " is disConnected !");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    ToolKits.alertDisconnected();
//                }
//            });
//        }
//    }

    /// After app reconnect the device, the interface will be invoked.
    /// 重连回调
//    public class DeviceReConnect implements CB_fHaveReConnect {
//        @Override
//        public void invoke(long loginHandle, String deviceIp, int devicePort) {
//            Log.d(TAG, "Device " + deviceIp + " is reconnect !");
//        }
//    }

//    private Handler mHandler = new Handler(Looper.myLooper());

}
