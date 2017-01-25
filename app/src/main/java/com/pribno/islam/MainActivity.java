package com.pribno.islam;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.pribno.islam.adapter.NavDrawerListAdapter;
import com.pribno.islam.geofence.Constants;
import com.pribno.islam.geofence.GeofenceErrorMessages;
import com.pribno.islam.geofence.GeofenceTransitionsIntentService;
import com.pribno.islam.model.NavDrawerItem;
import com.pribno.islam.qiblacompass.CompassFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>{
//	Geo Fences Feilds and variables
	protected ArrayList<Geofence> mGeofenceList;
	protected GoogleApiClient mGoogleApiClient;
	
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 if (!isGooglePlayServicesAvailable()) {
			 Log.d(ContractConstants.TAG, "Not Connected : "+isGooglePlayServicesAvailable());
			 
	        }else{		
		 Log.d(ContractConstants.TAG, "Connected : "+isGooglePlayServicesAvailable());
	        }
		//geoFences list
		mGeofenceList = new ArrayList<Geofence>();
		buildGoogleApiClient();
		
		populateGeofenceList();
		
		  // Get the geofences used. Geofence data is hard coded in this sample.

        // Kick off the request to build GoogleApiClient.
        
        //add GeofencesHandler method
        
		//########################################################################################################################//
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Pages
//		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));


		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		Log.i(ContractConstants.TAG, "Add GeoFence() call before or after onStart() method");
		
	}
//#################################################################################################################################//
	
	private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
        	
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
	
	 private PendingIntent getGeofencePendingIntent() {
	        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
	        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
	        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    }
	 
	 
	 public void addGeofencesButtonHandler() {
	        if (!mGoogleApiClient.isConnected()) {
	            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
	            return;
	        }

	        try {
	            LocationServices.GeofencingApi.addGeofences(
	                    mGoogleApiClient,
	                    // The GeofenceRequest object.
	                    getGeofencingRequest(),
	                    // A pending intent that that is reused when calling removeGeofences(). This
	                    // pending intent is used to generate an intent when a matched geofence
	                    // transition is observed.
	                    getGeofencePendingIntent()
	            ).setResultCallback(this); // Result processed in onResult().
	        } catch (SecurityException securityException) {
	            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
	        }
	    }
	
	@Override
		protected void onStart() {
			super.onStart();
			Log.i(ContractConstants.TAG, "Onstart()");
			 if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
				 mGoogleApiClient.connect();
		        }
		}
	
	@Override
		protected void onResume() {
		Log.d(ContractConstants.TAG, "OnResume()");
			super.onResume();
			
			startService(new Intent(MainActivity.this, PrayerService.class));
		}
	
	 @Override
	    protected void onStop() {
		 Log.d(ContractConstants.TAG, "OnStop()");
	        super.onStop();
	        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
	            mGoogleApiClient.disconnect();
	        }
	    }
	 
	 @Override
	    public void onConnected(Bundle connectionHint) {
		 try {
			 addGeofencesButtonHandler();
		 }catch (Exception e){

		 }
	 }

	    @Override
	    public void onConnectionFailed(ConnectionResult result) {}

	    @Override
	    public void onConnectionSuspended(int cause) {
	        mGoogleApiClient.connect();
	    }
//###################################################################################################################################//
	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new PrayerTimeFragment(this);
			break;
		case 1:
			fragment = new NearByMosqMapFragment(this);
			break;
		case 2:
			fragment = new CompassFragment(this);
			break;
		case 3:
			fragment = new SettingsFragment();
			break;
		/*case 4:
			fragment = new PagesFragment();
			break;*/
		case 4:
			fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e(ContractConstants.TAG, "[MainActivity]  :  Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	
	 public void populateGeofenceList() {
		 HashMap<String, LatLng> BAY_AREA_LANDMARKS = Constants.getPlacesMapLatLng(this);
	        for (Map.Entry<String, LatLng> entry : BAY_AREA_LANDMARKS.entrySet()) {

	            mGeofenceList.add(new Geofence.Builder()
	                    // Set the request ID of the geofence. This is a string to identify this
	                    // geofence.
	                    .setRequestId(entry.getKey())

	                            // Set the circular region of this geofence.
	                    .setCircularRegion(
	                            entry.getValue().latitude,
	                            entry.getValue().longitude,
	                            Constants.GEOFENCE_RADIUS_IN_METERS
	                    )

	                            // Set the expiration duration of the geofence. This geofence gets automatically
	                            // removed after this period of time.
	                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

	                            // Set the transition types of interest. Alerts are only generated for these
	                            // transition. We track entry and exit transitions in this sample.
	                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
	                            Geofence.GEOFENCE_TRANSITION_EXIT)

	                            // Create the geofence.
	                    .build());
	        }
	 }
//###################################################################################################################################//

	    /**
	     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
	     */
	    protected synchronized void buildGoogleApiClient() {
	    	Log.d(ContractConstants.TAG, "buildGoogleApiClient()");
	        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
	                .addConnectionCallbacks(this)
	                .addOnConnectionFailedListener(this)
	                .addApi(LocationServices.API)
	                .build();
	    }
	    
	    private GeofencingRequest getGeofencingRequest() {

	        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
	        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
	        builder.addGeofences(mGeofenceList);
	        return builder.build();
	    }
	    
	    public void onResult(Status status) {
	        if (status.isSuccess()) {
	            Toast.makeText(
	                    this,
	                    "Geofences Added",
	                    Toast.LENGTH_SHORT
	            ).show();
	        } else {
	            // Get the status code for the error and log it using a user-friendly message.
	            String errorMessage = GeofenceErrorMessages.getErrorString(this,
	                    status.getStatusCode());
	        }
	    }
	    
	    private void test_sendNotification(String notificationDetails) {
	        // Create an explicit content Intent that starts the main Activity.
	        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

	        // Construct a task stack.
	        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

	        // Add the main Activity to the task stack as the parent.
	        stackBuilder.addParentStack(MainActivity.class);

	        // Push the content Intent onto the stack.
	        stackBuilder.addNextIntent(notificationIntent);

	        // Get a PendingIntent containing the entire back stack.
	        PendingIntent notificationPendingIntent =
	                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

	        // Get a notification builder that's compatible with platform versions >= 4
	        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

	        // Define the notification settings.
	        builder.setSmallIcon(R.drawable.ic_launcher)
	                // In a real app, you may want to use a library like Volley
	                // to decode the Bitmap.
	                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
	                        R.drawable.ic_launcher))
//	                .setColor(Color.RED)
	                .setContentTitle(notificationDetails)
	                .setContentText(getString(R.string.geofence_transition_notification_text))
	                .setContentIntent(notificationPendingIntent);

	        // Dismiss notification once the user touches it.
	        builder.setAutoCancel(true);

	        // Get an instance of the Notification manager
	        NotificationManager mNotificationManager =
	                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	        // Issue the notification
	        mNotificationManager.notify(0, builder.build());
	    }

}
