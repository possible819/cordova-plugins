/********* CustomAppStatusDispatcher.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

@interface CustomAppStatusDispatcher : CDVPlugin 
- (void)init:(CDVInvokedUrlCommand*)command;
@end

@implementation CustomAppStatusDispatcher
- (void)init:(CDVInvokedUrlCommand*)command
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onAppDidBecomeActive:)name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onAppWillResignActive:)name:UIApplicationWillResignActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onAppDidEnterToBackground:)name:UIApplicationDidEnterBackgroundNotification object:nil];
}

- (void)onAppDidBecomeActive:(UIApplication *)application
{
    [self.commandDelegate evalJs:@"document.dispatchEvent(new CustomEvent('app-actived'))"];
}

- (void)onAppWillResignActive:(UIApplication *)application
{
    [self.commandDelegate evalJs:@"document.dispatchEvent(new CustomEvent('app-deactived'))"];
}

- (void)onAppDidEnterToBackground:(UIApplication *)application
{
    [self.commandDelegate evalJs:@"document.dispatchEvent(new CustomEvent('app-enter-background'))"];
}
@end
