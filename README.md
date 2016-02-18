# MarketingCloud Cordova/PhoneGap Push Plugin
> Alfa Release -  Leadclic Solutions - 2016

##Installation
```Bash
cordova plugin add https://github.com/soporteleadclic/cordova-plugin-mc \
	--variable PRODAPPID='xxxxx-xxxx-xxxx-xxxx-xxxxxxxx' \
	--variable PRODACCESSTOKEN='xxxxxxxxxxxxxxxxxx' \
	--variable PRODGCMSENDERID='xxxxxxxxxxxxxx' \

```

### Android compilation details
You will need to ensure that you have installed the following items through the Android SDK Manager:

- Android Support Library version 23 or greater
- Android Support Repository version 20 or greater
- Google Play Services version 27 or greater
- Google Repository version 22 or greater

### Cordova iOS 4+ known issue

- Install previous cordova version
```Bash
cordova platform remove ios
cordova platform add ios@3.9.2 
```

##Usage

###Enable Push Notifications

```javascript
MCPlugin.enablePush(); //Device Id as identifier
MCPlugin.enablePush('subscriber@key.com'); //Custom identifier
```

###Disable Push Notifications

```javascript
MCPlugin.disablePush();
```

###Capture Push Notifications

```javascript
MCPlugin.onNotification(function(payload){
    alert(payload.alert);
});
```
