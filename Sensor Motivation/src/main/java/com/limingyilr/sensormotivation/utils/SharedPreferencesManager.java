package com.limingyilr.sensormotivation.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lanqx on 2014/5/8.
 */
public class SharedPreferencesManager {
	public static int MODE = Context.MODE_WORLD_READABLE
			+ Context.MODE_WORLD_WRITEABLE;
	public static String PREFERENCE_NAME;

    public final static String LOG_SWITCH = "LOG_SWITCH";
	public final static String GRAVITY_SWITCH = "GRAVITY_SWITCH";
    public final static String SENSITIVITY = "SENSITIVITY";

	private Context context;

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	public SharedPreferencesManager(Context context) {
		this.context = context;
		this.MODE = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
		this.PREFERENCE_NAME = "SaveSetting";
		this.sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
				MODE);
		this.editor = sharedPreferences.edit();
	}

    public void saveLogSwitch(boolean isChecked) {
        editor.putBoolean(LOG_SWITCH, isChecked);
        editor.commit();
    }

    public boolean readLogSwitch() {
        return sharedPreferences.getBoolean(LOG_SWITCH, false);
    }
	public void saveGravitySwitch(boolean isChecked) {
		editor.putBoolean(GRAVITY_SWITCH, isChecked);
		editor.commit();
	}

	public boolean readGravitySwitch() {
		return sharedPreferences.getBoolean(GRAVITY_SWITCH, false);
	}

    public void saveSensitivity(int Sensitivity) {
        editor.putInt(SENSITIVITY, Sensitivity);
        editor.commit();
    }
    public int readSensitivity() {
        return sharedPreferences.getInt(SENSITIVITY, 12);
    }

	public void initSetting() {
        saveGravitySwitch(false);
	}
}
