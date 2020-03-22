/********* CustomOpenSettingView.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

@interface CustomOpenSettingView : CDVPlugin {
}

- (void)open:(CDVInvokedUrlCommand*)command;
@end

@implementation CustomOpenSettingView

- (void)open:(CDVInvokedUrlCommand*)command
{
    __block CDVPluginResult* pluginResult = nil;
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString: UIApplicationOpenSettingsURLString]
                                       options:@{}
                             completionHandler:^(BOOL success) {
                                 if(success) {
                                     pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
                                 } else {
                                     pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
                                 }
                                 
                                 [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                             }];
}

@end
