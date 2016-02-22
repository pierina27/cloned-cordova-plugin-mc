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
        
        [[ETPush pushManager] setSubscriberKey:subKey];
        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:subKey];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

- (void) enablePush:(CDVInvokedUrlCommand *)command
{
    NSString* subKey = [command.arguments objectAtIndex:0];
    NSLog(@"setting sub key %@", subKey);
    [self.commandDelegate runInBackground:^{
        
        [[ETPush pushManager] setSubscriberKey:subKey];
        CDVPluginResult* pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:subKey];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
    
}

-(void) notifyOfMessage:(NSData *)payload
{
    NSString *JSONString = [[NSString alloc] initWithBytes:[payload bytes] length:[payload length] encoding:NSUTF8StringEncoding];
    NSString * notifyJS = [NSString stringWithFormat:@"%@('%@');", notificationCallback, JSONString];
    NSLog(@"stringByEvaluatingJavaScriptFromString %@", notifyJS);
    
    NSString *jsResults = [self.webView stringByEvaluatingJavaScriptFromString:notifyJS];
}


@end