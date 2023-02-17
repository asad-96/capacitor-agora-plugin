#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorPluginAgoraPlugin, "CapacitorPluginAgora",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(joinChannel, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(leaveChannel, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(showRecordingStatus, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setCountdown, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(updateParticipantLists, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(enterPictureInPictureMode, CAPPluginReturnNone);
)
