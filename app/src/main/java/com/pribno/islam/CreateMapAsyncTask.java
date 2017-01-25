package com.pribno.islam;

import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.os.AsyncTask;

public class CreateMapAsyncTask extends AsyncTask<Void, Void, Void> {
	private GoogleMap g_map;
	private Activity con;
	public CreateMapAsyncTask(GoogleMap g_map, Activity con) {
		this.g_map = g_map;
		this.con = con;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}

}
