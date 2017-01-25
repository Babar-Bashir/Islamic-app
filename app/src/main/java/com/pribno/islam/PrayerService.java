package com.pribno.islam;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PrayerService extends Service {
	Long currentTime;
	private String PREF_NAME = "prayerPref";
	SharedPreferences pref;
	private int STATE_OF_CURRENT_MODE = 0;
	private AudioManager audioManager;
	int i = 0;
	Calendar cal;
	PrayerManager prayerManager;
	Context con;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		pref = getSharedPreferences(PREF_NAME, 0);
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		prayerManager = new PrayerManager(getBaseContext());
		Toast.makeText(getBaseContext(), "oncrate()", Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Toast.makeText(getBaseContext(), "onstartCommand() milliseconds"+currentTime, Toast.LENGTH_SHORT).show();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				currentTime = System.currentTimeMillis();
				Log.d(ContractConstants.TAG, "Start menthod Run()");
				switch (prayerManager.checkPrayerTimeDuration(currentTime)) {
				case -1:
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					break;

				default:
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					
					break;
				}
//				checkPrayerTimeDuration(currentTime);
				
							Log.d(ContractConstants.TAG, "MilliSeconds is : "+currentTime.toString());
							Log.d(ContractConstants.TAG, "MilliSeconds is : "+i);
			}
		};
		timer.scheduleAtFixedRate(task,0, 1000);
		return START_STICKY;
	} 
	
	}

