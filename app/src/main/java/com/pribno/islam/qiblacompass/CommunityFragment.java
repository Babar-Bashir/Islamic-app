package com.pribno.islam.qiblacompass;

import java.util.Locale;

import com.pribno.islam.R;
import com.pribno.islam.R.layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommunityFragment extends Fragment {
	
	public final float MAX_ROATE_DEGREE = 1.0f;
	    public SensorManager mSensorManager;
	    public Sensor mOrientationSensor;
	    public LocationManager mLocationManager;
	    public String mLocationProvider;
	    public float mDirection;
	    public float mTargetDirection;
	    public AccelerateInterpolator mInterpolator;
	    public final Handler mHandler = new Handler();
	    public boolean mStopDrawing;
	    public boolean mChinease;
	    private Context con;
	    View mCompassView;
	    CompassView mPointer;
	    TextView mLocationTextView;
	    LinearLayout mDirectionLayout;
	    LinearLayout mAngleLayout;
	    View rootView;
	    protected Runnable mCompassViewUpdater = new Runnable() {
	        @Override
	        public void run() {
	            if (mPointer != null && !mStopDrawing) {
	                if (mDirection != mTargetDirection) {

	                    // calculate the short routine
	                    float to = mTargetDirection;
	                    if (to - mDirection > 180) {
	                        to -= 360;
	                    } else if (to - mDirection < -180) {
	                        to += 360;
	                    }

	                    // limit the max speed to MAX_ROTATE_DEGREE
	                    float distance = to - mDirection;
	                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
	                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
	                    }

	                    // need to slow down if the distance is short
	                    mDirection = normalizeDegree(mDirection
	                            + ((to - mDirection) * mInterpolator.getInterpolation(Math
	                                    .abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
	                    mPointer.updateDirection(mDirection);
	                }

	                updateDirection();

	                mHandler.postDelayed(mCompassViewUpdater, 20);
	            }
	        }
	    };
	
	public CommunityFragment(Context con){
		this.con = con;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_community, container, false);
        initResources();
        initServices();
        onResume();
        return rootView;
    }
	
	public void onResume() {
        super.onResume();
        if (mLocationProvider != null) {
            updateLocation(mLocationManager.getLastKnownLocation(mLocationProvider));
            mLocationManager.requestLocationUpdates(mLocationProvider, 2000, 10, mLocationListener);
        } else {
            mLocationTextView.setText(R.string.cannot_get_location);
        }
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mOrientationSensorEventListener, mOrientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);
    }
	
	 private void initResources() {
	        mDirection = 0.0f;
	        mTargetDirection = 0.0f;
	        mInterpolator = new AccelerateInterpolator();
	        mStopDrawing = true;
	        mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");

	        mCompassView = rootView.findViewById(R.id.view_compass);
	        mPointer = (CompassView) rootView.findViewById(R.id.compass_pointer);
	        mLocationTextView = (TextView) rootView.findViewById(R.id.textview_location);
	        mDirectionLayout = (LinearLayout) rootView.findViewById(R.id.layout_direction);
	        mAngleLayout = (LinearLayout) rootView.findViewById(R.id.layout_angle);

	        mPointer.setImageResource(mChinease ? R.drawable.compass_cn : R.drawable.compass);
	    }

	    private void initServices() {
	        // sensor manager
	        mSensorManager = (SensorManager) con.getSystemService(Context.SENSOR_SERVICE);
	        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	        // location manager
	        mLocationManager = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_FINE);
	        criteria.setAltitudeRequired(false);
	        criteria.setBearingRequired(false);
	        criteria.setCostAllowed(true);
	        criteria.setPowerRequirement(Criteria.POWER_LOW);
	        mLocationProvider = mLocationManager.getBestProvider(criteria, true);

	    }

	    private void updateDirection() {
	        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	        mDirectionLayout.removeAllViews();
	        mAngleLayout.removeAllViews();

	        ImageView east = null;
	        ImageView west = null;
	        ImageView south = null;
	        ImageView north = null;
	        float direction = normalizeDegree(mTargetDirection * -1.0f);
	        if (direction > 22.5f && direction < 157.5f) {
	            // east
	            east = new ImageView(con);
	            east.setImageResource(mChinease ? R.drawable.e_cn : R.drawable.e);
	            east.setLayoutParams(lp);
	        } else if (direction > 202.5f && direction < 337.5f) {
	            // west
	            west = new ImageView(con);
	            west.setImageResource(mChinease ? R.drawable.w_cn : R.drawable.w);
	            west.setLayoutParams(lp);
	        }

	        if (direction > 112.5f && direction < 247.5f) {
	            // south
	            south = new ImageView(con);
	            south.setImageResource(mChinease ? R.drawable.s_cn : R.drawable.s);
	            south.setLayoutParams(lp);
	        } else if (direction < 67.5 || direction > 292.5f) {
	            // north
	            north = new ImageView(con);
	            north.setImageResource(mChinease ? R.drawable.n_cn : R.drawable.n);
	            north.setLayoutParams(lp);
	        }

	        if (mChinease) {
	            // east/west should be before north/south
	            if (east != null) {
	                mDirectionLayout.addView(east);
	            }
	            if (west != null) {
	                mDirectionLayout.addView(west);
	            }
	            if (south != null) {
	                mDirectionLayout.addView(south);
	            }
	            if (north != null) {
	                mDirectionLayout.addView(north);
	            }
	        } else {
	            // north/south should be before east/west
	            if (south != null) {
	                mDirectionLayout.addView(south);
	            }
	            if (north != null) {
	                mDirectionLayout.addView(north);
	            }
	            if (east != null) {
	                mDirectionLayout.addView(east);
	            }
	            if (west != null) {
	                mDirectionLayout.addView(west);
	            }
	        }

	        int direction2 = (int) direction;
	        boolean show = false;
	        if (direction2 >= 100) {
	            mAngleLayout.addView(getNumberImage(direction2 / 100));
	            direction2 %= 100;
	            show = true;
	        }
	        if (direction2 >= 10 || show) {
	            mAngleLayout.addView(getNumberImage(direction2 / 10));
	            direction2 %= 10;
	        }
	        mAngleLayout.addView(getNumberImage(direction2));

	        ImageView degreeImageView = new ImageView(con);
	        degreeImageView.setImageResource(R.drawable.degree);
	        degreeImageView.setLayoutParams(lp);
	        mAngleLayout.addView(degreeImageView);
	    }

	    private ImageView getNumberImage(int number) {
	        ImageView image = new ImageView(con);
	        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        switch (number) {
	            case 0:
	                image.setImageResource(R.drawable.number_0);
	                break;
	            case 1:
	                image.setImageResource(R.drawable.number_1);
	                break;
	            case 2:
	                image.setImageResource(R.drawable.number_2);
	                break;
	            case 3:
	                image.setImageResource(R.drawable.number_3);
	                break;
	            case 4:
	                image.setImageResource(R.drawable.number_4);
	                break;
	            case 5:
	                image.setImageResource(R.drawable.number_5);
	                break;
	            case 6:
	                image.setImageResource(R.drawable.number_6);
	                break;
	            case 7:
	                image.setImageResource(R.drawable.number_7);
	                break;
	            case 8:
	                image.setImageResource(R.drawable.number_8);
	                break;
	            case 9:
	                image.setImageResource(R.drawable.number_9);
	                break;
	        }
	        image.setLayoutParams(lp);
	        return image;
	    }

	    private void updateLocation(Location location) {
	        if (location == null) {
	            mLocationTextView.setText(R.string.getting_location);
	        } else {
	            StringBuilder sb = new StringBuilder();
	            double latitude = location.getLatitude();
	            double longitude = location.getLongitude();

	            if (latitude >= 0.0f) {
	                sb.append(getString(R.string.location_north, getLocationString(latitude)));
	            } else {
	                sb.append(getString(R.string.location_south, getLocationString(-1.0 * latitude)));
	            }

	            sb.append("    ");

	            if (longitude >= 0.0f) {
	                sb.append(getString(R.string.location_east, getLocationString(longitude)));
	            } else {
	                sb.append(getString(R.string.location_west, getLocationString(-1.0 * longitude)));
	            }

	            mLocationTextView.setText(sb.toString());
	        }
	    }

	    private String getLocationString(double input) {
	        int du = (int) input;
	        int fen = (((int) ((input - du) * 3600))) / 60;
	        int miao = (((int) ((input - du) * 3600))) % 60;
	        return String.valueOf(du) + "" + String.valueOf(fen) + "'" + String.valueOf(miao) + "\"";
	    }
	    
	    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

	        @Override
	        public void onSensorChanged(SensorEvent event) {
	            float direction = event.values[0] * -1.0f;
	            mTargetDirection = normalizeDegree(direction);
	        }

	        @Override
	        public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        }
	    };

	    private float normalizeDegree(float degree) {
	        return (degree + 720) % 360;
	    }

	    LocationListener mLocationListener = new LocationListener() {

	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras) {
	            if (status != LocationProvider.OUT_OF_SERVICE) {
	                updateLocation(mLocationManager.getLastKnownLocation(mLocationProvider));
	            } else {
	                mLocationTextView.setText(R.string.cannot_get_location);
	            }
	        }

	        @Override
	        public void onProviderEnabled(String provider) {
	        }

	        @Override
	        public void onProviderDisabled(String provider) {
	        }

	        @Override
	        public void onLocationChanged(Location location) {
	            updateLocation(location);
	        }

	    };
}
