package com.smsparking.parkmanobjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferanceManager {
	// Mode
	private final int PRIVATE_MODE = 0;

	// Name
	private final String PREF_NAME = "ParkManPref";

	private SharedPreferences pref;

	// Editor for Shared preferences
	private Editor editor;

	// Context
	private Context _context;

	public PreferanceManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void addStringPref(String key, String value) {
		editor.putString(key, value);
		commit();
	}
	public void addBooleanPref(String key, Boolean value) {
		editor.putBoolean(key, value);
		commit();
	}

	public void addIntegerpref(String key, int value) {
		editor.putInt(key, value);
		commit();
	}

	public String getStringPref(String key) {
		try {
			return pref.getString(key, null);
		} catch (Exception e) {
			return null;
		}
	}
	public boolean getBooleanPref(String key) {
		try {
			return pref.getBoolean(key, false);
		} catch (Exception e) {
			return false;
		}
	}

	public int getIntegerPref(String key) {
		try {
			return pref.getInt(key, 0);
		} catch (Exception e) {
			return 0;
		}
	}

	public void removeKey(String key) {
		try {
			editor.remove(key); // delete key
			commit();
		} catch (Exception e) {
		}
	}

	public void clearPreferance() {
		try {
			editor.clear();// clear changes
			commit();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void commit() {
		editor.commit();
	}

}