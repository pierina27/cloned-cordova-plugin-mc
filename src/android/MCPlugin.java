package com.leadclic.test.plugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.provider.Settings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETLocationManager;
 
public class MCPlugin extends CordovaPlugin {
 
	private static final String TAG = "MCPlugin";
	
	public static CordovaWebView gWebView;
	public static String notificationCallBack = "MCPlugin.onNotificationReceived";
	
	public final int PERMISSION_LOCATION = 1;
	public static final String ACCESS_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
	 
	public MCPlugin() {}
	
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		gWebView = webView;
		Log.d(TAG, "MCPlugin INITIALZE");
	}
	 
	public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

		Log.d(TAG,"MCPlugin RECEIVED: "+ action);
		
		try{
			// READY //
			if (action.equals("ready")) {
				//ETPush.getInstance().enablePush();
				startLocation(callbackContext);
			}
			// NOTIFICATION CALLBACK REGISTER //
			else if (action.equals("registerNotification")) {
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						if(MCPluginApplication.lastPush != null) MCPlugin.sendPushPayload( MCPluginApplication.lastPush );
						MCPluginApplication.lastPush = null;
					}
				});
			}
			// SUBSCRIBER KEY //
			else if (action.equals("setSubscriberKey")) {
				ETPush.getInstance().setSubscriberKey( args.getString(0) );
			}
			else if (action.equals("getSubscriberKey")) {
				callbackContext.success( ETPush.getInstance().getSubscriberKey() );
				return true;
			}
			// ATTRIBUTES //
			else if (action.equals("addAttribute")) {
				ETPush.getInstance().addAttribute(args.getString(0), args.getString(1));
			}
			else if (action.equals("removeAttribute")) {
				ETPush.getInstance().removeAttribute(args.getString(0));
			}
			// TAGS //
			else if (action.equals("addTag")) {
				ETPush.getInstance().addTag(args.getString(0));
			}
			else if (action.equals("removeTag")) {
				ETPush.getInstance().removeTag(args.getString(0));
			}
			// MONITOR LOCATION //
			else if (action.equals("startWatchingLocation")) {
				startLocation(callbackContext);
			}
			else if (action.equals("stopWatchingLocation")) {
				cordova.getThreadPool().execute(new Runnable() {
					public void run() {
						try{
							ETLocationManager.locationManager().stopWatchingLocation();
						}catch(Exception e){
							Log.d(TAG, "ERROR: onStopWatchingLocation: " + e.getMessage());
							callbackContext.error(e.getMessage());
						}
					}
				});
			}
			else if (action.equals("isWatchingLocation")) {
				callbackContext.success( ""+ETLocationManager.getInstance().isWatchingLocation() );
				return true;
			}
			// SDK STATE //
			else if (action.equals("getSDKState")) {
				String SDKState = ETPush.getInstance().getSDKState();
				Log.d(TAG, "SDKState: " + SDKState);
				callbackContext.success( SDKState );
				return true;
			}
			// METHOD NOT FOUND //
			else{
				callbackContext.error("Method not found");
				return false;
			}
		}catch(Exception e){
			Log.d(TAG, "ERROR: onPluginAction: " + e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
		}
		
		//cordova.getThreadPool().execute(new Runnable() {
		//	public void run() {
		//	  //
		//	}
		//});
		
		//cordova.getActivity().runOnUiThread(new Runnable() {
        //    public void run() {
        //      //
        //    }
        //});
		callbackContext.success("Received " + action);
		return true;
	}
	
	public void startLocation(final CallbackContext callbackContext){
		if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.M || cordova.hasPermission(ACCESS_LOCATION)){
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					try{
						ETLocationManager.locationManager().startWatchingLocation();
					}catch(Exception e){
						Log.d(TAG, "ERROR: onStartWatchingLocation: " + e.getMessage());
						callbackContext.error(e.getMessage());
					}
				}
			});
		}else{
			cordova.requestPermission(this, PERMISSION_LOCATION, ACCESS_LOCATION);
		}
	}
	
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		switch(requestCode) {
			case PERMISSION_LOCATION:
				cordova.getThreadPool().execute(new Runnable() {
					public void run() {
					   try {
						   ETLocationManager.locationManager().startWatchingLocation();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				break;
		}
	}
	
	public static void sendPushPayload(Bundle payload) {
	    try {
		    JSONObject jo = new JSONObject();
			for (String key : payload.keySet()) {
			    jo.put(key, payload.get(key));
				Log.d(TAG, "payload: " + key + " => " + payload.get(key));
            }
			String callBack = "javascript:" + notificationCallBack + "(" + jo.toString() + ")";
		    Log.d(TAG, "Sent PUSH to view: " + callBack);
		    gWebView.sendJavascript(callBack);
		} catch (Exception e) {
			Log.d(TAG, "ERROR sendPushToView: " + e.getMessage());
		}
	}
}