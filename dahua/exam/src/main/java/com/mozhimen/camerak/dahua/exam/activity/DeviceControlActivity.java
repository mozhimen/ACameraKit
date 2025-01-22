package com.mozhimen.camerak.dahua.exam.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mozhimen.camerak.dahua.exam.R;
import com.mozhimen.camerak.dahua.exam.common.DialogProgress;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;
import com.mozhimen.camerak.dahua.exam.module.DeviceControlModule;

public class DeviceControlActivity extends AppCompatActivity implements View.OnClickListener {

    DialogProgress mDialogProgress;
    AlertDialog.Builder builder;
    private String deviceTime = null;
    private DeviceControlModule mDeviceControlModule;
    private DeviceControlType current = DeviceControlType.DEVICE_CONTROL_UNKNOWN;

    private enum DeviceControlType {
        DEVICE_CONTROL_UNKNOWN,
        DEVICE_CONTROL_REBOOT,
        DEVICE_CONTROL_SET_TIME,
        DEVICE_CONTROL_GET_TIME,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);
        setTitle(R.string.activity_function_list_device_control);

        mDialogProgress = new DialogProgress(this);
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.device_control_reboot_tips);
        mDeviceControlModule = new DeviceControlModule(this);

        // 添加返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setupView() {
        ((Button) findViewById(R.id.buttonReStart)).setOnClickListener(this);
        ((Button) findViewById(R.id.buttonSetUpTime)).setOnClickListener(this);
        ((Button) findViewById(R.id.buttonGetTime)).setOnClickListener(this);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                current = DeviceControlType.DEVICE_CONTROL_REBOOT;
                DeviceControlTask rebootTask = new DeviceControlTask();
                rebootTask.execute();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    /// DeviceControlTask
    private class DeviceControlTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialogProgress.setMessage(getString(R.string.waiting));
            mDialogProgress.setSpinnerType(DialogProgress.FADED_ROUND_SPINNER);
            mDialogProgress.setCancelable(false);
            mDialogProgress.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean bRet = false;
            switch (current) {
                case DEVICE_CONTROL_REBOOT:
                    bRet = mDeviceControlModule.reboot();
                    break;
                case DEVICE_CONTROL_SET_TIME:
                    bRet = mDeviceControlModule.setTime();
                    try {
                        Thread.sleep(100); // 做延时
                    } catch (InterruptedException e) {
                    }
                    break;
                case DEVICE_CONTROL_GET_TIME:
                    deviceTime = mDeviceControlModule.getTime();
                    if (deviceTime != null) {
                        bRet = true;
                    }
                    try {
                        Thread.sleep(200); // 做延时
                    } catch (InterruptedException e) {
                    }
                    break;
                default:
                    break;
            }
            return bRet;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialogProgress.dismiss();
            if (result) {
                switch (current) {
                    case DEVICE_CONTROL_REBOOT:
                    case DEVICE_CONTROL_SET_TIME:
                        ToolKits.showMessage(DeviceControlActivity.this, getString(mDeviceControlModule.getResId()));
                        break;
                    case DEVICE_CONTROL_GET_TIME:
                        if (deviceTime != null) {
                            ((TextView) findViewById(R.id.textGetTime)).setText(getString(R.string.device_time) + " : " + deviceTime);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                ToolKits.showMessage(DeviceControlActivity.this, getString(mDeviceControlModule.getResId()));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonReStart) {
            builder.show();
        } else if (id == R.id.buttonSetUpTime) {
            current = DeviceControlType.DEVICE_CONTROL_SET_TIME;
            DeviceControlTask setTimeTask = new DeviceControlTask();
            setTimeTask.execute();
        } else if (id == R.id.buttonGetTime) {
            current = DeviceControlType.DEVICE_CONTROL_GET_TIME;
            DeviceControlTask getTimeTask = new DeviceControlTask();
            getTimeTask.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mDeviceControlModule = null;
        builder = null;
        super.onDestroy();
    }
}
