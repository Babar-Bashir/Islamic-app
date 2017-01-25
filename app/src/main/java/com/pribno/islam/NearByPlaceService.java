package com.pribno.islam;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class NearByPlaceService extends Service {
	private SharedPreferences pref;
	@Override
	public void onCreate() {
		super.onCreate();
		pref = getSharedPreferences(ContractConstants.LocatioPref, Context.MODE_PRIVATE);
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		double lat = Double.parseDouble(pref.getString(ContractConstants.LatPref, "0.0"));
		double lng = Double.parseDouble(pref.getString(ContractConstants.LngPref, "0.0"));
		String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
				"json?location="+lat+","+lng+
				"&radius=3000&sensor=true" +
				"&types=mosque"+
				"&key=AIzaSyBZ3WHXZfZR6AhWciNUEKah55wynv9mDHw";
		
		new GooglePlacesReadTask(this).execute(placesSearchStr);
		
		return START_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	

}
