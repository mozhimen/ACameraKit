package com.mozhimen.camerak.dahua.exam.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mozhimen.camerak.dahua.exam.R;
import com.mozhimen.camerak.dahua.exam.common.ClearEditText;
import com.mozhimen.camerak.dahua.exam.common.DialogProgress;
import com.mozhimen.camerak.dahua.exam.common.EyeImageButton;
import com.mozhimen.camerak.dahua.exam.common.IPLoginModule;
import com.mozhimen.camerak.dahua.exam.common.PrefsConstants;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;

public class IPLoginActivity extends AppCompatActivity {
    private ClearEditText mEditTextAddress;
    private ClearEditText mEditTextPort;
    private ClearEditText mEditTextUsername;
    private ClearEditText mEditTextPassword;
    private CheckBox mCheckBox;
    private EyeImageButton mEyeImageButton;

    private SharedPreferences mSharedPrefs;
    private IPLoginModule mLoginModule;
    private NetSDKApplication app;
    private DialogProgress mDialogProgress;
    private Resources res;

    private String mAddress;
    private String mPort;
    private String mUsername;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iplogin);
        setTitle(R.string.activity_main_ip);

        /// get global data
        app = (NetSDKApplication)getApplication();
        res = getResources();
        mDialogProgress = new DialogProgress(this);
        mLoginModule = new IPLoginModule();

        // 添加返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setupView();
    }

    @Override
    protected void onDestroy() {
        if(null != mLoginModule) {
            mLoginModule.logout();
            mLoginModule = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // while onResume we should logout the device.
        mLoginModule.logout();
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setupView() {
        mEditTextAddress = (ClearEditText)findViewById(R.id.editTextServerIp);
        mEditTextPort = (ClearEditText)findViewById(R.id.editTextServerPort);
        mEditTextUsername = (ClearEditText)findViewById(R.id.editTextUsername);
        mEditTextPassword = (ClearEditText)findViewById(R.id.editTextPassword);

        Button ButtonLogin = (Button)findViewById(R.id.buttonLogin);
        ButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginEditText()) {
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute();
                }
            }
        });
        mCheckBox = (CheckBox)findViewById(R.id.checkBox);
        mEyeImageButton = (EyeImageButton)findViewById(R.id.eye_button_ip);

        mEyeImageButton.setEditText(mEditTextPassword);

        getSharePrefs();
    }

    /// LoginTask 
    private class LoginTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialogProgress.setMessage(res.getString(R.string.logining));
            mDialogProgress.setSpinnerType(DialogProgress.FADED_ROUND_SPINNER);
            mDialogProgress.setCancelable(false);
            mDialogProgress.show();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            return mLoginModule.login(mAddress, mPort, mUsername, mPassword);
        }
        @Override
        protected void onPostExecute(Boolean result){
            mDialogProgress.dismiss();
            if (result) {
                putSharePrefs();

                app.setLoginHandle(mLoginModule.getLoginHandle());
                app.setDeviceInfo(mLoginModule.getDeviceInfo());
                app.setDevName(mLoginModule.getDeviceName());
                startActivity(new Intent(IPLoginActivity.this, FunctionListActivity.class));
            } else {
                ToolKits.showMessage(IPLoginActivity.this, getErrorCode(getResources(), mLoginModule.errorCode()));
            }
        }
    }

    private void putSharePrefs() {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        if (mCheckBox.isChecked()) {
            editor.putString(PrefsConstants.LOGIN_IP, mAddress);
            editor.putString(PrefsConstants.LOGIN_PORT, mPort);
            editor.putString(PrefsConstants.LOGIN_USERNAME, mUsername);
            editor.putString(PrefsConstants.LOGIN_PASSWORD, mPassword);
            editor.putBoolean(PrefsConstants.LOGIN_CHECK, true);
        }
        editor.apply();
    }

    private void getSharePrefs() {
        mSharedPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        mEditTextAddress.setText(mSharedPrefs.getString(PrefsConstants.LOGIN_IP, ""));
        mEditTextPort.setText(mSharedPrefs.getString(PrefsConstants.LOGIN_PORT, ""));
        mEditTextUsername.setText(mSharedPrefs.getString(PrefsConstants.LOGIN_USERNAME, ""));
        mEditTextPassword.setText(mSharedPrefs.getString(PrefsConstants.LOGIN_PASSWORD, ""));
        //mCheckBox.setChecked(mSharedPrefs.getBoolean(PrefsConstants.LOGIN_CHECK, false));

        editor.apply();
    }

    private boolean checkLoginEditText() {
        mAddress = mEditTextAddress.getText().toString();
        mPort = mEditTextPort.getText().toString();
        mUsername = mEditTextUsername.getText().toString();
        mPassword = mEditTextPassword.getText().toString();

        if(mAddress.length() == 0) {
            ToolKits.showMessage(IPLoginActivity.this, res.getString(R.string.activity_iplogin_ip_empty));
            return false;
        }
        if(mPort.length() == 0) {
            ToolKits.showMessage(IPLoginActivity.this, res.getString(R.string.activity_iplogin_port_empty));
            return false;
        }
        if(mUsername.length() == 0) {
            ToolKits.showMessage(IPLoginActivity.this, res.getString(R.string.activity_iplogin_username_empty));
            return false;
        }
        if(mPassword.length() == 0) {
            ToolKits.showMessage(IPLoginActivity.this, res.getString(R.string.activity_iplogin_password_empty));
            return false;
        }

        try {
            Integer.parseInt(mPort);
        } catch (Exception e) {
            e.printStackTrace();
            ToolKits.showMessage(IPLoginActivity.this, res.getString(R.string.activity_iplogin_port_err));
            return false;
        }

        return true;
    }

    public static String getErrorCode(Resources res, int errorCode) {
        switch(errorCode) {
            case IPLoginModule.NET_USER_FLASEPWD_TRYTIME:
                return res.getString(R.string.NET_USER_FLASEPWD_TRYTIME);
            case IPLoginModule.NET_LOGIN_ERROR_PASSWORD:
                return res.getString(R.string.NET_LOGIN_ERROR_PASSWORD);
            case IPLoginModule.NET_LOGIN_ERROR_USER:
                return res.getString(R.string.NET_LOGIN_ERROR_USER);
            case IPLoginModule.NET_LOGIN_ERROR_TIMEOUT:
                return res.getString(R.string.NET_LOGIN_ERROR_TIMEOUT);
            case IPLoginModule.NET_LOGIN_ERROR_RELOGGIN:
                return res.getString(R.string.NET_LOGIN_ERROR_RELOGGIN);
            case IPLoginModule.NET_LOGIN_ERROR_LOCKED:
                return res.getString(R.string.NET_LOGIN_ERROR_LOCKED);
            case IPLoginModule.NET_LOGIN_ERROR_BLACKLIST:
                return res.getString(R.string.NET_LOGIN_ERROR_BLACKLIST);
            case IPLoginModule.NET_LOGIN_ERROR_BUSY:
                return res.getString(R.string.NET_LOGIN_ERROR_BUSY);
            case IPLoginModule.NET_LOGIN_ERROR_CONNECT:
                return res.getString(R.string.NET_LOGIN_ERROR_CONNECT);
            case IPLoginModule.NET_LOGIN_ERROR_NETWORK:
                return res.getString(R.string.NET_LOGIN_ERROR_NETWORK);
            default:
               return res.getString(R.string.NET_ERROR);
        }
    }
}
