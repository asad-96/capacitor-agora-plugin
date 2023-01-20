import Foundation
import Capacitor
import UIKit
import AVFoundation
import AgoraRtcKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorPluginAgoraPlugin)
public class CapacitorPluginAgoraPlugin: CAPPlugin {
    private let implementation = CapacitorPluginAgora()
    
    // The main entry point for Video SDK
    var agoraEngine: AgoraRtcEngineKit!
    
    // By default, set the current user role to broadcaster to both send and receive streams.
    var userRole: AgoraClientRole = .broadcaster
    
    // The video feed for the local user is displayed here
    var localView = UIView()
    
    // The video feed for the remote user is displayed here
    var remoteView = UIView()
    
    var btnLeave = UIButton()
    var joined: Bool = false
    
    private var EVENT = "event";
    private var UID = "uid";
    private var ELAPSED = "elapsed";
    private var CHANNEL = "channel";
    private var REASON = "reason";
    
    
    //MARK: Methods
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
    
    @objc func joinChannel(_ call: CAPPluginCall) {
        let channelName = call.getString(Constant.CHANNELNAME) ?? ""
        let uid = call.getInt(Constant.UID) ?? 0
        let token = call.getString(Constant.TOKEN) ?? ""
        let appId = call.getString(Constant.APPID) ?? ""
        
        initializeAgoraEngine(appId: appId)
        initViews()
        joinChannel(channelName: channelName, uid: UInt(uid), token: token)
        call.resolve([
            "value": implementation.echo("Join channel value")
        ])
    }
    
    @objc func leaveChannel(_ call: CAPPluginCall) {
        call.resolve([
            "value": implementation.echo("leaveChannel value")
        ])
    }
    
    //MARK: Sub Functions
    func initViews() {
        DispatchQueue.main.async {
            self.remoteView.frame = UIScreen.main.bounds
            self.localView.frame =  CGRect(x: UIScreen.main.bounds.width - 90, y: 0, width: 120, height: 160)
            let currentWindow: UIWindow? = UIApplication.shared.windows.first
            self.remoteView.backgroundColor = .white
            self.localView.backgroundColor = .white
            currentWindow?.addSubview(self.remoteView)
            currentWindow?.addSubview(self.localView)
            
            self.btnLeave.frame = CGRect(x: UIScreen.main.bounds.width - (UIScreen.main.bounds.width / 1.5), y: UIScreen.main.bounds.height - 120, width: 120, height: 60)
            self.btnLeave.setTitle("Leave", for: .normal)
            self.btnLeave.backgroundColor = UIColor.link
            self.btnLeave.layer.cornerRadius = self.btnLeave.frame.height / 2
            self.btnLeave.addTarget(self, action: #selector(self.leaveChannelUser), for: .touchUpInside)
            currentWindow?.addSubview(self.btnLeave)
        }
    }
    
    func checkForPermissions() -> Bool {
        var hasPermissions = false
        
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized: hasPermissions = true
        default: hasPermissions = requestCameraAccess()
        }
        // Break out, because camera permissions have been denied or restricted.
        if !hasPermissions { return false }
        switch AVCaptureDevice.authorizationStatus(for: .audio) {
        case .authorized: hasPermissions = true
        default: hasPermissions = requestAudioAccess()
        }
        return hasPermissions
    }
    
    func requestCameraAccess() -> Bool {
        var hasCameraPermission = false
        let semaphore = DispatchSemaphore(value: 0)
        AVCaptureDevice.requestAccess(for: .video, completionHandler: { granted in
            hasCameraPermission = granted
            semaphore.signal()
        })
        semaphore.wait()
        return hasCameraPermission
    }
    
    func requestAudioAccess() -> Bool {
        var hasAudioPermission = false
        let semaphore = DispatchSemaphore(value: 0)
        AVCaptureDevice.requestAccess(for: .audio, completionHandler: { granted in
            hasAudioPermission = granted
            semaphore.signal()
        })
        semaphore.wait()
        return hasAudioPermission
    }
    
