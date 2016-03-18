#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface MCPlugin : CDVPlugin
{
    //NSString *notificationCallBack;
}

+ (MCPlugin *) etPlugin;
- (void)ready:(CDVInvokedUrlCommand*)command;
- (void)setSubscriberKey:(CDVInvokedUrlCommand*)command;
- (void)registerNotification:(CDVInvokedUrlCommand*)command;
- (void)notifyOfMessage:(NSData*) payload;

@end