package com.leadclic;

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
			Log.d(TAG, "AAAA");
			EventBus.getInstance().register(this);
			ETPushConfig.Builder pushConfigBuilder = new ETPushConfig.Builder( this );
			Log.d(TAG, "BBBB");
			ETPush.setLogLevel(Log.DEBUG);
			pushConfigBuilder
				         .setEtAppId(getString( getResources().getIdentifier("et_app_id_prod", "string", getPackageName()) ))
				     .setAccessToken(getString( getResources().getIdentifier("et_access_token_prod", "string", getPackageName()) ))
				     .setGcmSenderId(getString( getResources().getIdentifier("gcm_sender_id_prod", "string", getPackageName()) ))
				 .setLocationEnabled(true)
			 	.setAnalyticsEnabled(true);
			ETPush.readyAimFire(pushConfigBuilder.build());
			Log.d(TAG, "CCCC");
		}catch(Exception e){
			Log.d(TAG, "ERROR onCreate: " + e.getMessage());
		}
    }
	
	public void onEvent(final ReadyAimFireInitCompletedEvent event) {
		Log.d(TAG, "onReadyAimFireInitCompletedEvent start");
		ETPush etPush = null;
		try {
			etPush = event.getEtPush();
			etPush.addTag("3.0.0");
			int iconId = getResources().getIdentifier("mc_plugin_stat_icon", "drawable", getPackageName());
			Log.d(TAG, "Setting Android Notification Icon ID: " + iconId);
			if(iconId != 0) etPush.setNotificationResourceId( getResources().getIdentifier("mc_plugin_stat_icon", "drawable", getPackageName()) );
		} catch (ETException e) {
			Log.d(TAG, "ERROR onReadyAimFireInitCompletedEvent: " + e.getMessage());
		}
		Log.d(TAG, "onReadyAimFireInitCompletedEvent end");
    }
	
	public void onEvent(final ServerErrorEvent event) {
		Log.d(TAG, "onServerErrorEvent: " + event.getMessage());
	}
	
	public void onEvent(final LastKnownLocationEvent event) {
		Log.d(TAG, "onLastKnownLocationEvent: " + event.getLocation() );
	}
	
	public void onEvent(final GeofenceResponseEvent event) {
		Log.d(TAG, "onGeofenceResponseEvent");
	}
	
	public void onEvent(final LocationStatusEvent event) {
		Log.d(TAG, "onLocationStatusEvent: " + event.isWatchingLocation() );
	}
	
	public void onEvent(final PushReceivedEvent event) {
		Log.d(TAG, "onPushReceivedEvent start");
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
		Log.d(TAG, "onPushReceivedEvent end");
    }
	
	public void onEvent(final RegistrationEvent event) {
	    Log.d(TAG, "onRegistrationEvent start");
		Log.d(TAG, "Device ID:" + event.getDeviceId());
		Log.d(TAG, "System Token:" + event.getSystemToken());
        Log.d(TAG, "Subscriber key:" + event.getSubscriberKey());
		Log.d(TAG, "onRegistrationEvent end");
    }

}