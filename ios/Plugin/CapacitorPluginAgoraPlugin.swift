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
    
    // Update with the App ID of your project generated on Agora Console.
    let appID = "6efdd253f1c74743bd343fc97c136d6c"
    
    // Update with the temporary token generated in Agora Console.
    var token = "007eJxTYPBTMkx1ffTD4c+ptVNbij2vlNZsyPU4K6Ug2Bet1tm5XE6BwSw1LSXFyNQ4zTDZ3MTcxDgpxdjEOC3Z0jzZ0NgsxSz57eHtyQ2BjAz5y1IYGRkgEMRnZShJLS4xZGAAAGAEH/E="
    
    // Update with the channel name you used to generate the token in Agora Console.
    var channelName = "test1"
    
    // The video feed for the local user is displayed here
    var localView = UIView()
    // The video feed for the remote user is displayed here
    var remoteView = UIView()
    
    var btnLeave = UIButton()
    
    var joined: Bool = false


    
    //MARK: Methods
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
        
    @objc func joinChannel(_ call: CAPPluginCall) {
        initializeAgoraEngine()
        initViews()
        joinChannel()
        call.resolve([
            "value": implementation.echo("Join channel value")
        ])
    }
    
    @objc func leaveChannel(_ call: CAPPluginCall) {
         call.resolve([
            "value": implementation.echo("leaveChannel value")
        ])
        let currentWindow: UIWindow? = UIApplication.shared.windows.first        
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
    
    func initializeAgoraEngine() {
        let config = AgoraRtcEngineConfig()
        // Pass in your App ID here.
        config.appId = appID
        // Use AgoraRtcEngineDelegate for the following delegate parameter.
        agoraEngine = AgoraRtcEngineKit.sharedEngine(with: config, delegate: self)

    }
    
    func joinChannel() {
        if !self.checkForPermissions() {
            showMessage(title: "Error", text: "Permissions were not granted")
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
            byToken: token, channelId: channelName, uid: 0, mediaOptions: option,
            joinSuccess: { (channel, uid, elapsed) in }
        )
            // Check if joining the channel was successful and set joined Bool accordingly
        if (result == 0) {
            joined = true
            showMessage(title: "Success", text: "Successfully joined the channel as \(self.userRole)")
        }
    }
    
    func showMessage(title: String, text: String, delay: Int = 2) -> Void {
//        let alert = UIAlertController(title: title, message: text, preferredStyle: .alert)
//        UIApplication.shared.windows.first?.rootViewController?.present(alert, animated: true)
//        let deadlineTime = DispatchTime.now() + .seconds(delay)
//        DispatchQueue.main.asyncAfter(deadline: deadlineTime, execute: {
//            alert.dismiss(animated: true, completion: nil)
//        })
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
        let videoCanvas = AgoraRtcVideoCanvas()
        videoCanvas.uid = uid
        videoCanvas.renderMode = .hidden
        videoCanvas.view = remoteView
        agoraEngine.setupRemoteVideo(videoCanvas)
    }
}
