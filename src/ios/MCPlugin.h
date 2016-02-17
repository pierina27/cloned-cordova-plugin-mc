#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface MCPlugin : CDVPlugin
{
    //NSString *notificationCallBack;
}

+ (MCPlugin *) etPlugin;

- (void)enablePush:(CDVInvokedUrlCommand*)command;
- (void)notifyOfMessage:(NSData*) payload;

@end