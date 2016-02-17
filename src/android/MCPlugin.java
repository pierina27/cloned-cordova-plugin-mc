package io.cordova.hellocordova;

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
 
	private static final String TAG = "ETSDKWrapper";
	
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
			 cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                  if(MainApplication.lastPush != null) MCPlugin.sendPushPayload( MainApplication.lastPush );
				  MainApplication.lastPush = null;
                }
              });
			}
		
			if (action.equals("enablePush")) {
			  if( args.getString(0) != null ) ETPush.getInstance().setSubscriberKey( args.getString(0) );
			  ETPush.getInstance().enablePush();
			}
		
			if (action.equals("disablePush")) {
			  ETPush.getInstance().disablePush();
			}
			
			callbackContext.success("Bieeeeeeeeeeeeeeeeeeeeeen! " + action);
			
		}catch(Exception e){
			Log.d(TAG, "ERROR: onRegistrationEvent" + e.getMessage());
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