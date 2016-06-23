# MarketingCloud Cordova/PhoneGap Push Plugin
> v2.0 Beta release -  Leadclic Solutions - 2016

##Release History
#### Version 2.1
- Fixed Cordova-ios 4.x compatibility problem

#### Version 2.0
- Android SDK 4.4.0
- iOS SDK 4.3.0
- Added Geofence capabilities

#### Version 1.2
- Added Android >5.0 status bar notification icon resource. (See Android compilation details)

#### Version 1.1
- Added sdk functions: Attributes, Tags, Custom Keys and OpenDirect.

#### Version 1.0
- Android SDK 4.2.0 
- iOS SDK 4.1.0
- Available sdk functions: setSubscriberKey and notification capture.

##Installation
```Bash
cordova plugin add https://github.com/soporteleadclic/cordova-plugin-mc \
	--variable APPID='xxxxx-xxxx-xxxx-xxxx-xxxxxxxx' \
	--variable ACCESSTOKEN='xxxxxxxxxxxxxxxxxx' \
	--variable GCMSENDERID='xxxxxxxxxxxxxx' \

```

#### Android compilation details
You will need to ensure that you have installed the following items through the Android SDK Manager:

- Android Support Library version 23 or greater
- Android Support Repository version 20 or greater
- Google Play Services version 27 or greater
- Google Repository version 22 or greater

:warning: For Android >5.0 status bar icon, you must to include transparent solid color icon with name 'mc_plugin_stat_icon.png' in the 'res' folder in the same way you add the other application icons.
If you do not set this resource, then the SDK will use the default icon for your app which may not meet the standards for Android 5.0.

##Usage

####Set Subscriber Key

```javascript
//If this function is not called, device id is used as default subscriber key.
MCPlugin.setSubcriberKey('subscriberkey@example.com');
```

####Add/Remove Attribute

```javascript
MCPlugin.addAttribute('key', 'value');
MCPlugin.removeAttribute('key');
```

####Add/Remove Tag

```javascript
MCPlugin.addTag('value');
MCPlugin.removeTag('value');
```

####OpenDirect
```
The OpenDirect customized push message contains a URL to open in a web view.
By default, the SDK will open the URL in a web view.
The URL must include the protocol (http:// or https://).
```

####Geofence
:warning: Geofence capabilities are enabled by default
```javascript
MCPlugin.isWatchingLocation(function(result){
	console.log(result)
	//result = "true"|"false" depending on location status
});
```

####Capture Push Notifications

```javascript
//Android example
MCPlugin.onNotification(function(payload){
    alert(payload.alert);
    alert(payload.customKey);//Retrieve customKey
});

//iOS example
MCPlugin.onNotification(function(payload){
    alert(payload.aps.alert);
    alert(payload.aps.customKey);//Retrieve customKey
});
```
