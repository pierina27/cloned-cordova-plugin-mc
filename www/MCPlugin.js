var exec = require('cordova/exec');

function MCPlugin() { 
	console.log("MCPlugin.js: is created");
}

MCPlugin.prototype.enablePush = function(success, error, subscriberKey){
	exec(success, error , "MCPlugin",'enablePush',[subscriberKey]);
}
//ONLY ANDROID
MCPlugin.prototype.disablePush = function(success, error){
	exec(success, error , "MCPlugin",'disablePush',[]);
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
