var exec = require('cordova/exec');

function MCPlugin() { 
	console.log("MCPlugin.js: is created");
}

MCPlugin.prototype.setSubscriberKey = function(success, error, subscriberKey){
	exec(success, error , "MCPlugin",'setSubscriberKey',[subscriberKey]);
}

MCPlugin.prototype.addAttribute = function(success, error, key, value){
	exec(success, error , "MCPlugin",'addAttribute',[key, value]);
}

MCPlugin.prototype.removeAttribute = function(success, error, key){
	exec(success, error , "MCPlugin",'removeAttribute',[key]);
}

MCPlugin.prototype.addTag = function(success, error, value){
	exec(success, error , "MCPlugin",'addTag',[value]);
}

MCPlugin.prototype.removeTag = function(success, error, value){
	exec(success, error , "MCPlugin",'removeTag',[value]);
}

MCPlugin.prototype.onNotificationReceived = function(payload){
	console.log("Received push notification")
	console.log(payload)
}

MCPlugin.prototype.onNotification = function( callback ){
	MCPlugin.prototype.onNotificationReceived = callback;
	exec(function(result){ console.log("Notification callback OK") }, function(result){ console.log("Notification callback ERROR") }, "MCPlugin", 'registerNotification',[]);
}

//ready
exec(function(result){ console.log("MCPlugin Ready OK") }, function(result){ console.log("MCPlugin Ready ERROR") }, "MCPlugin",'ready',[]);

var mcPlugin = new MCPlugin();
module.exports = mcPlugin;
