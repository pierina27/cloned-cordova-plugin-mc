# MarketingCloud Cordova/PhoneGap Push Plugin
> v1.1 Beta release -  Leadclic Solutions - 2016

##Release History
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

####Add Attribute

```javascript
MCPlugin.addAttribute('key', 'value');
```

####Remove Attribute

```javascript
MCPlugin.removeAttribute('key');
```

####Add Tag

```javascript
MCPlugin.addTag('value');
```

####Remove Tag

```javascript
MCPlugin.removeTag('value');
```

####OpenDirect
A webView is opened with the Specified URL

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
