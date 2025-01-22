package com.mozhimen.camerak.dahua.exam.activity.APConfig;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.company.NetSDK.CFG_NETAPP_WLAN;
import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.mozhimen.camerak.dahua.exam.R;
import com.mozhimen.camerak.dahua.exam.activity.NetSDKApplication;
import com.mozhimen.camerak.dahua.exam.common.ClearEditText;
import com.mozhimen.camerak.dahua.exam.common.Encryption_2;
import com.mozhimen.camerak.dahua.exam.common.EyeImageButton;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;


/**
 * Created by 32940 on 2018/12/11.
 */
public class ApConfigActivity extends AppCompatActivity {
    private EditText         mEditTextWlanName;
    private ClearEditText mEditTextWlanPassword;
    private EyeImageButton mEyeImageButton;
    private Button           mButtonApConfig;
    private TextView         mTextViewStatus;

    private Resources        res;

    private String           wlan_name;
    private String           wlan_pwd;
    private int             wlan_authMode;
    private int             wlan_encrAlgr;
    private int             nEncryption;

    //private boolean         bOther;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ap_config);
        setTitle(R.string.activity_main_ap_config);

        // 添加返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        res = this.getResources();

        wlan_name = getIntent().getStringExtra("wlan_name");
        wlan_authMode = getIntent().getIntExtra("wlan_authMode", 0);
        wlan_encrAlgr = getIntent().getIntExtra("wlan_encrAlgr", 0);
        nEncryption = Encryption_2.getValue(wlan_authMode, wlan_encrAlgr);

        ToolKits.writeLog("wlan_name:" + wlan_name);
        ToolKits.writeLog("wlan_authMode:" + wlan_authMode);
        ToolKits.writeLog("wlan_encrAlgr:" + wlan_encrAlgr);
        ToolKits.writeLog("nEncryption:" + nEncryption);

