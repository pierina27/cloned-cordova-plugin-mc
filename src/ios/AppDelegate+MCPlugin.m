#import "AppDelegate+MCPlugin.h"
#import "ETPush.h"
#import "ETRegion.h"
#import "MCPlugin.h"
#import "MainViewController.h"
#import <Cordova/CDVPlugin.h>
#import <objc/runtime.h>

@implementation AppDelegate (MCPlugin)

static NSData *lastPush;

+ (void)load {  
    Method original =  class_getInstanceMethod(self, @selector(application:didFinishLaunchingWithOptions:));  
    Method custom =    class_getInstanceMethod(self, @selector(application:customDidFinishLaunchingWithOptions:));  
    method_exchangeImplementations(original, custom);  
}  

- (BOOL)application:(UIApplication *)application customDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

	[self application:application customDidFinishLaunchingWithOptions:launchOptions];

    BOOL successful = NO;
    NSError *error = nil;
	
NSBundle* mainBundle = [NSBundle mainBundle];
    NSDictionary* ETSettings = [mainBundle objectForInfoDictionaryKey:@"ETAppSettings"];
    BOOL useGeoLocation = [[ETSettings objectForKey:@"mc_enable_location"] boolValue];
    BOOL useAnalytics = [[ETSettings objectForKey:@"mc_enable_analitycs"] boolValue];
	
    NSString* prodAppID = [ETSettings objectForKey:@"mc_app_id"];
    NSString* prodAccessToken = [ETSettings objectForKey:@"mc_access_token"];
    // configure and set initial settings of the JB4ASDK
	[ETPush setETLoggerToRequiredState:YES];
    successful = [[ETPush pushManager] configureSDKWithAppID:prodAppID
                                              andAccessToken:prodAccessToken
                                               withAnalytics:YES
                                         andLocationServices:YES
                                        andProximityServices:NO
                                               andCloudPages:NO
                                             withPIAnalytics:YES
                                                       error:&error];
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
		
		// This method is required in order for location messaging to work and the user's location to be processed

        [[ETLocationManager sharedInstance] startWatchingLocation];
		[ETRegion retrieveGeofencesFromET];
		
		// you would typically implement this in your AppDelegate didFinishLaunchingWithOptions method to enable background refresh of geofence and beacon messages.
		if([[[UIDevice currentDevice] systemVersion] floatValue] >=7.0){
		
			if ( [[UIApplication sharedApplication] backgroundRefreshStatus] == UIBackgroundRefreshStatusAvailable ){
			
				// setting this will enable iOS to call the app delegate method performFetchWithCompletionHandler periodically. The implementation of that method (see below)
				// will call the JB4ASDK at most once per day to update location and proximity messages in the background - if those services have been enabled.
				// Only call this method if you have LocationServices set to YES in configureSDK()
				// Note that you will require "App downloads content from the network" in your plist for this background app refresh to work
				[[UIApplication sharedApplication] setMinimumBackgroundFetchInterval:UIApplicationBackgroundFetchIntervalMinimum]; 
			}
		}
		
		[[ETPush pushManager] addTag:@"leadclic-mc-plugin v2.0"];
        
        // inform the JB4ASDK of the launch options - possibly UIApplicationLaunchOptionsRemoteNotificationKey or UIApplicationLaunchOptionsLocalNotificationKey
        [[ETPush pushManager] applicationLaunchedWithOptions:launchOptions];
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
		// app is in the foreground so call notification callback
        if (application.applicationState == UIApplicationStateActive) {
			NSLog(@"app active");
			[MCPlugin.etPlugin notifyOfMessage:jsonData];
			[[ETPush pushManager] resetBadgeCount];
		// app is in background or in stand by
		}else{
			NSLog(@"APP WAS CLOSED DURING PUSH RECEPTION (saved)");
			lastPush = jsonData;
		}
    }
	
	/// MCPLUGIN FINAL BLOCK
    
    handler(UIBackgroundFetchResultNoData);
}

// this method will be called by iOS to tell the JB4ASDK to update location and proximity messages. This will only be called if [[UIApplication sharedApplication] setMinimumBackgroundFetchInterval:
// has been set to a value other than UIApplicationBackgroundFetchIntervalNever and Background App Refresh is enabled.
-(void)application:(UIApplication *)application performFetchWithCompletionHandler:(void (^)(UIBackgroundFetchResult) completionHandler{
    
	[[ETPush pushManager] refreshWithFetchCompletionHandler:completionHandler];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {

    NSLog(@"active");

    if (lastPush) {
        [MCPlugin.etPlugin notifyOfMessage:lastPush];
        lastPush = nil;
    }
	[[ETPush pushManager] resetBadgeCount];
}

@end