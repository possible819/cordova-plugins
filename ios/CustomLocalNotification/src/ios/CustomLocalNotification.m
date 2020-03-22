/********* CustomLocalNotification Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

@import UserNotifications;

UNAuthorizationOptions authorizationOptions = (UNAuthorizationOptionAlert + UNAuthorizationOptionSound);
NSString* REQUST_IDENTIFIER = @"ALARM_IDENTIFIER";

@interface CustomLocalNotification : CDVPlugin {
}

- (void)addNotification:(CDVInvokedUrlCommand*)command;
- (void)registerNotification:(CDVInvokedUrlCommand*)command;
- (void)nonGrantedAuthorization:(CDVInvokedUrlCommand*)command;
- (void)clearNotification:(CDVInvokedUrlCommand*)command;
- (void)throwError:(CDVInvokedUrlCommand*)command :(NSError*) error;

@end

@implementation CustomLocalNotification

- (void)addNotification:(CDVInvokedUrlCommand*)command
{
    UNUserNotificationCenter* center = [UNUserNotificationCenter currentNotificationCenter];
    [center requestAuthorizationWithOptions:authorizationOptions
                          completionHandler:^(BOOL granted, NSError * _Nullable error) {
                              if(error) {
                                  [self throwError:command :error];
                              } else if(granted) {
                                  [self registerNotification:command];
                              } else {
                                  [self nonGrantedAuthorization:command];
                              }
                          }];
}

-(void)registerNotification:(CDVInvokedUrlCommand *)command
{
    [self clearNotification:nil];
    
    NSString* title = [[command arguments] objectAtIndex:0];
    NSString* subtitle = [[command arguments] objectAtIndex:1];
    NSString* body = [[command arguments] objectAtIndex:2];
    int hour = [[[command arguments] objectAtIndex:3] intValue];
    int minute = [[[command arguments] objectAtIndex:4] intValue];
    
    UNUserNotificationCenter* center = [UNUserNotificationCenter currentNotificationCenter];
    
    UNMutableNotificationContent* content = [[UNMutableNotificationContent alloc] init];
    [content setTitle:title];
    [content setBody:body];
    if([subtitle class] == [NSString class] && [subtitle length] > 0) {
        [content setSubtitle:subtitle];
    }
    [content setSound:UNNotificationSound.defaultSound];
    
    NSDateComponents* dateComponents = [[NSDateComponents alloc] init];
    [dateComponents setHour:hour];
    [dateComponents setMinute:minute];
    [dateComponents setSecond:0];
    
    UNCalendarNotificationTrigger* trigger = [UNCalendarNotificationTrigger triggerWithDateMatchingComponents:dateComponents
                                                                                                      repeats:YES];
    
    UNNotificationRequest* notificationRequest = [UNNotificationRequest requestWithIdentifier:REQUST_IDENTIFIER
                                                                                      content:content
                                                                                      trigger:trigger];
    
    [center addNotificationRequest:notificationRequest
             withCompletionHandler:^(NSError * _Nullable error) {
                 if(error != nil) {
                     [self throwError:command :error];
                 } else {
                     [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
                 }
             }];
}

-(void)nonGrantedAuthorization:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"non-granted"] callbackId:command.callbackId];
}

-(void)clearNotification:(CDVInvokedUrlCommand*)command
{
    UNUserNotificationCenter* center = [UNUserNotificationCenter currentNotificationCenter];
    [center removeAllPendingNotificationRequests];
    
    if(command != nil) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    }
}

-(void)throwError:(CDVInvokedUrlCommand *)command :(NSError *)error
{
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR] callbackId:command.callbackId];
}


@end
