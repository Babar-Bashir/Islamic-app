package com.pribno.islam;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class FusedLocationService extends Service implements
ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
//	protected GoogleApiClient mGoogleApiClient;
//	protected LocationRequest mLocationRequest;
	private SharedPreferences sharedPref;
	private Editor editor;
	private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 100 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
	
    
    @Override
	public void onCreate() {
		Log.d(ContractConstants.TAG, "[FusedLocationService]  :  onCreate()  ");
		sharedPref = getSharedPreferences(ContractConstants.LocatioPref, Context.MODE_PRIVATE);
		
		 if (!isGooglePlayServicesAvailable()) {
			 Log.d(ContractConstants.TAG, "Not Connected : "+isGooglePlayServicesAvailable());
			 
	        }		
		 Log.d(ContractConstants.TAG, "Connected : "+isGooglePlayServicesAvailable());
		try {
			createLocationRequest();
			buildGoogleApiClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		editor = sharedPref.edit();
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
		
//		startLocationUpdates();
		return START_STICKY;
	}
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
        	
            return true;
        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

	@Override
	public void onLocationChanged(Location location) {
		 mCurrentLocation = location;
		if(location == null){Log.d(ContractConstants.TAG, "[FusedLocationService]  :  Location is null");}
		Log.i(ContractConstants.TAG, location.toString());
		editor.putString(ContractConstants.LatPref, String.valueOf(mCurrentLocation.getLatitude()));
		editor.putString(ContractConstants.LngPref, String.valueOf(mCurrentLocation.getLongitude()));
		editor.commit();

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(ContractConstants.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());		
	}
	protected void createLocationRequest() throws Exception{
        try {
			mLocationRequest = new LocationRequest();
			mLocationRequest.setInterval(INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		} catch (Exception e) {
			Log.e(ContractConstants.TAG, "[FusedLocationService] : createLocationRequest : "+e.getMessage());
			//e.printStackTrace();
		}
    }

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(ContractConstants.TAG, "[FusedLocationService]  :  onConnected FusedLocation");

		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
//		mCurrentLocation =Location.
		
//		startLocationUpdates();
	}
	
	 protected void startLocationUpdates() {
		 
	        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
	                mGoogleApiClient, mLocationRequest, this);
//	        Log.d(TAG, "Location update started ..............: ");
	    }

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(ContractConstants.TAG, "Connection suspended");
		mGoogleApiClient.connect();		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

}
