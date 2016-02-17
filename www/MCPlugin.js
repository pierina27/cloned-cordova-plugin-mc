var exec = require('cordova/exec');

function MCPlugin() { 
	console.log("MCPlugin.js: is created");
}

MCPlugin.prototype.enablePush = function(subscriberKey){
	exec(function(result){ alert("OK: " + result); },
		 function(result){ alert("KO: " + result); },
		 "MCPlugin",'enablePush',[subscriberKey]
	);
}

MCPlugin.prototype.disablePush = function(){
	exec(function(result){ alert("OK: " + result); },
		 function(result){ alert("KO: " + result); },
		 "MCPlugin",'disablePush',[]
  );
}

MCPlugin.prototype.onNotificationReceived = function(payload){
	alert(payload.alert);
}

MCPlugin.prototype.onNotification = function( callback ){
	MCPlugin.prototype.onNotificationReceived = callback;
}

//ready
exec(function(result){ },
	function(result){ },
	"MCPlugin",'ready',[]
);

var mcPlugin = new MCPlugin();
module.exports = mcPlugin;
