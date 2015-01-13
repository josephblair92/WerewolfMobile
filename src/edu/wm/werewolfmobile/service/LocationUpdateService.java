package edu.wm.werewolfmobile.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import edu.wm.werewolfmobile.HomeActivity;
import edu.wm.werewolfmobile.HttpRequestTask;
import edu.wm.werewolfmobile.MainActivity;
import edu.wm.werewolfmobile.NewGameActivity;
//import edu.wm.werewolfmobile.HomeActivity.ErrorDialogFragment;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocationUpdateService extends IntentService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    LocationClient mLocationClient;
    Timer timer;
    private Handler handler;
    private Runnable runnable;
	public static boolean timerRunning = false;
	
	public LocationUpdateService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public LocationUpdateService() {
		super("LocationUpdateService");
	}

	@Override
    protected void onHandleIntent(Intent i) {
		
		System.out.println("Background service started");
				
		boolean servicesConnected = servicesConnected();
		System.out.println(servicesConnected);
		
		if (!servicesConnected)
			return;
		
		mLocationClient = new LocationClient(getApplicationContext(), this, this);
		System.out.println("Connecting");
		mLocationClient.connect();
		
	}
	
	@Override
	public void onDestroy() {
		System.out.println("Background service stopped");
		timerRunning = false;
		super.onDestroy();
	}
	
   

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            //int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play services
//            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
//                    resultCode,
//                    this,
            //        CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
//            if (errorDialog != null) {
//                // Create a new DialogFragment for the error dialog
//                ErrorDialogFragment errorFragment =
//                        new ErrorDialogFragment();
//                // Set the dialog in the DialogFragment
//                errorFragment.setDialog(errorDialog);
//                // Show the error dialog in the DialogFragment
//                errorFragment.show(getFragmentManager(),
//                        "Location Updates");
//            }
            
            return false;
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
		System.out.println("Connection failed");
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {

		System.out.println("Connected");		
		System.out.println("Creating runnable");
		
		runnable = new Runnable() {
			   @Override
			   public void run() {
				   
				   	System.out.println("Running runnable");
				   
	        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LocationUpdateService.this.getBaseContext());
	        		String username = settings.getString("edu.wm.werewolfmobile.username", "Unavailable");
	        		String password = settings.getString("edu.wm.werewolfmobile.password", "Unavailable");
	        		
	        		if (username.equals("Unavailable") || password.equals("Unavailable"))  {
	        			System.out.println("Location update error");
	        			return;
	        		}
	            	
	        		try {
	        			System.out.println("Checking if location update is necessary");
	        			HttpRequestTask activeGameTask = new HttpRequestTask();
	        			String activeGameResponse = activeGameTask.execute("http://werewolf-jpblair.herokuapp.com/activegame", "GET", null, null).get();
	        			
	        			if (activeGameResponse.equals(""))  {
	        				System.out.println("No active game");
	        				mLocationClient.disconnect();
	        				LocationUpdateService.timerRunning = false;
	        				return;
	        			}
	        			
	        			HttpRequestTask atNightTask = new HttpRequestTask();
	        			String atNightResponse = atNightTask.execute("http://werewolf-jpblair.herokuapp.com/atnight", "GET", null, null).get();
	        			if (atNightResponse.equals("true"))  {
	        				
	        				System.out.println("At night, location update needed");
	                		Location mCurrentLocation = mLocationClient.getLastLocation();
	                		String lat = "" + mCurrentLocation.getLatitude();
	                		String lng = "" + mCurrentLocation.getLongitude();
	                		System.out.println("Lat: " + lat + " Lon: " + lng);
	                		
	                		HttpRequestTask locationUpdateTask = new HttpRequestTask();
	            			String locationUpdateResponse = locationUpdateTask.execute("http://werewolf-jpblair.herokuapp.com/location", "POST", username, password, "lat", lat, "lng", lng).get();
	            			System.out.println(locationUpdateResponse);
	            			JSONObject json = new JSONObject(locationUpdateResponse);
	      			      	handler.postDelayed(this, 60000);
	            			/*
	            			if (json.getString("success").equals("false"))  {
	            				mLocationClient.disconnect();
	            				LocationUpdateService.this.timer.cancel();
	                			return;
	            			}
	            			*/
	            			

	        			}
	        			
	        		}
	        		catch (Exception e) {
	        			e.printStackTrace();
	    				mLocationClient.disconnect();
	        			return;
	        		}

			   }
			};
			
			handler = new Handler();
			handler.post(runnable);
			timerRunning = true;
		
		/*
        
		TimerTask task = new TimerTask() {
            public void run() {
            	
        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LocationUpdateService.this.getBaseContext());
        		String username = settings.getString("edu.wm.werewolfmobile.username", "Unavailable");
        		String password = settings.getString("edu.wm.werewolfmobile.password", "Unavailable");
        		
        		if (username.equals("Unavailable") || password.equals("Unavailable"))  {
        			System.out.println("Location update error");
        			return;
        		}
            	
        		try {
        			System.out.println("Checking if location update is necessary");
        			HttpRequestTask activeGameTask = new HttpRequestTask();
        			String activeGameResponse = activeGameTask.execute("http://werewolf-jpblair.herokuapp.com/activegame", "GET", null, null).get();
        			
        			if (activeGameResponse.equals(""))  {
        				System.out.println("No active game");
        				mLocationClient.disconnect();
        				LocationUpdateService.this.timer.cancel();
        				LocationUpdateService.timerRunning = false;
        				return;
        			}
        			
        			HttpRequestTask atNightTask = new HttpRequestTask();
        			String atNightResponse = atNightTask.execute("http://werewolf-jpblair.herokuapp.com/atnight", "GET", null, null).get();
        			if (atNightResponse.equals("true"))  {
        				
        				System.out.println("At night, location update needed");
                		Location mCurrentLocation = mLocationClient.getLastLocation();
                		String lat = "" + mCurrentLocation.getLatitude();
                		String lng = "" + mCurrentLocation.getLongitude();
                		System.out.println("Lat: " + lat + " Lon: " + lng);
                		
                		HttpRequestTask locationUpdateTask = new HttpRequestTask();
            			String locationUpdateResponse = locationUpdateTask.execute("http://werewolf-jpblair.herokuapp.com/location", "POST", username, password, "lat", lat, "lng", lng).get();
            			System.out.println(locationUpdateResponse);
            			JSONObject json = new JSONObject(locationUpdateResponse);
            			/*
            			if (json.getString("success").equals("false"))  {
            				mLocationClient.disconnect();
            				LocationUpdateService.this.timer.cancel();
                			return;
            			}
            			

        			}
        			
        		}
        		catch (Exception e) {
        			e.printStackTrace();
    				mLocationClient.disconnect();
    				LocationUpdateService.this.timer.cancel();
        			return;
        		}
            }
        };
        
        */
        
        /*
		
        if (timer == null) {
	        timer = new Timer();
	        System.out.println("Schedule timer");
	        timer.schedule(task, 500, 60000);
	        timerRunning = true;
        }
        
        */
		
	}

	@Override
	public void onDisconnected() {

		System.out.println("Disconnected");
		
	}
	
	public static class ErrorDialogFragment extends DialogFragment {
	        // Global field to contain the error dialog
	        private Dialog mDialog;
	        // Default constructor. Sets the dialog field to null
	        public ErrorDialogFragment() {
	            super();
	            mDialog = null;
	        }
	        // Set the dialog to display
	        public void setDialog(Dialog dialog) {
	            mDialog = dialog;
	        }
	        // Return a Dialog to the DialogFragment.
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            return mDialog;
	        }
	    }	

}
