package com.pribno.islam;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

public class PrayerManager {
	private SharedPreferences pref;
//	private AudioManager audioManager;
	
	private int num;

	Context context;

	public PrayerManager(Context context) {
		this.context = context;
		pref = context.getSharedPreferences(ContractConstants.PRAY_TIME_PREF, 0);
		num = -1;
	}

	public int checkPrayerTimeDuration(long currentTime) {
		Log.d(ContractConstants.TAG, "[PrayerManager]   :  checkPrayerTimeDuration()  :   Start Time");
		if (currentTime >= (pref.getLong("start" + 0, 0))
				&& currentTime <= pref.getLong("end" + 0, 0)) {
			num = 0;
		} else if (currentTime >= pref.getLong("start" + 1, 0)
				&& currentTime <= pref.getLong("end" + 1, 0)) {
			num = 1;
		} else if (currentTime > pref.getLong("start" + 2, 0)
				&& currentTime <= pref.getLong("end" + 2, 0)) {
			num = 2;
		} else if (currentTime >= pref.getLong("start" + 3, 0)
				&& currentTime <= pref.getLong("end" + 3, 0)) {
			num = 3;
		} else if (currentTime >= pref.getLong("start" + 4, 0)
				&& currentTime <= pref.getLong("end" + 4, 0)) {
			num = 4;
		} else if (currentTime >= pref.getLong("start" + 5, 0)
				&& currentTime <= pref.getLong("end" + 5, 0)) {
			num = 5;
		} else if (currentTime >= pref.getLong("start" + 6, 0)
				&& currentTime <= pref.getLong("end" + 6, 0)) {
			num = 6;
		}else {
			num = -1;
		}
		return num;
	}
}
