#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface MCPlugin : CDVPlugin
{
    //NSString *notificationCallBack;
}

+ (MCPlugin *) etPlugin;
- (void)ready:(CDVInvokedUrlCommand*)command;
- (void)setSubscriberKey:(CDVInvokedUrlCommand*)command;
- (void)getSubscriberKey:(CDVInvokedUrlCommand*)command;
- (void)addAttribute:(CDVInvokedUrlCommand*)command;
- (void)removeAttribute:(CDVInvokedUrlCommand*)command;
- (void)addTag:(CDVInvokedUrlCommand*)command;
- (void)removeTag:(CDVInvokedUrlCommand*)command;
- (void)startWatchingLocation:(CDVInvokedUrlCommand*)command;
- (void)stopWatchingLocation:(CDVInvokedUrlCommand*)command;
- (void)isWatchingLocation:(CDVInvokedUrlCommand*)command;
- (void)getSDKState:(CDVInvokedUrlCommand*)command;
- (void)registerNotification:(CDVInvokedUrlCommand*)command;
- (void)notifyOfMessage:(NSData*) payload;

@end