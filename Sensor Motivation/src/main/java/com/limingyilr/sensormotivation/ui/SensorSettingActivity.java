package com.limingyilr.sensormotivation.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.limingyilr.sensormotivation.R;
import com.limingyilr.sensormotivation.service.SensorMotivationService;
import com.limingyilr.sensormotivation.utils.SharedPreferencesManager;

public class SensorSettingActivity extends ActionBarActivity {
    private static final String TAG = "SensorSettingActivity";
    private static final int BaseSensitivity = 10;
    private TextView SensitivityText;
    private SeekBar seekBar;
    private SharedPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new SharedPreferencesManager(SensorSettingActivity.this);
        setContentView(R.layout.activity_sensor_setting);
        initView();
    }

    private void initView() {
        SensitivityText = (TextView) findViewById(R.id.sensor_setting_sensitivity);
        seekBar = (SeekBar) findViewById(R.id.sensor_setting_sensitivity_seekbar);

        seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        initStatus();
    }

    private void initStatus() {
        SensitivityText.setText(preferencesManager.readSensitivity() + "");
        seekBar.setProgress(preferencesManager.readSensitivity() - BaseSensitivity);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            preferencesManager.saveSensitivity(BaseSensitivity + progress);
            SensitivityText.setText(BaseSensitivity + progress + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
