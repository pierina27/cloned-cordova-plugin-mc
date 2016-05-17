package com.leadclic;

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
 
public class MCPlugin extends CordovaPlugin {
 
	private static final String TAG = "MCPlugin";
	
	public static CordovaWebView gWebView;
	public static String notificationCallBack = "MCPlugin.onNotificationReceived";
	 
	public MCPlugin() {}
	
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		gWebView = webView;
		Log.d(TAG, "MCPlugin INITIALZE");
	}
	 
	public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

		Log.d(TAG,"MCPlugin RECEIVED: "+ action);
		
		try{
			if (action.equals("ready")) {
				//It seems that the SDK enables push automatically
				//ETPush.getInstance().enablePush();
			}
			// NOTIFICATION CALLBAACK REGISTER //
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