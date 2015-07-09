package com.limingyilr.sensormotivation.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.limingyilr.sensormotivation.R;
import com.limingyilr.sensormotivation.dao.Reflector;
import com.limingyilr.sensormotivation.service.SensorMotivationService;
import com.limingyilr.sensormotivation.utils.SharedPreferencesManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    private SharedPreferencesManager preferencesManager;
    private TableRow gravityWakeUp;
    private Switch gravitySwitch;
    private Switch logSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new SharedPreferencesManager(MainActivity.this);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        gravityWakeUp = (TableRow) findViewById(R.id.main_gravity);
        gravitySwitch = (Switch) findViewById(R.id.main_gravity_switch);
        logSwitch = (Switch) findViewById(R.id.main_log_switch);

        gravityWakeUp.setOnClickListener(mListener);
        gravitySwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        logSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);

        initStatus();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initStatus() {
        gravitySwitch.setChecked(preferencesManager.readGravitySwitch());
        logSwitch.setChecked(preferencesManager.readLogSwitch());
        Intent intent = new Intent(MainActivity.this, SensorMotivationService.class);
        if (gravitySwitch.isChecked()) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.main_gravity:
                    intent = new Intent(MainActivity.this, SensorSettingActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.main_gravity_switch:
                    preferencesManager.saveGravitySwitch(isChecked);
                    if (isChecked) {
                        Intent intent = new Intent(MainActivity.this, SensorMotivationService.class);
                        startService(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, SensorMotivationService.class);
                        stopService(intent);
                    }
                    break;
                case R.id.main_log_switch:
                    preferencesManager.saveLogSwitch(isChecked);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
