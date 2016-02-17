package io.cordova.hellocordova;

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

import android.app.Activity;
import android.content.Context;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

public class MainApplication extends Application {

	private static final String TAG = "ETSDKWrapper";
	public static Bundle lastPush = null;

    @Override
    public void onCreate() {
        super.onCreate();
		try {
		  Log.d(TAG, "AAAA");
		  EventBus.getInstance().register(this);
          ETPushConfig.Builder pushConfigBuilder = new ETPushConfig.Builder( this );
		  Log.d(TAG, "BBBB");
		  ETPush.readyAimFire(this, getString(R.string.et_app_id_prod), getString(R.string.et_access_token_prod), getString(R.string.gcm_sender_id_prod), false, false, false, false);
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
			etPush.addTag("2.0.0");
		} catch (ETException e) {
			Log.d(TAG, "ERROR onReadyAimFireInitCompletedEvent: " + e.getMessage());
		}
		Log.d(TAG, "onReadyAimFireInitCompletedEvent end");
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