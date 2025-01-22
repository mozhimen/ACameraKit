package com.mozhimen.camerak.dahua.exam.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mozhimen.camerak.dahua.exam.R;
import com.mozhimen.camerak.dahua.exam.common.ToolKits;
import com.mozhimen.camerak.dahua.exam.module.AlarmContrlModule;

public class AlarmControlActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    AlarmContrlModule mAlarmContrlModule;
    NetSDKApplication app;
    Spinner mSelectAlarmInputChn;
    Spinner mSelectAlarmInputState;
    Spinner mSelectAlarmOutputChn;
    Spinner mSelectAlarmOutputState;
    Spinner mSelectAlarmTriggerChn;
    Spinner mSelectAlarmTriggerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_control);
        setTitle(R.string.activity_function_list_alarm_control);

        // 添加返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAlarmContrlModule = new AlarmContrlModule(this);
        app = (NetSDKApplication) getApplication();
        setupView();
        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setupView() {
        mSelectAlarmInputChn = (Spinner) findViewById(R.id.spinner_alarm_input_channel);
        mSelectAlarmInputState = (Spinner) findViewById(R.id.spinner_alarm_input_state);
        mSelectAlarmOutputChn = (Spinner) findViewById(R.id.spinner_alarm_output_channel);
        mSelectAlarmOutputState = (Spinner) findViewById(R.id.spinner_alarm_output_state);
        mSelectAlarmTriggerChn = (Spinner) findViewById(R.id.spinner_alarm_trigger_channel);
        mSelectAlarmTriggerMode = (Spinner) findViewById(R.id.spinner_alarm_trigger_mode);

        ((Button) findViewById(R.id.inputgetbutton)).setOnClickListener(this);
        ((Button) findViewById(R.id.inputsetbutton)).setOnClickListener(this);
        ((Button) findViewById(R.id.outputgetbutton)).setOnClickListener(this);
        ((Button) findViewById(R.id.outputsetbutton)).setOnClickListener(this);
        ((Button) findViewById(R.id.triggergetbutton)).setOnClickListener(this);
        ((Button) findViewById(R.id.triggersetbutton)).setOnClickListener(this);
    }

    private void initView() {
        ///Alarm Input
        ///报警输入
        mSelectAlarmInputChn.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                mAlarmContrlModule.getInputChnList(app.getLoginHandle())));
        mSelectAlarmInputChn.setSelection(0);
        mSelectAlarmInputChn.setOnItemSelectedListener(this);

        mSelectAlarmInputState.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                mAlarmContrlModule.getInputStateList()));
        if (mAlarmContrlModule.nChnIn <= 0) {
            ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_input_control));
        }

        ///Alarm Output
        ///报警输出
        mSelectAlarmOutputChn.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                mAlarmContrlModule.getOutputChnList(app.getLoginHandle())));
        mSelectAlarmOutputChn.setSelection(0);
        mSelectAlarmOutputChn.setOnItemSelectedListener(this);

        mSelectAlarmOutputState.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                mAlarmContrlModule.getOutputStateList()));
        if (mAlarmContrlModule.nChnOut <= 0) {
            ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_output_control));
        }

        ///Alarm Trigger
        ///报警触发
        mSelectAlarmTriggerChn.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                mAlarmContrlModule.getTriggerChnList(app.getLoginHandle())));
        mSelectAlarmTriggerChn.setSelection(0);
        mSelectAlarmTriggerChn.setOnItemSelectedListener(this);

        mSelectAlarmTriggerMode.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                mAlarmContrlModule.getTriggerStateList()));
        if (mAlarmContrlModule.nChnTri <= 0) {
            ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_trigger_control));
        }
    }

    @Override
    protected void onDestroy() {
        mAlarmContrlModule = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int parentID = parent.getId();
        int nSelectGetState = 0;
        if (parentID == R.id.spinner_alarm_input_channel) {
            nSelectGetState = mAlarmContrlModule.getAlarmInputChnState(app.getLoginHandle(), mSelectAlarmInputChn.getSelectedItemPosition());
            mSelectAlarmInputState.setSelection(nSelectGetState, true);
        } else if (parentID == R.id.spinner_alarm_output_channel) {
            nSelectGetState = mAlarmContrlModule.getAlarmOutputChnState(app.getLoginHandle(), mSelectAlarmOutputChn.getSelectedItemPosition());
            mSelectAlarmOutputState.setSelection(nSelectGetState, true);
        } else if (parentID == R.id.spinner_alarm_trigger_channel) {
            nSelectGetState = mAlarmContrlModule.getAlarmTriggerChnState(app.getLoginHandle(), mSelectAlarmTriggerChn.getSelectedItemPosition());
            mSelectAlarmTriggerMode.setSelection(nSelectGetState, true);
        }

        if (nSelectGetState >= 0) {
            ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.get) + getString(R.string.info_success));
        } else {
            ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.get) + getString(R.string.info_failed));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int nButtonGetState = 0;
        boolean bButtonSetState = false;
        int viewId = v.getId();
        if (viewId == R.id.inputgetbutton) {
            if (mAlarmContrlModule.nChnIn > 0) {
                nButtonGetState = mAlarmContrlModule.getAlarmInputChnState(app.getLoginHandle(), mSelectAlarmInputChn.getSelectedItemPosition());
                mSelectAlarmInputState.setSelection(nButtonGetState, true);
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_input_control));
            }
        } else if (viewId == R.id.outputgetbutton) {
            if (mAlarmContrlModule.nChnOut > 0) {
                nButtonGetState = mAlarmContrlModule.getAlarmOutputChnState(app.getLoginHandle(), mSelectAlarmOutputChn.getSelectedItemPosition());
                mSelectAlarmOutputState.setSelection(nButtonGetState, true);
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_output_control));
            }
        } else if (viewId == R.id.triggergetbutton) {
            if (mAlarmContrlModule.nChnTri > 0) {
                nButtonGetState = mAlarmContrlModule.getAlarmTriggerChnState(app.getLoginHandle(), mSelectAlarmTriggerChn.getSelectedItemPosition());
                mSelectAlarmTriggerMode.setSelection(nButtonGetState, true);
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_trigger_control));
            }
        } else if (viewId == R.id.inputsetbutton) {
            if (mAlarmContrlModule.nChnIn > 0) {
                bButtonSetState = mAlarmContrlModule.setAlarmInputChnState(app.getLoginHandle(), mSelectAlarmInputChn.getSelectedItemPosition(),
                        mSelectAlarmInputState.getSelectedItemPosition());
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_input_control));
            }
        } else if (viewId == R.id.outputsetbutton) {
            if (mAlarmContrlModule.nChnOut > 0) {
                bButtonSetState = mAlarmContrlModule.setAlarmOutputChnState(app.getLoginHandle(), mSelectAlarmOutputChn.getSelectedItemPosition(),
                        mSelectAlarmOutputState.getSelectedItemPosition());
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_output_control));
            }
        } else if (viewId == R.id.triggersetbutton) {
            if (mAlarmContrlModule.nChnTri > 0) {
                bButtonSetState = mAlarmContrlModule.setAlarmTriggerChnState(app.getLoginHandle(), mSelectAlarmTriggerChn.getSelectedItemPosition(),
                        mSelectAlarmTriggerMode.getSelectedItemPosition());
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.device_no_support_alarm_trigger_control));
            }
        }

        ///When clicking the corresponding button and corresponding channel is bigger than 0, judge whether the function is successful or not.
        ///当点击对应的按键且对应的通道大于0时，判断功能实现是否成功
        if (((v.getId() == R.id.inputgetbutton) && mAlarmContrlModule.nChnIn > 0)
                || ((v.getId() == R.id.outputgetbutton) && mAlarmContrlModule.nChnOut > 0)
                || ((v.getId() == R.id.triggergetbutton) && mAlarmContrlModule.nChnTri > 0)) {
            if (nButtonGetState >= 0) {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.get) + getString(R.string.info_success));
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.get) + getString(R.string.info_failed));
            }
        }

        if (((v.getId() == R.id.inputsetbutton) && mAlarmContrlModule.nChnIn > 0)
                || ((v.getId() == R.id.outputsetbutton) && mAlarmContrlModule.nChnOut > 0)
                || ((v.getId() == R.id.triggersetbutton) && mAlarmContrlModule.nChnTri > 0)) {
            if (bButtonSetState) {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.set) + getString(R.string.info_success));
            } else {
                ToolKits.showMessage(AlarmControlActivity.this, getString(R.string.set) + getString(R.string.info_failed));
            }
        }
    }
}