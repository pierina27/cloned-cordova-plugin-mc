#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface MCPlugin : CDVPlugin
{
    //NSString *notificationCallBack;
}

+ (MCPlugin *) etPlugin;

- (void)isPushEnabled:(CDVInvokedUrlCommand*)command;
- (void)setSubscriberKey:(CDVInvokedUrlCommand*)command;
- (void)addTag:(CDVInvokedUrlCommand*)command;
- (void)removeTag:(CDVInvokedUrlCommand*)command;
- (void)addAttribute:(CDVInvokedUrlCommand*)command;
- (void)removeAttribute:(CDVInvokedUrlCommand*)command;
- (void)resetBadgeCount:(CDVInvokedUrlCommand*)command;
- (void)registerForNotifications:(CDVInvokedUrlCommand*)command;
- (void)notifyOfMessage:(NSData*) payload;

@end