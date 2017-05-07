

#if __has_include(<React/RCTUtils.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTConvert.h>
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTRootView.h>
#import <React/RCTView.h>
#import <React/UIView+React.h>
#import <React/RCTUtils.h>
#else // back compatibility for RN version < 0.40
#import "RCTBridgeModule.h"
#import "RCTConvert.h"
#import "RCTEventDispatcher.h"
#import "RCTRootView.h"
#import "RCTView.h"
#import "RCTBridge.h"
#import "RCTUtils.h"
#import "UIView+React.h"
#endif


@interface RNHTMLtoPDF : RCTView <RCTBridgeModule, UIWebViewDelegate>

@end
