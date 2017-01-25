package com.pribno.islam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SplashActivity extends Activity {
	private Thread splashThread;
	private SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		pref = getSharedPreferences(ContractConstants.LocatioPref, 0);
		try {
			startService(new Intent(SplashActivity.this, FusedLocationService.class));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		splashThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(5*1000);
					startService(new Intent(SplashActivity.this, NearByPlaceService.class));
					Log.d(ContractConstants.TAG, "Latitude is : "+pref.getString(ContractConstants.LatPref, "Null"));
					Log.d(ContractConstants.TAG, "Longitude is : "+pref.getString(ContractConstants.LngPref, "Null"));
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				} catch (InterruptedException e) {
					Log.e(ContractConstants.TAG, "[SplashScreen] : Sleep Error : "+e.getMessage());
					e.printStackTrace();
					
				}
			}
		});
		splashThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
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
