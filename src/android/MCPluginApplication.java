package com.leadclic.test.plugin;

import java.util.Iterator;

import android.content.pm.PackageManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.exacttarget.etpushsdk.util.EventBus;
import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETPushConfig;
import com.exacttarget.etpushsdk.event.ReadyAimFireInitCompletedEvent;
import com.exacttarget.etpushsdk.event.PushReceivedEvent;
import com.exacttarget.etpushsdk.event.RegistrationEvent;
import com.exacttarget.etpushsdk.event.ServerErrorEvent;
import com.exacttarget.etpushsdk.event.GeofenceResponseEvent;
import com.exacttarget.etpushsdk.event.LastKnownLocationEvent;
import com.exacttarget.etpushsdk.event.LocationStatusEvent;
import com.exacttarget.etpushsdk.event.BackgroundEvent;

import android.app.Activity;
import android.content.Context;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

public class MCPluginApplication extends Application {

	private static final String TAG = "MCPlugin";
	public static Bundle lastPush = null;

    @Override
    public void onCreate() {
        super.onCreate();
		try {
			Log.d(TAG, "==> MCPlugin onCreate");
			EventBus.getInstance().register(this);
			ETPushConfig.Builder pushConfigBuilder = new ETPushConfig.Builder( this );
			ETPush.setLogLevel(Log.VERBOSE);
			pushConfigBuilder
				         .setEtAppId(getString( getResources().getIdentifier("mc_app_id", "string", getPackageName()) ))
				     .setAccessToken(getString( getResources().getIdentifier("mc_access_token", "string", getPackageName()) ))
				     .setGcmSenderId(getString( getResources().getIdentifier("gcm_sender_id", "string", getPackageName()) ))
				 .setLocationEnabled(true)
			 	.setAnalyticsEnabled(true);
			ETPush.readyAimFire(pushConfigBuilder.build());
		}catch(Exception e){
			Log.d(TAG, "ERROR onCreate: " + e.getMessage());
		}
    }
	
	
	
	//Sent when readyAimFire() initialization completes
	public void onEvent(final ReadyAimFireInitCompletedEvent event) {
		Log.d(TAG, "==> onReadyAimFireInitCompletedEvent");
		ETPush etPush = null;
		try {
			etPush = event.getEtPush();
			etPush.addTag("leadclic-mc-plugin v2.0");
			//ICON
			int iconId = getResources().getIdentifier("mc_plugin_stat_icon", "drawable", getPackageName());
			Log.d(TAG, "\tSetting Android Notification Icon ID: " + iconId);
			if(iconId != 0) etPush.setNotificationResourceId( getResources().getIdentifier("mc_plugin_stat_icon", "drawable", getPackageName()) );
		} catch (ETException e) {
			Log.d(TAG, "ERROR onReadyAimFireInitCompletedEvent: " + e.getMessage());
		}
    }
	//Occurs whenever we come into the foreground or whenever the 5000m fence (aka the Magic Fence) is crossed
	public void onEvent(final BackgroundEvent event) {
		Log.d(TAG, "==> onBackgroundEvent: isInBackground=" + event.isInBackground() + " timeWentInBackground=" + event.getTimeWentInBackground());
	}
	//This event is posted to the EventBus when the application goes into the background for more than a minute seconds, or when the application has come back into the foreground
	public void onEvent(final LastKnownLocationEvent event) {
		Log.d(TAG, "==> onLastKnownLocationEvent: " + event.getLocation() );
	}
	//Sent after geofence and message data to start monitoring has been received from ExactTarget for the user's current location
	public void onEvent(final GeofenceResponseEvent event) {
		Log.d(TAG, "==> onGeofenceResponseEvent");
	}
	//Location watching status change
	public void onEvent(final LocationStatusEvent event) {
		Log.d(TAG, "==> onLocationStatusEvent: isLocating=" + event.isWatchingLocation() );
	}
	public void onEvent(final ServerErrorEvent event) {
		Log.d(TAG, "==> onServerErrorEvent: " + event.getMessage());
	}
	//Sent whenever an error occurs on the ExactTarget server
	public void onEvent(final PushReceivedEvent event) {
		Log.d(TAG, "==> onPushReceivedEvent");
		Bundle payload = null;
		try {
			payload = event.getPayload();
			if(MCPlugin.gWebView != null) MCPlugin.sendPushPayload(payload);
			else{
			  Log.d(TAG, "APP WAS CLOSED DURING PUSH RECEPTION (saved)");
			  lastPush = payload;
			}
		} catch (Exception e) {
			Log.d(TAG, "ERROR onPushReceivedEvent: " + e.getMessage());
		}
    }
	//Sent whenever a user registers or unregisters for push notifications with ExactTarget
	public void onEvent(final RegistrationEvent event) {
	    Log.d(TAG, "==> onRegistrationEvent start");
		Log.d(TAG, "\tDevice ID:" + event.getDeviceId());
		Log.d(TAG, "\tSystem Token:" + event.getSystemToken());
        Log.d(TAG, "\tSubscriber key:" + event.getSubscriberKey());
    }
	
}