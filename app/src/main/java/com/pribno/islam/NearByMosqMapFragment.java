package com.pribno.islam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.array;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pribno.islam.geofence.Constants;

public class NearByMosqMapFragment extends Fragment implements OnMapReadyCallback{
	private SharedPreferences pref;
	private SharedPreferences placesPref;
	private Activity con;
	private boolean mapReady = false;
	private Marker userMarker;
	private Marker MosqMarker;
	private GoogleMap g_map;
	private double lat;
	private double lng;
	
	private static View rootView;
	
	
	public NearByMosqMapFragment(Activity con){
		pref = con.getSharedPreferences(ContractConstants.LocatioPref, Context.MODE_PRIVATE);
		placesPref = con.getSharedPreferences(ContractConstants.NEARBY_PREF,Context.MODE_PRIVATE);
		this.con = con;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView != null){
			 ViewGroup parent = (ViewGroup) rootView.getParent();
		        if (parent != null)
		            parent.removeView(rootView);
		    }
		    try {
		        rootView = inflater.inflate(R.layout.fragment_find_people, container, false);
		    } catch (InflateException e) {
		        /* map is already there, just return view as it is */
		    }
		
			

		lat = Double.parseDouble(pref.getString(ContractConstants.LatPref, ""));
		lng = Double.parseDouble(pref.getString(ContractConstants.LngPref, ""));

		int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(con);
		if(ConnectionResult.SUCCESS == status){
			Log.d("DDD", "###########Connected SuccessFull########## ");
			try {
				init();
			} catch (Exception e) {
				Log.e("DDD", "Error Init()  :   "+e.getMessage());
				e.printStackTrace();
			}


		}else{
			Log.i("DDD", "Not Connected and status is : "+status);
		}

		return rootView;
	}

	public void init()throws Exception{
//		if(g_map == null){
			Log.d("DDD", "Init Fragment is null");
			MapFragment mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.the_map));
			mapFrag.getMapAsync(this);

//		}
	}



	@Override
	public void onMapReady(GoogleMap g_map) {
		update(g_map);
	}

	public void update(GoogleMap map) {
		Log.d(ContractConstants.TAG, "[FindPeopleFragment]: update  Working OnMapReady!!!!");
		mapReady =true;
		LatLng userLatLng = new LatLng(lat,lng);
		if(userMarker!=null) userMarker.remove();
		try {
			userMarker = map.addMarker(new MarkerOptions()
			.position(userLatLng)
			.title("You are here")
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_icon))
			.snippet("Your last recorded location"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<LatLng> listofMosq = nearByLocation();
		Log.d(ContractConstants.TAG, "List of Mosq size is : "+listofMosq.size());
		int i =0;
		for (LatLng latLng : listofMosq) {
			i++;
//			if(MosqMarker!=null) MosqMarker.remove();
			Log.i(ContractConstants.TAG, "ListOfMosq fire for Circle : "+i++);
			try {
				/*map.addCircle(new CircleOptions()
		        .center(latLng)
		        .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
		        .strokeColor(Color.GREEN)
		        .fillColor(Color.argb(64,0,255,0)));*/

				map.addMarker(new MarkerOptions()
						.position(latLng)
//						.title("You are here")
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.other_mosq)));
						//.snippet("Your last recorded location"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CameraPosition target = CameraPosition.builder().target(userLatLng).zoom(14).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
	}

	private ArrayList<LatLng> nearByLocation() {
		ArrayList<LatLng> listofLanLng = new ArrayList<LatLng>();
		String placesStr = placesPref.getString(ContractConstants.NEARBY_MOSQUE_PREF, null);
		
		if(placesStr != null){
			Log.d(ContractConstants.TAG, "[FindPeopleFragment] : nearByLocation() : PlacesSTR is not equals to null and value : "+placesStr);
			try {
				JSONObject placesJson = new JSONObject(placesStr);

				Places places = new Places();
				List<HashMap<String, String>> listOfMosq = places.parse(placesJson);
				for (HashMap<String, String> hashMap : listOfMosq) {
					double lat = Double.parseDouble(hashMap.get("lat"));
					double lng = Double.parseDouble(hashMap.get("lng"));
					LatLng latlng = new LatLng(lat, lng);
					listofLanLng.add(latlng);
				}
			} catch (Exception e) {
				Log.e(ContractConstants.TAG, "[FindPeopleFragment]  : nearByLocation  Error is : "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		Log.d(ContractConstants.TAG, "[FindPeopleFragment] : nearByLocation() : PlacesSTR Null and value : "+placesStr);
		return listofLanLng;
	}
}
