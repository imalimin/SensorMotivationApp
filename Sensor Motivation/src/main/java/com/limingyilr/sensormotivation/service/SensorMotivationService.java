package com.limingyilr.sensormotivation.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.limingyilr.sensormotivation.dao.Reflector;
import com.limingyilr.sensormotivation.utils.LockUtil;
import com.limingyilr.sensormotivation.utils.LogText;
import com.limingyilr.sensormotivation.utils.SharedPreferencesManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by 翌日黄昏 on 2014/9/22.
 */
public class SensorMotivationService extends Service {
    private static final String TAG = "SensorMotivationService";
    private static final int SENSOR_SHAKE = 10;
    private static final int Time_Dalta = 300;
    private static final int Thread_Dalta = 1000;
//    private int Sensitivity = 12;

    private SensorMotivationThread sensorMotivationThread;

    private SharedPreferencesManager preferencesManager;
    private SensorManager sensorManager;
    private LogText logText;
    private float x = 0; // x轴方向的重力加速度，向右为正
    private float y = 0; // y轴方向的重力加速度，向前为正
    private float z = 0; // z轴方向的重力加速度，向上为正

    private long sensorDataTime = -1;
    private ScreenBroadcastReceiver mScreenReceiver;
    private boolean isScreenOn = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesManager = new SharedPreferencesManager(SensorMotivationService.this);
        sensorMotivationThread = new SensorMotivationThread(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        logText = new LogText("log_SensorMotivation");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorMotivationThread.start();
        startScreenBroadcastReceiver();
        registerListener();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sensorMotivationThread.stop();
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
        this.unregisterReceiver(mScreenReceiver);
        super.onDestroy();
    }

    private class SensorMotivationThread {

        private String threadName = "workThread";

        private Context context;
        private Thread workThread;
        private boolean running = true;

        public SensorMotivationThread(Context context) {
            this.context = context;
            this.workThread = new Thread(null, runnable, threadName);
        }

        private Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (running) {
                    long dalta = System.currentTimeMillis() - sensorDataTime;
                    Date curDate = new Date(System.currentTimeMillis());
                    if(curDate.getMinutes()%10 == 0) {
                        logText.addLog("\n" + dalta);
                    }
                    Log.v(TAG, "Runnable");
                    if(sensorDataTime > 0 && dalta > Time_Dalta) {
                        Log.v(TAG, dalta + "");
                        registerListener();
                    }

                    if(isScreenOn) {
                        try {
                            Thread.sleep(2*60*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            Thread.sleep(Thread_Dalta);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        public void start() {
            if (!workThread.isAlive()) {
                workThread.start();
                running = true;
            }
        }

        public void stop() {
            if (workThread.isAlive()) {
                workThread.interrupt();
                running = false;
            }
        }

        public Thread getWorkThread() {
            return workThread;
        }
    }

    private void startScreenBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mScreenReceiver = new ScreenBroadcastReceiver();
        this.registerReceiver(mScreenReceiver, filter);
    }

    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            sensorDataTime = System.currentTimeMillis();
            float[] values = event.values;
            x = values[0]; // x轴方向的重力加速度，向右为正
            y = values[1]; // y轴方向的重力加速度，向前为正
            z = values[2]; // z轴方向的重力加速度，向上为正
            if (preferencesManager.readLogSwitch()) {
                logText.addLog("\nx:" + x + "\ny:" + y + "\nz:" + z);
            }
//            Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
//            int medumValue = 14;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
            if (Math.abs(x) > preferencesManager.readSensitivity()) {
                Message msg1 = new Message();
                msg1.what = SENSOR_SHAKE;
                handler.sendMessage(msg1);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 动作执行
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENSOR_SHAKE:
                    LockUtil.wakeUpAndUnlock(SensorMotivationService.this);
                    break;
            }
        }
    };

    private void registerListener() {
        if (sensorManager != null) {// 注册监听器
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;


        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                // 开屏
                isScreenOn = true;
                Log.v(TAG, "Screen On");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // 锁屏
                isScreenOn = false;
                Log.v(TAG, "Screen Off");
                sensorMotivationThread.getWorkThread().interrupt();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                // 解锁
            }
        }
    }

}
