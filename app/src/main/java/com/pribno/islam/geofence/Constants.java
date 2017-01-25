/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pribno.islam.geofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pribno.islam.ContractConstants;
import com.pribno.islam.Places;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

/**
 * Created by lmoroney on 12/17/14.
 */
public final class Constants {
	private static SharedPreferences placesPref;
	private Constants() {
	}

	public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

	public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

	public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

	/**
	 * Used to set an expiration time for a geofence. After this amount of time Location Services
	 * stops tracking the geofence.
	 */
	public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

	/**
	 * For this sample, geofences expire after twelve hours.
	 */
	public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
			GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
	//public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
	public static final float GEOFENCE_RADIUS_IN_METERS = 50; // 1 mile, 1.6 km

	/**
	 * Map for storing information about airports in the San Francisco bay area.
	 */


	public static HashMap<String, LatLng> getPlacesMapLatLng(Context con){
		placesPref = con.getSharedPreferences(ContractConstants.NEARBY_PREF,Context.MODE_PRIVATE);
		HashMap<String, LatLng> placesLatlng = new HashMap<String, LatLng>();
		
		String placesStr = placesPref.getString(ContractConstants.NEARBY_MOSQUE_PREF, null);

		if(placesStr != null){
			Log.d(ContractConstants.TAG, "[FindPeopleFragment] : nearByLocation() : PlacesSTR is not equals to null and value : "+placesStr);
			try {
				JSONObject placesJson = new JSONObject(placesStr);

				Places places = new Places();
				List<HashMap<String, String>> listOfMosq = places.parse(placesJson);
				for (HashMap<String, String> hashMap : listOfMosq) {
					String nameMosq = hashMap.get("place_name");
					double lat = Double.parseDouble(hashMap.get("lat"));
					double lng = Double.parseDouble(hashMap.get("lng"));
					LatLng latlng = new LatLng(lat, lng);
					placesLatlng.put(nameMosq, latlng);
					
				}
			} catch (Exception e) {
				Log.e(ContractConstants.TAG, "[FindPeopleFragment]  : nearByLocation  Error is : "+e.getMessage());
				e.printStackTrace();
			}
		}
		return placesLatlng;
	}


	public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();
	static {
		// San Francisco International Airport.
		BAY_AREA_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));

		// Googleplex.
		BAY_AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));

		// Test
		BAY_AREA_LANDMARKS.put("Udacity Studio", new LatLng(37.3999497,-122.1084776));
	}

}
