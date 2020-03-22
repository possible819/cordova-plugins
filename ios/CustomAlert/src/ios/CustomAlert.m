/********* CustomAlert.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

@interface CustomAlert : CDVPlugin {
}
- (void)init:(CDVInvokedUrlCommand*)command;
- (void)alert:(CDVInvokedUrlCommand*)command;
- (UIViewController*)getViewController;
@end

@implementation CustomAlert
- (void)init:(CDVInvokedUrlCommand*)command
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(dismissAlert:)name:UIApplicationWillResignActiveNotification object:nil];
}

- (void)alert:(CDVInvokedUrlCommand*)command
{
    NSString* alertType = [command.arguments objectAtIndex:0];
    NSString* title = [command.arguments objectAtIndex:1];
    NSString* message = [command.arguments objectAtIndex:2];
    NSArray* buttons = [command.arguments objectAtIndex:3];
    NSString* callbackId = command.callbackId;
    
    CDVPluginResult* pluginResult = nil;
    
    if (title == nil) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Title is not defined."];
    }
    
    if (message == nil) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Message is not defined."];
    }
    
    if ([buttons count] == 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Buttons are not defined."];
    }

    UIAlertController* alertController;
    
    if([alertType isEqualToString:@"DEFAULT"]) {
        alertController = [UIAlertController alertControllerWithTitle:title
                                                              message:message
                                                       preferredStyle:UIAlertControllerStyleAlert];
    } else if ([alertType isEqualToString:@"ACTION"]) {
        alertController = [UIAlertController alertControllerWithTitle:title
                                                              message:message
                                                       preferredStyle:UIAlertControllerStyleActionSheet];
    }
    
    __weak CustomAlert* customAlert = self;
    
    for(NSDictionary* button in buttons) {
        NSString* buttonText = button[@"name"];
        NSString* buttonType = button[@"type"];
        
        if([buttonType isEqualToString:@"DEFAULT"]) {
            [alertController addAction:[UIAlertAction actionWithTitle:buttonText
                                                                style:UIAlertActionStyleDefault
                                                              handler:^(UIAlertAction * action)
            {
                CDVPluginResult* buttonResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:buttonText];
                [customAlert.commandDelegate sendPluginResult:buttonResult callbackId:callbackId];
            }]];
        } else if ([buttonType isEqualToString:@"CANCEL"]) {
            [alertController addAction:[UIAlertAction actionWithTitle:buttonText
                                                                style:UIAlertActionStyleCancel
                                                              handler:^(UIAlertAction * action)
            {
                CDVPluginResult* buttonResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:buttonText];
                [customAlert.commandDelegate sendPluginResult:buttonResult callbackId:callbackId];
            }]];
        } else if ([buttonType isEqualToString:@"DESTRUCTIVE"]) {
            [alertController addAction:[UIAlertAction actionWithTitle:buttonText
                                                                style:UIAlertActionStyleDestructive
                                                              handler:^(UIAlertAction * action)
            {
                CDVPluginResult* buttonResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:buttonText];
                [customAlert.commandDelegate sendPluginResult:buttonResult callbackId:callbackId];
            }]];
        }
        
    }
    
    [self.getViewController presentViewController:alertController
                                           animated:YES
                                         completion:^{
                                             NSLog(@"Alert presented.");
                                         }];
}

- (void)dismissAlert:(UIApplication *)application
{
    [self.getViewController dismissViewControllerAnimated:YES
                                               completion:^{
                                                   NSLog(@"Alert Dismissed");
                                               }];
}

- (UIViewController*)getViewController
{
    UIViewController *presentingViewController = self.viewController;
    if (presentingViewController.view.window != [UIApplication sharedApplication].keyWindow){
        presentingViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    }
    
    while (presentingViewController.presentedViewController != nil && ![presentingViewController.presentedViewController isBeingDismissed]){
        presentingViewController = presentingViewController.presentedViewController;
    }
    
    return presentingViewController;
}

@end
