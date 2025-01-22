package com.mozhimen.camerak.dahua.exam.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.company.NetSDK.CB_fSearchDevicesCB;
import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.mozhimen.camerak.dahua.exam.R;
import com.mozhimen.camerak.dahua.exam.common.ClearEditText;
import com.mozhimen.camerak.dahua.exam.common.DialogProgress;
import com.mozhimen.camerak.dahua.exam.common.EyeImageButton;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;
import com.mozhimen.camerak.dahua.exam.module.DeviceSearchModule;
import com.mozhimen.camerak.dahua.exam.module.WIFIConfigurationModule;
import com.mozhimen.camerak.dahua.exam.zxing.activity.CaptureActivity;

public class WIFIConfigurationActivity extends AppCompatActivity implements View.OnClickListener{
    private ClearEditText mSnEditText;
    private ClearEditText mSsidEditText;
    private ClearEditText mPwdEditText;
    private EyeImageButton mEyeImageButton;
    private ImageButton mScanImageButton;
    private TextView mDeviceInfoTextView;

    private WIFIConfigurationModule mConfigModule;
    private DeviceSearchModule mDeviceSearchModule;
    private DialogProgress mDialogProgress;
    Resources res;

    private final int WIFI_CONFIG = 0x11;
    private String snStr;
    private String ssidStr;
    private String pwdStr;

    SmartConfigTask smartconfigTask;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_CONFIG:  // wifi
                    mDeviceInfoTextView.setText((String)msg.obj);

                    if(!smartconfigTask.isCancelled()) {
                        smartconfigTask.cancel(false);
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private CB_fSearchDevicesCB callback = new  CB_fSearchDevicesCB(){

        @Override
        public void invoke(DEVICE_NET_INFO_EX device_net_info_ex) {
            String temp = res.getString(R.string.config_succeed) + "\n" +
                          res.getString(R.string.activity_iplogin_device_ip) + " : "+ new String(device_net_info_ex.szIP).trim();

            ///Wifi配置判断
            if(new String(device_net_info_ex.szSerialNo).trim().equals(snStr)
                    && (device_net_info_ex.iIPVersion == 4)) {

                Message msg = mHandler.obtainMessage(WIFI_CONFIG);
                msg.obj = temp;
                mHandler.sendMessage(msg);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonfiguration);
        setTitle(R.string.activity_main_wifi_config);
        mConfigModule = new WIFIConfigurationModule(this);
        mDeviceSearchModule = new DeviceSearchModule(this);
        mDialogProgress = new DialogProgress(this);
        res = this.getResources();

        // 添加返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setSystemVolumeMax();
        setupView();
        getWIFIInfo();
        ToolKits.verifyCameraPermissions(WIFIConfigurationActivity.this);
    }

    private void setupView(){
        mSnEditText = (ClearEditText)findViewById(R.id.sn_et);
        mSsidEditText = (ClearEditText)findViewById(R.id.ssid_et);
        mPwdEditText = (ClearEditText)findViewById(R.id.pwd_et);

        mScanImageButton = ((ImageButton)findViewById(R.id.scan_button));
        mScanImageButton.setOnClickListener(this);

        mEyeImageButton = (EyeImageButton)findViewById(R.id.eye_button_passwd);
        mEyeImageButton.setEditText(mPwdEditText);

        ((Button)findViewById(R.id.config_start)).setOnClickListener(this);

        mDeviceInfoTextView = (TextView)findViewById(R.id.config_device_info);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {    ///Wifi配置与声波配对的成功与否，是通过设备搜索判断的
        int id = v.getId();
        if (id == R.id.config_start){    ///wifi配置
            if(checkConfigEditText()) {
                if(getWIFIInfo().equals(ssidStr)) {
                    smartconfigTask = new SmartConfigTask();
                    smartconfigTask.execute();
                } else {
                    ToolKits.showMessage(WIFIConfigurationActivity.this, res.getString(R.string.please_check_wlan_ssid));
                }
            }
        } else if(id == R.id.scan_button) {
            startActivityForResult(new Intent(WIFIConfigurationActivity.this, CaptureActivity.class), 0);
        }
    }

    private class SmartConfigTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialogProgress.setMessage(res.getString(R.string.configing));
            mDialogProgress.setSpinnerType(DialogProgress.FADED_ROUND_SPINNER);
            mDialogProgress.show();
            mDeviceInfoTextView.setText("");
        }
        @Override
        protected Boolean doInBackground(String... params) {
            mDeviceSearchModule.stopSearchDevices();
            mDeviceSearchModule.startSearchDevices(callback);
            mConfigModule.startSearchIPCWifi(snStr, ssidStr, pwdStr);

            for(int i = 0; i < 400; i++) {
                try {
                    if(smartconfigTask.isCancelled()) {
                        break;
                    }
                    if(!mDialogProgress.isShowing()) {
                        smartconfigTask.cancel(false);
                        break;
                    }

                    Thread.sleep(250);

                    // 5秒发一次搜索命令
                    if(i % 20 == 0) {
                        mDeviceSearchModule.stopSearchDevices();
                        mDeviceSearchModule.startSearchDevices(callback);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(mDeviceSearchModule.lDevSearchHandle != 0) {
                mConfigModule.stopSearchIPCWifi();
                mDeviceSearchModule.stopSearchDevices();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            mDialogProgress.dismiss();
            if(!result) {
                ToolKits.showMessage(WIFIConfigurationActivity.this, res.getString(R.string.config_failed));
            }
        }

        @Override
        protected void onCancelled() {
            if(mDialogProgress.isShowing()) {
                mDialogProgress.dismiss();
            }
        }
    }

    ///获取连接的wlan的名称，并设置到界面
    private String getWIFIInfo() {
        ///判断wifi状态
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
            ///如果没连接wifi，设为空
            mSsidEditText.setText("");
            return "";
        }

        ///如果连接了wifi，获取当前wifi名称
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String nowSSID = "";
        if (wifiInfo != null) {
            nowSSID = wifiInfo.getSSID().replace("\"", "");
        }
        mSsidEditText.setText(nowSSID);
        return nowSSID;
    }

    ///修改系统音量
    private void setSystemVolumeMax() {
        try {
            AudioManager  mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
        } catch (Exception e) {
            Log.e("lc Exception", "init audio error", e);
        }
    }

    ///检查序列号与wlan名称是否输入
    public boolean checkConfigEditText() {
        snStr = mSnEditText.getText().toString();
        ssidStr = mSsidEditText.getText().toString();
        pwdStr = mPwdEditText.getText().toString();

        if(snStr.length() == 0) {
            ToolKits.showMessage(WIFIConfigurationActivity.this, res.getString(R.string.please_input_device_sn));
            return false;
        }

        if(ssidStr.length() == 0) {
            ToolKits.showMessage(WIFIConfigurationActivity.this, res.getString(R.string.please_input_wlan_ssid));
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = "";
            Bundle bundle = data.getExtras();
            if (bundle != null){
                result = bundle.getString("result");
                if(result.contains(",")) {
                    result =  result.split(",")[0].split(":")[1];
                }
            }
            mSnEditText.setText(result);
        }
    }

    @Override
    protected void onDestroy(){
        if(smartconfigTask != null && smartconfigTask.getStatus() == AsyncTask.Status.RUNNING) {
            smartconfigTask.cancel(false);
        }

        mConfigModule.stopSearchIPCWifi();
        mDeviceSearchModule.stopSearchDevices();
        mConfigModule = null;
        mDeviceSearchModule = null;

        super.onDestroy();
    }
}
