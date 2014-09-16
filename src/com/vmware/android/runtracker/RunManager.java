package com.vmware.android.runtracker;

import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class RunManager {
	
    private static final String TAG = "RunManager";

    public static final String ACTION_LOCATION = "com.vmware.android.runtracker.ACTION_LOCATION";
    
    private static final String TEST_PROVIDER = "TEST_PROVIDER";
    
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    
	public void testConflict(int myResolvedInt){
		int tempInt = 0;
		tempInt = myResolvedInt;
		Log.i(TAG, "Input integer: " + tempInt);
	}
    
    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public static RunManager get(Context c) {
        if (sRunManager == null) {
            // we use the application context to avoid leaking activities
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        
        // if we have the test provider and it's enabled, use it
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && 
                mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider " + provider);
        

        displayLocationProviderInfo(mLocationManager);
        // get the last known location and broadcast it if we have one
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // reset the time to now
        	Date lastKnownLocationTime = new Date(lastKnown.getTime());
        	Log.d(TAG, "Last known location time: " + lastKnownLocationTime);
        	// You are not overriding system wide time here. lastKnown is a deep copy of the original object.
            lastKnown.setTime(System.currentTimeMillis());
            Log.d(TAG, "Last known location accuracy in meters: " + lastKnown.getAccuracy());
            broadcastLocation(lastKnown);
        }
        
        
        // start updates from the location manager
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    
    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    
    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }
    
    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    private void displayLocationProviderInfo(LocationManager locationManager) {
    	Location lastKnown = null;
    	for (String provider : locationManager.getAllProviders()){
    		lastKnown = locationManager.getLastKnownLocation(provider);
            Date lastKnownLocationTime = new Date(lastKnown.getTime());
            Log.d(TAG, "======= Provider: " + provider);
            Log.d(TAG, "Last known location time: " + lastKnownLocationTime);
            Log.d(TAG, "Last known location accuracy in meters: " + lastKnown.getAccuracy());
            Log.d(TAG, "Location (lat, lon): (" + lastKnown.getLatitude() + ", " + lastKnown.getLongitude() + ")");
            Log.d(TAG, "end Provider =======");
    	}
    	
    }


}
