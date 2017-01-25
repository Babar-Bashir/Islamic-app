package com.pribno.islam;




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.pribno.islam.adapter.PrayerListAdapter;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PrayerTimeFragment extends ListFragment {
	private Integer[] prayer_icon = {R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
	private String[] prayerName = {"Fajar","Sunrise","Zuhar","Asar","Sunset","Maghrib","Ishaa"};
	private String[] startTime = {"0:0","0:0","0:0","0:0","0:0","0:0","0:0"};
	private String[] endTime = {"0:0","0:0","0:0","0:0","0:0","0:0","0:0"};
	private boolean[] alarmStatus = {true,true,true,true,true,true,true};
	private SharedPreferences sharefPref;
	private SharedPreferences prayer_time_pref;
	private Editor editor;
	private Context con;
	private PrayerListAdapter prayerListAdapter;

	public PrayerTimeFragment(Context con){
		this.con = con;
		
		sharefPref = con.getSharedPreferences(ContractConstants.LocatioPref, Context.MODE_PRIVATE);
		prayer_time_pref = con.getSharedPreferences(ContractConstants.PRAY_TIME_PREF, 0);
		editor = prayer_time_pref.edit();
		double latitude = Double.parseDouble(sharefPref.getString(ContractConstants.LatPref, "0"));
		double longitude = Double.parseDouble(sharefPref.getString(ContractConstants.LngPref, "0"));
		double timezone = getGMT();
		// Test Prayer times here
		PrayTime prayers = new PrayTime();

		prayers.setTimeFormat(0);
		prayers.setCalcMethod(0);
		prayers.setAsrJuristic(0);
		prayers.setAdjustHighLats(1);
		int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
		prayers.tune(offsets);

		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		try {
			ArrayList<String> prayerTimes;
			prayerTimes = prayers.getPrayerTimes(cal,
					latitude, longitude, timezone);
			for (int i = 0; i < offsets.length; i++) {

				String startTimestr = prayerTimes.get(i);
				
				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
				//			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				try {
					Date date = sdf.parse(startTimestr);
					editor.putLong("start"+i, date.getTime());
					editor.putLong("end"+0, date.getTime()+1800000);
					editor.commit();
					SimpleDateFormat changeToTime = new SimpleDateFormat("hh:mm",Locale.getDefault());
					String startTime2 = changeToTime.format(date.getTime());
					startTime[i] = startTime2;
					String endTime2 = changeToTime.format((new Date(date.getTime()+1800000)));
					endTime[i] = endTime2;
				} catch (ParseException e) {
					Log.e(ContractConstants.TAG, "[HomeFragment] : ConvertToDate : "+e.getMessage());
					e.printStackTrace();
				}

			}
		} catch (Exception e1) {
			Log.e(ContractConstants.TAG, "[HomeFragment]  : PrayerTime Array Error   --->"+e1.getMessage());
			e1.printStackTrace();
		}
		prayerListAdapter = new PrayerListAdapter(con, prayer_icon, prayerName, startTime, endTime, alarmStatus);
	}
	private  int getGMT() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -cal.getTimeZone().getOffset(cal.getTimeInMillis()));
		long secondDate = cal.getTime().getTime();
		long currentDate = new Date().getTime();
		int gmt = (int)((currentDate - secondDate)/3600000);
		return gmt;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		this.setListAdapter(prayerListAdapter);

		return rootView;
	}
}
