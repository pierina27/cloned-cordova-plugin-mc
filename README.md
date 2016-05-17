# MarketingCloud Cordova/PhoneGap Push Plugin
> v1.2 Beta release -  Leadclic Solutions - 2016

##Release History
#### Version 1.2
- Added Android >5.0 status bar notification icon resource

#### Version 1.1
- Added sdk functions: Attributes, Tags, Custom Keys and OpenDirect.

#### Version 1.0
- Android SDK 4.2.0 
- iOS SDK 4.1.0
- Available sdk functions: setSubscriberKey and notification capture.

##Installation
```Bash
cordova plugin add https://github.com/soporteleadclic/cordova-plugin-mc \
	--variable PRODAPPID='xxxxx-xxxx-xxxx-xxxx-xxxxxxxx' \
	--variable PRODACCESSTOKEN='xxxxxxxxxxxxxxxxxx' \
	--variable PRODGCMSENDERID='xxxxxxxxxxxxxx' \

```

#### Android compilation details
You will need to ensure that you have installed the following items through the Android SDK Manager:

- Android Support Library version 23 or greater
- Android Support Repository version 20 or greater
- Google Play Services version 27 or greater
- Google Repository version 22 or greater

For Android >5.0 status bar icon, you must to add a transparent with solid color icon with name 'mc_plugin_stat_icon.png' in the 'res' folder, in the same way you add the application icons.

#### Cordova-iOS 4+ known issue

- Install previous cordova version
```Bash
cordova platform remove ios
cordova platform add ios@3.9.2 
```

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
A webView is opened with the specified URL
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
