package com.mozhimen.camerak.dahua.exam.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mozhimen.camerak.dahua.exam.R;
import com.mozhimen.camerak.dahua.exam.common.ClearEditText;
import com.mozhimen.camerak.dahua.exam.common.DialogProgress;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;
import com.mozhimen.camerak.dahua.exam.module.AlarmPushModule;

public class AlarmPushActivity extends AppCompatActivity {
    private static final String TAG = "AlarmPushActivity";
    private boolean isPush = false;
    private SharedPreferences mSharedPrefs;
    private AlarmPushModule mAlarmPushModule;
    private Button btnSubAlarm;
    private Button btnUnSubAlarm;
    private DialogProgress mDialogProgress;
    private ClearEditText mDevicanameEditText;
    private String mDevicename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_push);
        setTitle(R.string.activity_function_list_alarm_push);
        mAlarmPushModule = new AlarmPushModule(this);
        // 添加返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupView() {
        mDialogProgress = new DialogProgress(this);
        mDevicanameEditText = (ClearEditText)findViewById(R.id.editTextDeviceName);
        btnSubAlarm = (Button)findViewById(R.id.buttonSubAlarm);
        btnUnSubAlarm = (Button)findViewById(R.id.buttonUnSubAlarm);

        btnSubAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPush = true;
                mDevicename = mDevicanameEditText.getText().toString();
                new AlarmPushTask().execute();
            }
        });

        btnUnSubAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPush = false;
                new AlarmPushTask().execute();
            }
        });
    }

    private class AlarmPushTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialogProgress.setMessage(getString(R.string.waiting));
            mDialogProgress.setSpinnerType(DialogProgress.FADED_ROUND_SPINNER);
            mDialogProgress.setCancelable(false);
            mDialogProgress.show();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            if (isPush) {
                return mAlarmPushModule.subscribe(mDevicename);
            }else {
                return mAlarmPushModule.unsubscribe();
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            mDialogProgress.dismiss();
            ToolKits.showMessage(AlarmPushActivity.this, getString(mAlarmPushModule.getResId()));
        }
    }
}