        setupView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setupView() {
        mEditTextWlanName = (EditText)findViewById(R.id.wlan_name_editText);

        //if (wlan_name.length() <=0)
        //{
         //   bOther = true;
         //   mEditTextWlanName.setEnabled(true);
        //}
        //else
        //{
        //    bOther = false;
        //    mEditTextWlanName.setEnabled(false);
        //    mEditTextWlanName.setText(wlan_name);
        //}
        mEditTextWlanName.setEnabled(false);
        mEditTextWlanName.setText(wlan_name);

        mEditTextWlanPassword = (ClearEditText)findViewById(R.id.wlan_passwd_editText);

        mEyeImageButton = (EyeImageButton) findViewById(R.id.eye_button_wlan_passwd);
        mEyeImageButton.setEditText(mEditTextWlanPassword);

        mButtonApConfig = (Button)findViewById(R.id.button_ap_config);
        mButtonApConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wlan_pwd = mEditTextWlanPassword.getText().toString();

                if (mEditTextWlanName.getText().toString().isEmpty())
                {
                    ToolKits.showMessage(ApConfigActivity.this, res.getString(R.string.please_input_wlan_ssid));
                    return;
                }

                if(wlan_pwd.isEmpty()) {
                    ToolKits.showMessage(ApConfigActivity.this, res.getString(R.string.please_input_init_passwd));
                    return;
                }

                ApConfigActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText(res.getString(R.string.setting_ap_config));
                        mButtonApConfig.setEnabled(false);
                    }
                });

                new Thread(new APConfigRunnable()).start();
            }
        });

        mTextViewStatus = (TextView)findViewById(R.id.ap_config_status);
    }


    private class APConfigRunnable implements Runnable {
        @Override
        public void run() {
            final boolean bRet = apCofing(wlan_name, wlan_pwd, nEncryption);

            ApConfigActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bRet) {
                        mTextViewStatus.setText(res.getString(R.string.ap_config_completed));
                        mButtonApConfig.setEnabled(true);
                        ToolKits.showMessage(ApConfigActivity.this, res.getString(R.string.ap_config_completed));
                    } else if(!bRet){
                        mTextViewStatus.setText(res.getString(R.string.ap_config_completed));
                        mButtonApConfig.setEnabled(true);
                        ToolKits.showMessage(ApConfigActivity.this, res.getString(R.string.ap_config_completed));
                    }
                }
            });
        }

    }

    private boolean apCofing(String mWlanSSID, String mWlanPwd, int nEncryption) {
        CFG_NETAPP_WLAN stCfg = new CFG_NETAPP_WLAN();

        /**
         * 获取
         */
        boolean zRet = ToolKits.GetDevConfig(FinalVar.CFG_CMD_WLAN, stCfg, NetSDKApplication.getInstance().getLoginHandle(), -1, 10240);
        if (zRet) {

            /**
             * 设置
             */
            //设置wifi名称
//            System.arraycopy(mWlanSSID.getBytes(),0, stCfg.stuWlanInfo[0].szSSID, 0, mWlanSSID.getBytes().length);

            //设置wifi密码
//            System.arraycopy(mWlanPwd.getBytes(),0, stCfg.stuWlanInfo[0].szKeys[0], 0, mWlanPwd.getBytes().length);

            ToolKits.StringToByteArray(mWlanSSID, stCfg.stuWlanInfo[0].szSSID);
            ToolKits.StringToByteArray(mWlanPwd, stCfg.stuWlanInfo[0].szKeys[0]);
            //设置wlan加密方式,// 加密模式, 根据 Encryption.java 获取
            stCfg.stuWlanInfo[0].nEncryption = nEncryption;

            stCfg.stuWlanInfo[0].bEnable = true;  		 // WIFI网卡使能开关
            stCfg.stuWlanInfo[0].bConnectEnable = true; // 手动连接开关, TRUE手动连接, FALSE手动断开
            stCfg.stuWlanInfo[0].nKeyID = 0;              // 秘钥索引, 取值0~3
            stCfg.stuWlanInfo[0].bKeyFlag = false;       // 密码是否已经设置
            stCfg.stuWlanInfo[0].bLinkEnable = true;     // 自动连接开关, TRUE不自动连接, FALSE自动连接, IPC无意义
            String strDnsServers0 = "8.8.8.8";
            String strDnsServers1 = "8.8.4.4";
            //设置DNS
//            System.arraycopy(strDnsServers0.getBytes(), 0, stCfg.stuWlanInfo[0].stuNetwork.szDnsServers[0], 0, strDnsServers0.getBytes().length);
//            System.arraycopy(strDnsServers1.getBytes(), 0, stCfg.stuWlanInfo[0].stuNetwork.szDnsServers[1], 0, strDnsServers1.getBytes().length);

            ToolKits.StringToByteArray(strDnsServers0, stCfg.stuWlanInfo[0].stuNetwork.szDnsServers[0]);
            ToolKits.StringToByteArray(strDnsServers1, stCfg.stuWlanInfo[0].stuNetwork.szDnsServers[1]);
            /// AP配置的时候，设备会直接断网，接口不返回，所以将超时时间设置的稍微小点
            zRet = SetDevConfig(FinalVar.CFG_CMD_WLAN, stCfg, NetSDKApplication.getInstance().getLoginHandle(), -1, 10240);
            if(zRet) {
                ToolKits.writeLog("设置成功");
                return true;
            } else {
                ToolKits.writeErrorLog("设置失败");
            }
        }

        return false;

    }

    public static boolean SetDevConfig(String strCmd ,  Object cmdObject , long hHandle , int nChn , int nBufferLen )
    {
        boolean result = false;
        Integer error = new Integer(0);
        Integer restart = new Integer(0);
        char szBuffer[] = new char[nBufferLen];
        for(int i=0; i<nBufferLen; i++)szBuffer[i]=0;

        if(INetSDK.PacketData(strCmd, cmdObject, szBuffer, nBufferLen))
        {
            if( INetSDK.SetNewDevConfig(hHandle,strCmd , nChn , szBuffer, nBufferLen, error, restart, 3000))
            {
                result = true;
            }
            else
            {
                ToolKits.writeErrorLog("Set " + strCmd + " Config Failed!");
                result = false;
            }
        }
        else
        {
            ToolKits.writeErrorLog("Packet " + strCmd + " Config Failed!");
            result = false;
        }

        return result;
    }

    @Override
    protected void onPause() {
        wlan_name = "";
        wlan_pwd = "";
        wlan_authMode = 0;
        wlan_encrAlgr = 0;
        nEncryption = 0;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        wlan_name = "";
        wlan_pwd = "";
        wlan_authMode = 0;
        wlan_encrAlgr = 0;
        nEncryption = 0;
        super.onDestroy();
    }
}
