#include <sys/types.h>
#include <sys/sysctl.h>

#import <Cordova/CDV.h>
#import "MCPlugin.h"
#import "ETPush.h"


@interface MCPlugin () {}
@end

@implementation MCPlugin

static NSString *notificationCallback = @"MCPlugin.onNotificationReceived";
static MCPlugin *etPluginInstance;

+ (MCPlugin *) etPlugin {
    
    return etPluginInstance;
}

- (void) ready:(CDVInvokedUrlCommand *)command
{
    etPluginInstance = self;
    [self.commandDelegate runInBackground:^{
        
        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

// SUBSCRIBER KEY //
- (void) setSubscriberKey:(CDVInvokedUrlCommand *)command {
    NSString* subKey = [command.arguments objectAtIndex:0];
    NSLog(@"setting sub key %@", subKey);
    [self.commandDelegate runInBackground:^{
        
        if(subKey != nil)[[ETPush pushManager] setSubscriberKey:subKey];
        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:subKey];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

- (void) getSubscriberKey:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^ {
        NSString* subscriberKey = [[ETPush pushManager] getSubscriberKey];
        CDVPluginResult* pluginResult = nil;
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:subscriberKey];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

// ATTRIBUTES //
- (void) addAttribute:(CDVInvokedUrlCommand *)command {
    NSString* key = [command.arguments objectAtIndex:0];
    NSString* value = [command.arguments objectAtIndex:1];
    
    [self.commandDelegate runInBackground:^ {
        bool success = [[ETPush pushManager] addAttributeNamed:key value:value];
        CDVPluginResult* pluginResult = nil;
        
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"OK"];
        }
        else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"ERROR"];
        }
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void) removeAttribute:(CDVInvokedUrlCommand *)command {
    NSString* key = [command.arguments objectAtIndex:0];
    
    [self.commandDelegate runInBackground:^ {
        bool success = [[ETPush pushManager] removeAttributeNamed:key];
        CDVPluginResult* pluginResult = nil;
        
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"OK"];
        }
        else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"ERROR"];
        }
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

// TAGS //
- (void) addTag:(CDVInvokedUrlCommand *)command {
    NSString* tagName = [command.arguments objectAtIndex:0];
    
    [self.commandDelegate runInBackground:^ {
        bool success = [[ETPush pushManager] addTag:tagName];
        CDVPluginResult* pluginResult = nil;
        
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"OK"];
        }
        else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"ERROR"];
        }
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void) removeTag:(CDVInvokedUrlCommand *)command {
    NSString* tagName = [command.arguments objectAtIndex:0];
    
    [self.commandDelegate runInBackground:^ {
        bool success = [[ETPush pushManager] removeTag:tagName];
        CDVPluginResult* pluginResult = nil;
        
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"OK"];
        }
        else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"ERROR"];
        }
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

// MONITOR LOCATION //
- (void) startWatchingLocation:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^ {
        NSLog(@"startWatchingLocation called");
        [[ETLocationManager sharedInstance] startWatchingLocation];
    }];
}

- (void) stopWatchingLocation:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^ {
        NSLog(@"stopWatchingLocation called");
        [[ETLocationManager sharedInstance] stopWatchingLocation];
    }];
}

- (void) isWatchingLocation:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^ {
        BOOL* isWatchingLocation = [[ETLocationManager sharedInstance] getWatchingLocation];
        CDVPluginResult* pluginResult = nil;
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:isWatchingLocation ? @"true" : @"false"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

// SDK STATE //
- (void) getSDKState:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^ {
        NSString* SDKState = [ETPush getSDKState];
        CDVPluginResult* pluginResult = nil;
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:SDKState];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void) registerNotification:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        
        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

-(void) notifyOfMessage:(NSData *)payload
{
    NSString *JSONString = [[NSString alloc] initWithBytes:[payload bytes] length:[payload length] encoding:NSUTF8StringEncoding];
    NSString * notifyJS = [NSString stringWithFormat:@"%@(%@);", notificationCallback, JSONString];
    NSLog(@"stringByEvaluatingJavaScriptFromString %@", notifyJS);
    
    NSString *jsResults = [self.webView stringByEvaluatingJavaScriptFromString:notifyJS];
}


@end
