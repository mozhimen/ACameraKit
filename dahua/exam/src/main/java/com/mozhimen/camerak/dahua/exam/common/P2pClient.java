package com.mozhimen.camerak.dahua.exam.common;

import android.util.Log;

import com.mm.android.dhproxy.client.DHProxyClient;

/**
 * Created by 29779 on 2017/4/14.
 */
public class P2pClient {
    private final static String TAG = "P2pClient";
    private DHProxyClient mP2pClient;
    private int mLocalPort; /// the port used for p2p service
    private boolean mServiceStopped = false;

    public P2pClient() {
        mP2pClient = new DHProxyClient();
        mLocalPort = 0;
        mServiceStopped = false;
    }

    public int getP2pPort() {
        return this.mLocalPort;
    }

    public synchronized boolean stopService() {
        Log.d(TAG, "stopService");

        if (mServiceStopped) {
            return true;
        }

        if (mLocalPort > 0) {
            if (0 != mP2pClient.delPort(mLocalPort)) {
                Log.d(TAG, "delPort " + mLocalPort);
            }
            mLocalPort = 0;
        }

        if (0 != mP2pClient.exit()) {
            Log.d(TAG, "exit ");
        }

        mServiceStopped = true;
        return true;
    }

    public boolean startService(String svrAddress, String svrPort, String username,String svrKey,
                                String deviceSn, String devicePort, String p2pDeviceUsername, String p2pDevicePassword) {
        ToolKits.writeLog("Start Service --> Begin.");
        String strClientType = "NetsdkDemo";
        if (!mP2pClient.initWithName(svrAddress,Integer.parseInt(svrPort), svrKey, strClientType, username)) {
            Log.e(TAG, "Failed to init P2p Client.");
            return  false;
        }

        String strDeviceInfo = mP2pClient.getDeviceInfo(deviceSn);
        String strVesion = "";
        String strRandsalt = "";
        if (strDeviceInfo.length() != 0)
        {
            // get version
            int nVerPos = strDeviceInfo.indexOf("devp2pver");
            if(nVerPos != -1) {
                int nVerValueBeginPos = nVerPos + 12;
                int nVerValueEndPos = strDeviceInfo.indexOf('"', nVerValueBeginPos + 1);
                if(nVerValueEndPos != -1)
                {
                    strVesion = strDeviceInfo.substring(nVerValueBeginPos, nVerValueEndPos);
                }
            }
            // get randsalt
            int nRandsaltPos = strDeviceInfo.indexOf("randsalt");
            if(nRandsaltPos != -1) {
                int nRandsaltValueBeginPos = nRandsaltPos + 11;
                int nRandsaltValueEndPos = strDeviceInfo.indexOf('"', nRandsaltValueBeginPos + 1);
                if(nRandsaltValueEndPos != -1)
                {
                    strRandsalt = strDeviceInfo.substring(nRandsaltValueBeginPos, nRandsaltValueEndPos);
                }
            }
        }
        boolean bClientOnline = false;
        boolean bDeviceOnline = false;
        int nLoopCount = 3;
        while (nLoopCount > 0) {
            mLocalPort = mP2pClient.addPortEx(deviceSn, Integer.parseInt(devicePort), 0, p2pDeviceUsername, p2pDevicePassword, strRandsalt, strVesion);
            ToolKits.writeLog("mLocalPort : " + mLocalPort);
            int nTry2 = 0;
            while (nTry2 < 200) {    ///200次，每次100ms，总时间为20s；如果设置的总时间太少(比如20 * 100)，p2p不一定成功
                int nStatus = mP2pClient.portStatus(mLocalPort);
                ToolKits.writeLog("nStatus : " + nStatus);
                if (1 == nStatus) {
                    Log.d(TAG, "Start Service --> End. add port ok . port = " + mLocalPort);
                    return true;
                } else if (2 == nStatus) {
                    if (!bClientOnline) {
                        // status
                        if (mP2pClient.status() == 3) {
                            Log.d(TAG, "client is online.");
                            bClientOnline = true;
                        }
                    }

                    if (!bDeviceOnline) {
                        if (1 == mP2pClient.query(deviceSn)) {
                            Log.d(TAG, "device is online.");
                            bDeviceOnline = true;
                        }
                    }

                    break;
                }
                nTry2 ++;

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mP2pClient.delPort(mLocalPort);
            nLoopCount --;
        }

        Log.d(TAG, "Start Service --> End. failed to start p2p service.");
        return false;
    }
}
