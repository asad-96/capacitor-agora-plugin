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
    var participants: [IParticipant] = []
    
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
        
        var user: IParticipant? = nil
        debugPrint("[capacitor-agora] join joinChannel \(appId) ->\(token) ->\(channelName)")

        if let jsUser: JSObject = call.getObject("user") {
            user = IParticipant(object: jsUser)
        }
        
        
        //        initializeAgoraEngine(appId: appId)
//        initViews(params, user: user)
        //        joinChannel(channelName: channelName, uid: UInt(uid), token: token)
//                call.resolve([
//                    "value": implementation.echo("Join channel value")
//                ])
//
//
        
        DispatchQueue.main.async {
            
            let topMost = UIApplication.getTopViewController()
            let vc = WellCareViewController(user: user, params: params, delegate: self)
            vc.modalPresentationStyle = .fullScreen
           
            
            self.wellCareVC = vc
            self.wellCareVC?.joinChannelCallBack = { uid, message in
                if let _msg = message {
                    debugPrint("[capacitor-agora] joinChannelCallBack \(uid) \(_msg)")
                    call.reject(_msg)
                } else {
                    debugPrint("[capacitor-agora] joinChannelCallBack \(uid) \(message)")
                    call.resolve([
                        Constant.UID : uid
                    ])
                }
            }
            topMost?.present(vc, animated: true)
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {[weak self] in
                self?.wellCareVC?.updateParticipantLists(participants: self?.participants ?? [])
            }
        }
        
//        DispatchQueue.main.asyncAfter(deadline: .now() + 15) {[weak self] in
//            self?.wellCareVC?.startCallTimer(seconds: 1000)
//        }
//
//        DispatchQueue.main.asyncAfter(deadline: .now() + 5) {[weak self] in
//            self?.wellCareVC?.showRecordingStatus(isShown: true)
//        }
    }
    
    @objc func leaveChannel(_ call: CAPPluginCall) {
        
        debugPrint("[capacitor-agora] leaveChannel")
        call.resolve([
            "value": implementation.echo("leaveChannel value")
        ])
    }
    
    @objc func setCountdown(_ call: CAPPluginCall) {
        let seconds = call.getInt("seconds") ?? 0
        debugPrint("[capacitor-agora] setCountdown \(seconds)")

        wellCareVC?.startCallTimer(seconds: seconds)
    }
    
    @objc func showRecordingStatus(_ call: CAPPluginCall) {
        debugPrint("[capacitor-agora] showRecordingStatus")
        let isShown = call.getBool("isShown") ?? false
        wellCareVC?.showRecordingStatus(isShown: isShown)
    }
    
    @objc func updateParticipantLists(_ call: CAPPluginCall) {

        let allKeys = call.dictionaryRepresentation.allKeys
        debugPrint("[capacitor-agora] updateParticipantLists allKeys \(allKeys)")

        guard let values = call.getArray("participants") as? [JSObject] else {
            debugPrint("[capacitor-agora] updateParticipantLists error")

            return
        }
        
        let participants = values.compactMap({IParticipant(object: $0)})
        
        debugPrint("[capacitor-agora] updateParticipantLists \(participants.count)")
        
        for participant in participants {
            debugPrint("[capacitor-agora] updateParticipantLists -> \(participant.uid) \(participant.name) -> \(participant.subtitle) ->\(participant.hasJoined)")
        }
        
        self.participants = participants
        if let wellCareVC = wellCareVC {
            wellCareVC.updateParticipantLists(participants: participants)
        }
    }
    
    @objc func enterPictureInPictureMode(_ call: CAPPluginCall) {
        wellCareVC?.enterPictureInPictureMode()
    }
    
    @objc func exitPictureInPictureMode(_ call: CAPPluginCall) {
        wellCareVC?.exitPictureInPictureMode()
    }
}

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

extension CapacitorPluginAgoraPlugin: AgoraVideoViewerDelegate {
    
    public func remoteStreamJoined(uid: UInt) {
        let jsObject: [String: Any] = [
            EVENT: "join",
            UID: uid
            
        ]
        debugPrint("[capacitor-agora] onRemoteStreamChanged \(jsObject)")
        notifyListeners("onRemoteStreamChanged", data: jsObject)
    }
    
    public func remoteStreamLeaved(uid: UInt) {
        let jsObject: [String: Any] = [
            EVENT: "leave",
            UID: uid
            
        ]
        debugPrint("[capacitor-agora] onRemoteStreamChanged \(jsObject)")

        notifyListeners("onRemoteStreamChanged", data: jsObject)
    }
    
   
    public func leftChannel(_ channel: String) {
        participants = []
        wellCareVC?.dismiss(animated: true) { [weak self] in
            self?.wellCareVC = nil
        }
        
        let jsObject: [String: Any] = [
            EVENT: "leaved"
        ]
        
        debugPrint("[capacitor-agora] onSelfAction \(jsObject)")
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func joinedChannel(channel: String) {
        debugPrint("[capacitor-agora] joinedChannel \(channel)")
        let jsObject: [String: Any] = [
            "status": "success"
        ]
        notifyListeners("joinedChannel", data: jsObject)

    }
    
    public func onEnterPIP() {
        debugPrint("[capacitor-agora] onSelfAction onEnterPIP")

        let jsObject: [String: Any] = [
            EVENT: "chat"
        ]
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func onLeavePIP() {
        debugPrint("[capacitor-agora] onSelfAction onLeavePIP")

        let jsObject: [String: Any] = [
            EVENT: "exit_pip"
        ]
        notifyListeners("onSelfAction", data: jsObject)
    }
    
    public func onSendAction(action: IParticipantAction, to participant: IParticipant) {
        debugPrint("[capacitor-agora] onSendAction \(action.rawValue) to particiant \(participant.name)")
        
        
        let jsObject: [String: Any] = [
            EVENT: action.event,
            Constant.UID: participant.uid
        ]
        notifyListeners("onParticipantAction", data: jsObject)
    }
    
    public func onTappedbutton(button: AgoraControlButton) {
        wellCareVC?.onTappedbutton(button: button)
    }

    public func didChangedActiveSpeaker() {
        wellCareVC?.didChangedActiveSpeaker()
    }
    
    public func didChangeVideoConfig(event: String) {
        debugPrint("[capacitor-agora] didChangeVideoConfig \(event) ")

        wellCareVC?.didChangeVideoConfig()
         
        let jsObject: [String: Any] = [: ]
        notifyListeners("event", data: jsObject)
    }
    
    public func tokenDidExpire(_ engine: AgoraRtcEngineKit, uid: UInt) {
        debugPrint("[capacitor-agora] tokenDidExpire ")
        let jsObject: [String: Any] = [
            "code": "-999",
            "msg": "tokenDidExpire",
            "uid": uid
        ]
        notifyListeners("exception", data: jsObject)
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
    var uid: String
    
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
        if let avatarJS: JSObject = object["avatar"] as? JSObject, let avatarURL = avatarJS["url"] as? String{
            self.avatar = IAvatar(url: avatarURL)
        } else {
            self.avatar = IAvatar(url: "")
        }
        
        
        let roleRawValue = (object["role"] as? String) ?? ""
        self.role =  ClientRole(rawValue: roleRawValue) ?? ClientRole.host
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