    func initializeAgoraEngine(appId: String) {
        let config = AgoraRtcEngineConfig()
        // Pass in your App ID here.
        config.appId = appId
        // Use AgoraRtcEngineDelegate for the following delegate parameter.
        agoraEngine = AgoraRtcEngineKit.sharedEngine(with: config, delegate: self)
        
    }
    
    func joinChannel(channelName: String, uid: UInt, token: String) {
        if !self.checkForPermissions() {
            return
        }
        
        let option = AgoraRtcChannelMediaOptions()
        
        // Set the client role option as broadcaster or audience.
        if self.userRole == .broadcaster {
            option.clientRoleType = .broadcaster
            setupLocalVideo()
        } else {
            option.clientRoleType = .audience
        }
        
        // For a video call scenario, set the channel profile as communication.
        option.channelProfile = .communication
        
        // Join the channel with a temp token. Pass in your token and channel name here
        let result = agoraEngine.joinChannel(
            byToken: token, channelId: channelName, uid: uid, mediaOptions: option,
            joinSuccess: { (channel, uid, elapsed) in }
        )
        // Check if joining the channel was successful and set joined Bool accordingly
        if (result == 0) {
            joined = true
        }
    }
    
   
    
    func setupLocalVideo() {
        // Enable the video module
        agoraEngine.enableVideo()
        // Start the local video preview
        agoraEngine.startPreview()
        let videoCanvas = AgoraRtcVideoCanvas()
        videoCanvas.uid = 0
        videoCanvas.renderMode = .hidden
        videoCanvas.view = localView
        
        // Set the local video view
        agoraEngine.setupLocalVideo(videoCanvas)
    }
    
    @objc func leaveChannelUser() {
        agoraEngine.stopPreview()
        let result = agoraEngine.leaveChannel(nil)
        // Check if leaving the channel was successful and set joined Bool accordingly
        if (result == 0) { joined = false }
        btnLeave.removeFromSuperview()
        remoteView.removeFromSuperview()
        localView.removeFromSuperview()
    }
    
}


extension CapacitorPluginAgoraPlugin: AgoraRtcEngineDelegate {
    public func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        let jsObject: [String: Any] = [
            EVENT: "onUserJoined",
            UID: uid,
            ELAPSED: elapsed
        ]
        
        print("onUserJoined:- \(jsObject)")
        
        notifyListeners("onEventReceived", data: jsObject)
        
        let videoCanvas = AgoraRtcVideoCanvas()
        videoCanvas.uid = uid
        videoCanvas.renderMode = .hidden
        videoCanvas.view = remoteView
        agoraEngine.setupRemoteVideo(videoCanvas)
    }
    
    public func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int) {
        let jsObject: [String: Any] = [
            EVENT: "onJoinChannelSuccess",
            CHANNEL: channel,
            UID: uid,
            ELAPSED: elapsed
        ]
        print("onJoinChannelSuccess:- \(jsObject)")
        notifyListeners("onEventReceived", data: jsObject)
    }
    
    
    public func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        let jsObject: [String: Any] = [
            EVENT: "onUserOffline",
            UID: uid,
            REASON: reason
        ]
        print("onUserOffline:- \(jsObject)")
        notifyListeners("onEventReceived", data: jsObject)
    }
    
    public func rtcEngine(_ engine: AgoraRtcEngineKit, didLeaveChannelWith stats: AgoraChannelStats) {
        let jsObject: [String: Any] = [
            EVENT: "onLeaveChannel",
            UID: stats.userCount
        ]
        print("onLeaveChannel:- \(jsObject)")
        notifyListeners("onEventReceived", data: jsObject)
    }
}


class Constant {
    static let APPID = "appId"
    static let TOKEN = "token"
    static let UID = "uid"
    static let CHANNELNAME = "room"
}
