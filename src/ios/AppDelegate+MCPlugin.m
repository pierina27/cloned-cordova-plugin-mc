#import "AppDelegate+MCPlugin.h"
#import "ETPush.h"
#import "MCPlugin.h"
#import "MainViewController.h"
#import <Cordova/CDVPlugin.h>
#import <objc/runtime.h>

@implementation AppDelegate (MCPlugin)

+ (void)load {  
    Method original =    class_getInstanceMethod(self, @selector(application:didFinishLaunchingWithOptions:));  
    Method custom =    class_getInstanceMethod(self, @selector(application:customDidFinishLaunchingWithOptions:));  
    method_exchangeImplementations(original, custom);  
}  

- (BOOL)application:(UIApplication *)application customDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

	[self application:application customDidFinishLaunchingWithOptions:launchOptions];

    BOOL successful = NO;
    NSError *error = nil;
	
NSBundle* mainBundle = [NSBundle mainBundle];
    NSDictionary* ETSettings = [mainBundle objectForInfoDictionaryKey:@"ETAppSettings"];
    BOOL useGeoLocation = [[ETSettings objectForKey:@"UseGeofences"] boolValue];
    BOOL useAnalytics = [[ETSettings objectForKey:@"UseAnalytics"] boolValue];
	
#ifdef DEBUG
    NSString* devAppID = [ETSettings objectForKey:@"ApplicationID-Dev"];
    NSString* devAccessToken = [ETSettings objectForKey:@"AccessToken-Dev"];
    // Set to YES to enable logging while debugging
    [ETPush setETLoggerToRequiredState:YES];
    
    // configure and set initial settings of the JB4ASDK
    successful = [[ETPush pushManager] configureSDKWithAppID:devAppID
                                              andAccessToken:devAccessToken
                                               withAnalytics:NO
                                         andLocationServices:NO
                                               andCloudPages:NO
                                             withPIAnalytics:NO
                                                       error:&error];
#else
    NSString* prodAppID = [ETSettings objectForKey:@"ApplicationID-Prod"];
    NSString* prodAccessToken = [ETSettings objectForKey:@"AccessToken-Prod"];
    // configure and set initial settings of the JB4ASDK
    successful = [[ETPush pushManager] configureSDKWithAppID:prodAppID
                                              andAccessToken:prodAccessToken
                                               withAnalytics:NO
                                         andLocationServices:NO
                                               andCloudPages:NO
                                             withPIAnalytics:NO
                                                       error:&error];
#endif
    //
    // if configureSDKWithAppID returns NO, check the error object for detailed failure info. See PushConstants.h for codes.
    // the features of the JB4ASDK will NOT be useable unless configureSDKWithAppID returns YES.
    //
    if (!successful) {
        dispatch_async(dispatch_get_main_queue(), ^{
            // something failed in the configureSDKWithAppID call - show what the error is
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Failed configureSDKWithAppID!", @"Failed configureSDKWithAppID!")
                                        message:[error localizedDescription]
                                       delegate:nil
                              cancelButtonTitle:NSLocalizedString(@"OK", @"OK")
                              otherButtonTitles:nil] show];
        });
    }
    else {
        // register for push notifications - enable all notification types, no categories
        UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:
                                                UIUserNotificationTypeBadge |
                                                UIUserNotificationTypeSound |
                                                UIUserNotificationTypeAlert
                                                                                 categories:nil];
        
        [[ETPush pushManager] registerUserNotificationSettings:settings];
        [[ETPush pushManager] registerForRemoteNotifications];
        
        // inform the JB4ASDK of the launch options - possibly UIApplicationLaunchOptionsRemoteNotificationKey or UIApplicationLaunchOptionsLocalNotificationKey
        [[ETPush pushManager] applicationLaunchedWithOptions:launchOptions];

        // This method is required in order for location messaging to work and the user's location to be processed

        [[ETLocationManager sharedInstance] startWatchingLocation];
    }
	
	return YES;
    
}

- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
    // inform the JB4ASDK of the notification settings requested
    [[ETPush pushManager] didRegisterUserNotificationSettings:notificationSettings];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    // inform the JB4ASDK of the device token
    [[ETPush pushManager] registerDeviceToken:deviceToken];
}

-(void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    // inform the JB4ASDK that the device failed to register and did not receive a device token
    [[ETPush pushManager] applicationDidFailToRegisterForRemoteNotificationsWithError:error];
}


-(void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    // inform the JB4ASDK that the device received a local notification
    [[ETPush pushManager] handleLocalNotification:notification];
	
	/// MCPLUGIN START BLOCK
	NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:notification.userInfo
                                                       options:0
                                                         error:&error];
    if (!jsonData) {
        NSLog(@"jsn error: %@", error);
    } else {

        [MCPlugin.etPlugin notifyOfMessage:jsonData];
    }
	/// MCPLUGIN FINAL BLOCK
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult result))handler {
    
    // inform the JB4ASDK that the device received a remote notification
    [[ETPush pushManager] handleNotification:userInfo forApplicationState:application.applicationState];
	
	/// MCPLUGIN START BLOCK
	NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:userInfo
                                                       options:0
                                                         error:&error];
    if (!jsonData) {
        NSLog(@"json error: %@", error);
    } else {
        [MCPlugin.etPlugin notifyOfMessage:jsonData];
    }
	/// MCPLUGIN FINAL BLOCK
    
    // is it a silent push?
    if (userInfo[@"aps"][@"content-available"]) {
        // received a silent remote notification...
        
        // indicate a silent push
        [[UIApplication sharedApplication] setApplicationIconBadgeNumber:1];
    }
    else {
        // received a remote notification...
        
        // clear the badge
        [[ETPush pushManager] resetBadgeCount];
    }
    
    handler(UIBackgroundFetchResultNoData);
}

@end