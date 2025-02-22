package com.mozhimen.camerak.dahua.exam.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.company.NetSDK.CB_fMessageCallBack;
import com.company.NetSDK.INetSDK;
import com.mozhimen.camerak.dahua.exam.activity.NetSDKApplication;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;

import java.util.Date;

/**
 * Created by 29779 on 2017/4/8.
 */
public class AlarmListenModule {

    NetSDKApplication sdkApp;
    public AlarmListenModule(Context context){
        sdkApp = (NetSDKApplication)((AppCompatActivity)context).getApplication();
    }

    public void setCallback(CB_fMessageCallBack callback){
        INetSDK.SetDVRMessCallBack(callback);
    }

    ///start listening
    ///开始监听
    public boolean startListen(){
        boolean bRet = INetSDK.StartListenEx(sdkApp.getLoginHandle());
        if (!bRet) {
            ToolKits.writeLog("StartListenEx Failed!");
        }
        return bRet;
    }

    /// stop listening
    /// 结束监听
    public boolean stopListen(){
        boolean bRet = INetSDK.StopListen(sdkApp.getLoginHandle());
        if (!bRet) {
            ToolKits.writeLog("StopListen Failed!");
        }
        return bRet;
    }


    public enum AlarmStatus {
        ALARM_START, ALARM_STOP
    }

    /**
     * 报警事件信息
     * */
    public static class AlarmEventInfo {
        public int chn;
        public int type;
        public Date date;
        public AlarmStatus status;

        public AlarmEventInfo(int chn, int type, AlarmStatus status) {
            this.chn = chn;
            this.type = type;
            this.status = status;
            this.date = new Date();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AlarmEventInfo showInfo = (AlarmEventInfo) o;
            return chn == showInfo.chn && type == showInfo.type;
        }
    }
}
