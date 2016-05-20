var exec = require('cordova/exec');

function MCPlugin() { 
	console.log("MCPlugin.js: is created");
}




// SUBSCRIBER KEY //
MCPlugin.prototype.setSubscriberKey = function(subscriberKey, success, error){
	exec(success, error , "MCPlugin",'setSubscriberKey',[subscriberKey]);
}
// ATTRIBUTES //
MCPlugin.prototype.addAttribute = function(key, value, success, error){
	exec(success, error , "MCPlugin",'addAttribute',[key, value]);
}
MCPlugin.prototype.removeAttribute = function(key, success, error){
	exec(success, error , "MCPlugin",'removeAttribute',[key]);
}
// TAGS //
MCPlugin.prototype.addTag = function(tag, success, error){
	exec(success, error , "MCPlugin",'addTag',[tag]);
}
MCPlugin.prototype.removeTag = function(tag, success, error){
	exec(success, error , "MCPlugin",'removeTag',[tag]);
}
// MONITOR LOCATION //
MCPlugin.prototype.startWatchingLocation = function(success, error){
	exec(success, error , "MCPlugin",'startWatchingLocation',[]);
}
MCPlugin.prototype.stopWatchingLocation = function(success, error){
	exec(success, error , "MCPlugin",'stopWatchingLocation',[]);
}
MCPlugin.prototype.isWatchingLocation = function(success, error){
	exec(success, error , "MCPlugin",'isWatchingLocation',[]);
}
// SDK STATE //
MCPlugin.prototype.getSDKState = function(success, error){
	exec(success, error , "MCPlugin",'getSDKState',[]);
}
// NOTIFICATION CALLBACK //
MCPlugin.prototype.onNotification = function( callback ){
	MCPlugin.prototype.onNotificationReceived = callback;
	exec(function(result){ console.log("Notification callback OK") }, function(result){ console.log("Notification callback ERROR") }, "MCPlugin", 'registerNotification',[]);
}




// DEFAULT NOTIFICATION CALLBACK //
MCPlugin.prototype.onNotificationReceived = function(payload){
	console.log("Received push notification")
	console.log(payload)
}
// READY //
exec(function(result){ console.log("MCPlugin Ready OK") }, function(result){ console.log("MCPlugin Ready ERROR") }, "MCPlugin",'ready',[]);

var mcPlugin = new MCPlugin();
module.exports = mcPlugin;
