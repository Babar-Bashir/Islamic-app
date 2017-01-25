package com.pribno.islam;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class GooglePlacesReadTask extends AsyncTask<String, Integer, String> {
	private SharedPreferences pref;
    private String googlePlacesData = null;
    private Editor edit; 
    public GooglePlacesReadTask(Context con) {
    	pref = con.getSharedPreferences(ContractConstants.NEARBY_PREF, Context.MODE_PRIVATE);
	}
    
    @Override
    protected String doInBackground(String... inputObj) {
    	edit = pref.edit();
        try {
            String googlePlacesUrl = (String) inputObj[0];
            Http http = new Http();
            Places places = new Places();
            googlePlacesData = http.read(googlePlacesUrl);
            
            Log.d(ContractConstants.TAG, "[GooglePlacesReadTask] : \n"+googlePlacesData.toString());
            
            
            edit.putString(ContractConstants.NEARBY_MOSQUE_PREF, googlePlacesData.toString());
            edit.commit();
            
            
            
            JSONObject PlacesJson	 = new JSONObject(googlePlacesData);
            List<HashMap<String, String>> listPlaces = places.parse(PlacesJson);
            for (HashMap<String, String> hashMap : listPlaces) {
				Log.d(ContractConstants.TAG, hashMap.get("place_name"));
			}
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        
    }
}