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
        
        let roleStr = call.getString("role") ?? ""
        let role: ClientRole = ClientRole(rawValue: roleStr) ?? .host
        if let jsUser: JSObject = call.getObject("user") {
            let participant = IParticipant(object: jsUser)
        }
        
        
        //        initializeAgoraEngine(appId: appId)
        initViews(params, role: role)
        //        joinChannel(channelName: channelName, uid: UInt(uid), token: token)
//                call.resolve([
//                    "value": implementation.echo("Join channel value")
//                ])
//
//
        
    }
    
    @objc func leaveChannel(_ call: CAPPluginCall) {
        
        debugPrint("[capacitor-agora] leaveChannel")
        call.resolve([
            "value": implementation.echo("leaveChannel value")
        ])
    }
    
    @objc func setCountdown(_ call: CAPPluginCall) {
        debugPrint("[capacitor-agora] setCountdown")

        let seconds = call.getInt("seconds") ?? 0
        wellCareVC?.startCallTimer(seconds: seconds)
    }
    
    @objc func showRecordingStatus(_ call: CAPPluginCall) {
        debugPrint("[capacitor-agora] showRecordingStatus")
        let isShown = call.getBool("isShown") ?? false
        wellCareVC?.showRecordingStatus(isShown: isShown)
    }
    
    @objc func updateParticipantLists(_ call: CAPPluginCall) {
        guard let values = call.getArray("participants") as? [JSObject] else {
            return
        }
        let participants = values.compactMap({IParticipant(object: $0)})
        wellCareVC?.updateParticipantLists(participants: participants)
    }
    
    @objc func enterPictureInPictureMode() {
        wellCareVC?.enterPictureInPictureMode()
    }
    
    //MARK: Sub Functions
    func initViews(_ params: VideoCallParams, role: ClientRole) {
        DispatchQueue.main.async {
//            let currentWindow: UIWindow? = UIApplication.shared.windows.first

            let topMost = UIApplication.getTopViewController()
            let vc = WellCareViewController(role: role, params: params, delegate: self)
            vc.modalPresentationStyle = .fullScreen
            self.wellCareVC = vc
            topMost?.present(vc, animated: true)
//            vc.view.frame = UIScreen.main.bounds
//            currentWindow?.addSubview(vc.view)
        }
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


extension CapacitorPluginAgoraPlugin: AgoraVideoViewerDelegate {
    
    public func remoteStreamJoined(uid: UInt) {
        let jsObject: [String: Any] = [
            EVENT: "join",
            UID: uid
            
        ]
        print("hai leftChannel:- \(jsObject)")
        notifyListeners("onRemoteStreamChanged", data: jsObject)
    }
    
    public func remoteStreamLeaved(uid: UInt) {
        let jsObject: [String: Any] = [
            EVENT: "leave",
            UID: uid
            
        ]
        print("hai leftChannel:- \(jsObject)")
        notifyListeners("onRemoteStreamChanged", data: jsObject)
    }
    
   
    public func leftChannel(_ channel: String) {
       
        wellCareVC?.dismiss(animated: true) { [weak self] in
            self?.wellCareVC = nil
        }
        
        let jsObject: [String: Any] = [
            EVENT: "leaved"
        ]
        
        print("hai leftChannel:- \(jsObject)")
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func joinedChannel(channel: String) {
        debugPrint("[capacitor-agora] joinedChannel \(channel)")
    }
    
    public func onEnterPIP() {
        let jsObject: [String: Any] = [
            EVENT: "chat"
        ]
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func onLeavePIP() {
        let jsObject: [String: Any] = [
            EVENT: "exit_pip"
        ]
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func onSendAction(action: IParticipantAction, to participant: IParticipant) {
        
        debugPrint("[capacitor-agora] onSendAction \(action.rawValue) to particiant \(participant.name)")
        
        
        let jsObject: [String: Any] = [
            EVENT: action.event
        ]
        notifyListeners("onParticipantAction", data: jsObject)
    }
    
    public func onTappedbutton(button: AgoraControlButton) {
        wellCareVC?.onTappedbutton(button: button)
    }

    public func didChangedActiveSpeaker() {
        wellCareVC?.didChangedActiveSpeaker()
    }
    
    public func didChangeVideoConfig() {
        wellCareVC?.didChangeVideoConfig()
    }
}

struct VideoCallParams {
    let channelName: String
    let uid: Int
    let token: String
    let appID: String
}
public enum AgoraControlButton: Int, Codable {
    case flip = 0, camera, call, mic, chat
}

public enum IParticipantAction: Int, Codable {
    case call = 0, nudge, mute, unmute, enableCamera, disableCamera
    
    var event: String {
        switch self {
        case .call:
            return "call"
        case .nudge:
            return "nudge"
        case .mute:
            return "mute"
        case .unmute:
            return "unmute"
        case .enableCamera:
            return "enableCamera"
        case .disableCamera:
            return "disableCamera"
        }
    }
}

public struct IParticipant: Codable {
    let _id: String?
    var name: String
    var avatar: IAvatar
    let role: ClientRole
    let subtitle: String
    var hasJoined: Bool
    let uid: String
    
    init(_id: String?, name: String, avatar: IAvatar, role: ClientRole, subtitle: String, hasJoined: Bool, uid: String) {
        self._id = _id
        self.name = name
        self.avatar = avatar
        self.role = role
        self.subtitle = subtitle
        self.hasJoined = hasJoined
        self.uid = uid
    }
    
    init(object: JSObject) {
        self._id = UUID().uuidString
        self.name = (object["name"] as? String) ?? ""
        self.avatar = IAvatar(url: "")
        self.role =  ClientRole.audience
        self.subtitle = (object["subtitle"] as? String) ?? ""
        self.hasJoined = (object["hasJoined"] as? Bool) ?? false
        self.uid = (object["uid"] as? String) ?? UUID().uuidString
    }
}

enum ClientRole: String, Codable {
    case audience = "audience"
    case host = "host"
}

struct IAvatar: Codable {
    let url: String
}
