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
    
    private var EVENT = "event";
    private var UID = "uid";
    private var ELAPSED = "elapsed";
    private var CHANNEL = "channel";
    private var REASON = "reason";
    private let ROOM = "room"
    
    var wellCareVC: WellCareViewController?
    
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
        let params = VideoCallParams(channelName: channelName, uid: uid, token: token, appID: appId)
        
        //        initializeAgoraEngine(appId: appId)
        initViews(params)
        //        joinChannel(channelName: channelName, uid: UInt(uid), token: token)
        //        call.resolve([
        //            "value": implementation.echo("Join channel value")
        //        ])
        
        
        
        
    }
    
    @objc func leaveChannel(_ call: CAPPluginCall) {
        
        debugPrint("hai leaveChannel")
        call.resolve([
            "value": implementation.echo("leaveChannel value")
        ])
    }
    
    //MARK: Sub Functions
    func initViews(_ params: VideoCallParams) {
        DispatchQueue.main.async {
            //            self.remoteView.frame = UIScreen.main.bounds
            //            self.localView.frame =  CGRect(x: UIScreen.main.bounds.width - 90, y: 0, width: 120, height: 160)
            let currentWindow: UIWindow? = UIApplication.shared.windows.first
            //            self.remoteView.backgroundColor = .white
            //            self.localView.backgroundColor = .white
            //            currentWindow?.addSubview(self.remoteView)
            //            currentWindow?.addSubview(self.localView)
            //
            //            self.btnLeave.frame = CGRect(x: UIScreen.main.bounds.width - (UIScreen.main.bounds.width / 1.5), y: UIScreen.main.bounds.height - 120, width: 120, height: 60)
            //            self.btnLeave.setTitle("Leave", for: .normal)
            //            self.btnLeave.backgroundColor = UIColor.link
            //            self.btnLeave.layer.cornerRadius = self.btnLeave.frame.height / 2
            //            self.btnLeave.addTarget(self, action: #selector(self.leaveChannelUser), for: .touchUpInside)
            //            currentWindow?.addSubview(self.btnLeave)
            
            let topMost = UIApplication.getTopViewController()
            let vc = WellCareViewController(userPermissin: .doctor, params: params, delegate: self)
            vc.modalPresentationStyle = .fullScreen
            //            currentWindow?.addSubview(vc.view)
            self.wellCareVC = vc
            topMost?.present(vc, animated: true)
        }
    }
    
    
    
    func updateParticipantLists(participants: [IParticipant]) {
        
    }
}

//
//extension CapacitorPluginAgoraPlugin: AgoraRtcEngineDelegate {
//    public func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
//        let jsObject: [String: Any] = [
//            EVENT: "onUserJoined",
//            UID: uid,
//            ELAPSED: elapsed
//        ]
//
//        print("onUserJoined:- \(jsObject)")
//
//        notifyListeners("onEventReceived", data: jsObject)
//
//        let videoCanvas = AgoraRtcVideoCanvas()
//        videoCanvas.uid = uid
//        videoCanvas.renderMode = .hidden
//        videoCanvas.view = remoteView
//        agoraEngine.setupRemoteVideo(videoCanvas)
//    }
//
//    public func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int) {
//        let jsObject: [String: Any] = [
//            EVENT: "onJoinChannelSuccess",
//            CHANNEL: channel,
//            UID: uid,
//            ELAPSED: elapsed
//        ]
//        print("onJoinChannelSuccess:- \(jsObject)")
//        notifyListeners("onEventReceived", data: jsObject)
//    }
//
//
//    public func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
//        let jsObject: [String: Any] = [
//            EVENT: "onUserOffline",
//            UID: uid,
//            REASON: reason
//        ]
//        print("onUserOffline:- \(jsObject)")
//        notifyListeners("onEventReceived", data: jsObject)
//    }
//
//    public func rtcEngine(_ engine: AgoraRtcEngineKit, didLeaveChannelWith stats: AgoraChannelStats) {
//        let jsObject: [String: Any] = [
//            EVENT: "onLeaveChannel",
//            UID: stats.userCount
//        ]
//        print("onLeaveChannel:- \(jsObject)")
//        notifyListeners("onEventReceived", data: jsObject)
//    }
//}


class Constant {
    static let APPID = "appId"
    static let TOKEN = "token"
    static let UID = "uid"
    static let CHANNELNAME = "room"
}


extension UIApplication {
    
    class func getTopViewController(base: UIViewController? = UIApplication.shared.keyWindow?.rootViewController) -> UIViewController? {
        
        if let nav = base as? UINavigationController {
            return getTopViewController(base: nav.visibleViewController)
            
        } else if let tab = base as? UITabBarController, let selected = tab.selectedViewController {
            return getTopViewController(base: selected)
            
        } else if let presented = base?.presentedViewController {
            return getTopViewController(base: presented)
        }
        return base
    }
}

//extension CapacitorPluginAgoraPlugin {
//
//}


extension CapacitorPluginAgoraPlugin: AgoraVideoViewerDelegate{
   
    public func leftChannel(_ channel: String) {
        let jsObject: [String: Any] = [
            EVENT: "leaved"
        ]
        
        wellCareVC?.dismiss(animated: true) { [weak self] in
            self?.wellCareVC = nil
        }
        
        print("hai leftChannel:- \(jsObject)")
        notifyListeners("onLeaved", data: jsObject)
    }
    
    public func joinedChannel(channel: String) {
        debugPrint("hai joinedChannel \(channel)")
    }
    
    public func onEnterChat() {
        wellCareVC?.onEnterChat()
        
        let jsObject: [String: Any] = [
            EVENT: "chat"        ]
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func onLeaveChat() {
        let jsObject: [String: Any] = [
            EVENT: "leave"
        ]
        notifyListeners("onSelfAction", data: jsObject)
    }
}


struct VideoCallParams {
    let channelName: String
    let  uid: Int
    let token: String
    let appID: String
}

struct IParticipant {
    
}
