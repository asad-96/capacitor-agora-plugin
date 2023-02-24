//
//  ViewController.swift
//  WellCareVideoCall
//
//  Created by vulcanlabs-hai on 06/02/2023.
//

import UIKit
import AgoraRtcKit
import MediaPlayer

class WellCareViewController: UIViewController {
    static let ReminderTimeToEndCall: Float = 60
    var agoraEngine: AgoraRtcEngineKit!
    // By default, set the current user role to broadcaster to both send and receive streams.
    var userRole: AgoraClientRole = .broadcaster
    
    // Update with the App ID of your project generated on Agora Console.
    var appID: String{
        return params.appID
    }
    // Update with the temporary token generated in Agora Console.
    var token: String {
        return params.token
    }
    // Update with the channel name you used to generate the token in Agora Console.
    var channelName: String {
        return params.channelName
    }
    
    // Create the view object.
    var agoraView: AgoraVideoViewer!
    
    private lazy var topControlerView: UIView = {
        let view = UIView()
        view.backgroundColor = .clear
        return view
    }()
    
    private lazy var buttonStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 15
        stackView.distribution = .equalCentering
        return stackView
    }()
    
    private lazy var layoutButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "ic-layout-outline"), for: .normal)
        button.addTarget(self, action: #selector(tappedLayoutButton(_:)), for: .touchUpInside)
        
        button.layer.cornerRadius = 41.0/2.0
        button.clipsToBounds = true
        button.backgroundColor = UIColor.black.withAlphaComponent(0.1)
        button.setBackgroundColor(color: UIColor.black.withAlphaComponent(0.3), forState: .highlighted)
        
        return button
    }()
    
    private lazy var bluetoothButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "ic-bluetooth-outline"), for: .normal)
        button.addTarget(self, action: #selector(tappedBluetoothButton(_:)), for: .touchUpInside)
        button.layer.cornerRadius = 41.0/2.0
        button.clipsToBounds = true
        button.backgroundColor = UIColor.black.withAlphaComponent(0.1)
        button.setBackgroundColor(color: UIColor.black.withAlphaComponent(0.3), forState: .highlighted)
        
        return button
    }()
    
    private lazy var flashButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "ic-camera-flash-outline"), for: .normal)
        button.addTarget(self, action: #selector(tappedFlashButton(_:)), for: .touchUpInside)
        button.layer.cornerRadius = 41.0/2.0
        button.clipsToBounds = true
        button.backgroundColor = UIColor.black.withAlphaComponent(0.1)
        button.setBackgroundColor(color: UIColor.black.withAlphaComponent(0.3), forState: .highlighted)
        return button
    }()
    
    
    private lazy var backButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "ic-back-outline"), for: .normal)
        button.imageView?.contentMode = .scaleAspectFit
        button.addTarget(self, action: #selector(tappedBackButton(_:)), for: .touchUpInside)
        button.rounded(cornerRadius: 56.0/2.0)
        button.backgroundColor = UIColor.black.withAlphaComponent(0.1)
        button.setBackgroundColor(color: UIColor.black.withAlphaComponent(0.3), forState: .highlighted)
        
        return button
    }()
    
    private lazy var countdownView: UIView = {
        let view  = UIView()
        view.isHidden = false
        return view
    }()
    
    private lazy var reminderView: VideoCallReminderView = {
        let view = VideoCallReminderView()
        view.backgroundColor = UIColor(named: "color745A00")?.withAlphaComponent(0.6)
        view.layer.cornerRadius = 9
        view.clipsToBounds = true
        view.isHidden = true
        return view
    }()
    
    private lazy var recordImageView: UIImageView = {
        let recordImageView = UIImageView(image: UIImage(named: "ic-record"))
        recordImageView.contentMode = .scaleAspectFit
        recordImageView.isHidden = true
        return recordImageView
    }()
    
    private lazy var blurView: UIVisualEffectView = {
        let blurEffect = UIBlurEffect(style: UIBlurEffect.Style.dark)
        let blurEffectView = UIVisualEffectView(effect: blurEffect)
        //        blurEffectView.frame = view.bounds
        //        blurEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        //        view.addSubview(blurEffectView)
        
        return blurEffectView
    }()
    
    private lazy var countDownContainer: UIButton = {
        let button = UIButton()
        button.layer.cornerRadius = 5
        button.layer.borderWidth = 1
        button.layer.borderColor = UIColor(named: "colorFFE600")?.cgColor
        button.clipsToBounds = true
        button.isUserInteractionEnabled = false
        button.titleLabel?.font = .systemFont(ofSize: 12, weight: .semibold)
        button.setTitleColor(.white, for: .normal)
        button.setImage(UIImage(named: "ic-clock"), for: .normal)
        button.setTitle("00:00", for: .normal)
        button.imageView?.contentMode = .scaleAspectFit
        button.isHidden = true
        button.contentEdgeInsets = UIEdgeInsets(top: 0, left: 2, bottom: 0, right: 2)

        return button
    }()
    
    private lazy var pipControlView: PIPControlView = {
        let view = PIPControlView()
        view.delegate = self
        return view
    }()
    
    private lazy var pandGesture: UIPanGestureRecognizer = {
        let panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))

        return panGesture
    }()
    
    private lazy var tapGesture: UITapGestureRecognizer = {
        let panGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))

        return panGesture
    }()
    
    private let airplayVolume = MPVolumeView()
    private var callTimer: Timer?
    private var callTime: Float = 0
    private var user: IParticipant?
    private let params: VideoCallParams
    private let delegate: AgoraVideoViewerDelegate?
    var trayOriginalCenter: CGPoint = .zero
    private var isInit: Bool = false
    var joinChannelCallBack: ((UInt, String?)->())?
    
    init(user: IParticipant? = nil,
         params: VideoCallParams,
         delegate:  AgoraVideoViewerDelegate? = nil) {
        self.user = user
        self.params = params
        self.delegate = delegate
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        view.backgroundColor = .black
        initializeAndJoinChannel()
        layoutCountdownView()
        layoutTopControls()
        
        view.addGestureRecognizer(tapGesture)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        guard !isInit else { return }
        addBlurView()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        guard !isInit else { return }
        isInit = true
        agoraView?.reorganiseVideos()
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) { [weak self] in
            self?.removeBlurView()
        }
        
        
    }
    
    func initializeAndJoinChannel(){
        
        agoraView = AgoraVideoViewer(
            connectionData: AgoraConnectionData(
                appId: appID,
                rtcToken: token
            ), delegate: delegate
        )
        agoraView?.user = user
        agoraView?.joinChannelCallBack = joinChannelCallBack
        agoraView.fills(view: self.view)
        agoraView.join(
            channel: channelName,
            with: token,
            as: .broadcaster
        )
        
        agoraView?.scheduleMinimizedBottomView()
    }
    
    func layoutTopControls() {
        view.addSubview(topControlerView)
        topControlerView.addSubview(airplayVolume)
        topControlerView.addSubview(buttonStackView)
        
        topControlerView.translatesAutoresizingMaskIntoConstraints = false
        buttonStackView.translatesAutoresizingMaskIntoConstraints = false
        airplayVolume.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            topControlerView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: hasTopNorth ? 10 : 40),
            topControlerView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -10),
            topControlerView.widthAnchor.constraint(equalToConstant: 41),
            topControlerView.heightAnchor.constraint(equalToConstant: 157),
            
            buttonStackView.topAnchor.constraint(equalTo: topControlerView.topAnchor, constant: 0),
            buttonStackView.leftAnchor.constraint(equalTo: topControlerView.leftAnchor, constant: 0),
            buttonStackView.bottomAnchor.constraint(equalTo: topControlerView.bottomAnchor, constant: 0),
            buttonStackView.rightAnchor.constraint(equalTo: topControlerView.rightAnchor, constant: 0),

        ])
        
        buttonStackView.addArrangedSubview(layoutButton)
        buttonStackView.addArrangedSubview(bluetoothButton)
        buttonStackView.addArrangedSubview(flashButton)
        buttonStackView.arrangedSubviews.forEach { button in
            NSLayoutConstraint.activate([
                button.widthAnchor.constraint(equalToConstant: 41),
                button.heightAnchor.constraint(equalToConstant: 41),
            ])
        }
        
        airplayVolume.showsVolumeSlider = true
        airplayVolume.isHidden = true
        
        NSLayoutConstraint.activate([
            airplayVolume.leftAnchor.constraint(equalTo: topControlerView.leftAnchor),
            airplayVolume.centerYAnchor.constraint(equalTo: topControlerView.centerYAnchor),
            airplayVolume.widthAnchor.constraint(equalToConstant: 20),
            airplayVolume.heightAnchor.constraint(equalToConstant: 20)
        ])
    }
    
    func layoutCountdownView() {
        countdownView.addSubview(recordImageView)
        countdownView.addSubview(countDownContainer)
        
        view.addSubview(countdownView)
        countdownView.frame = CGRect(x: 26, y: hasTopNorth ? 20 : 14, width: 85, height: 18)
        recordImageView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            recordImageView.centerYAnchor.constraint(equalTo: countdownView.centerYAnchor),
            recordImageView.leftAnchor.constraint(equalTo: countdownView.leftAnchor),
            recordImageView.widthAnchor.constraint(equalToConstant: 11),
            recordImageView.heightAnchor.constraint(equalToConstant: 11)

        ])
        
        countDownContainer.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
//            countDownContainer.rightAnchor.constraint(equalTo: countdownView.rightAnchor),
            countDownContainer.topAnchor.constraint(equalTo: countdownView.topAnchor),
            countDownContainer.bottomAnchor.constraint(equalTo: countdownView.bottomAnchor),
            countDownContainer.leftAnchor.constraint(equalTo: countdownView.leftAnchor, constant: 15),
        ])
        
        view.addSubview(backButton)
        backButton.frame = CGRect(x: 12, y: hasTopNorth ? 56 : 46, width: 56, height: 56)
        
        
        //setup reminderView
        view.addSubview(reminderView)
        reminderView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            reminderView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 16),
            reminderView.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            reminderView.heightAnchor.constraint(equalToConstant: 43),
            reminderView.bottomAnchor.constraint(equalTo: agoraView.streamerCollectionView.topAnchor, constant: -20)
        ])
    }
    
    @objc func tappedLayoutButton(_ sender: UIButton) {
        
        debugPrint("[capacitor-agora] hai tappedLayoutButton")
        let segmentedStyle = [
            AgoraVideoViewer.Style.floating,
            AgoraVideoViewer.Style.grid,
            AgoraVideoViewer.Style.strip
        ]
        let videoCount = self.agoraView?.userVideoLookup.count ?? 0
        let style = self.agoraView?.style ?? .floating
        
        var index = segmentedStyle.firstIndex(of: style) ?? 0
        index = (index + 1) % segmentedStyle.count
        
        if videoCount != 2, index == 1 {
            // only show gridlayout for 2 users
            index = (index + 1) % segmentedStyle.count
        }
        
//        if videoCount == 1 {
//            self.agoraView?.style = .pinned
//        } else {
            self.agoraView?.style = segmentedStyle[index]
//        }
        
        let currentStyle = self.agoraView?.style ?? .floating
        
        if currentStyle == .floating {
            buttonStackView.axis = .vertical
            topControlerView.removeFromSuperview()
            topControlerView.translatesAutoresizingMaskIntoConstraints = false
            view.addSubview(topControlerView)
           
            for contraint in  topControlerView.constraints {
                contraint.isActive = false
            }
            
            NSLayoutConstraint.activate([
                topControlerView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: hasTopNorth ? 10 : 40),
                topControlerView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -10),
                topControlerView.widthAnchor.constraint(equalToConstant: 41),
                topControlerView.heightAnchor.constraint(equalToConstant: 157),
                
            ])
            
            topControlerView.frame.size = CGSize(width: 41, height: 157)
            backButton.frame = CGRect(x: 12, y: hasTopNorth ? 56 : 46, width: 56, height: 56)
            backButton.rounded(cornerRadius: 56.0 / 2.0)
            buttonStackView.arrangedSubviews.forEach({$0.removeFromSuperview()})
            buttonStackView.addArrangedSubview(layoutButton)
            buttonStackView.addArrangedSubview(bluetoothButton)
            buttonStackView.addArrangedSubview(flashButton)

        } else {
            buttonStackView.axis = .horizontal

            topControlerView.removeFromSuperview()
            topControlerView.translatesAutoresizingMaskIntoConstraints = false
            view.addSubview(topControlerView)
            for contraint in topControlerView.constraints {
                contraint.isActive = false
            }
            NSLayoutConstraint.activate([
                topControlerView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: hasTopNorth ? 0 : 15),
                topControlerView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -10),
                topControlerView.heightAnchor.constraint(equalToConstant: 41),
                topControlerView.widthAnchor.constraint(equalToConstant: 157)
            ])
            topControlerView.frame.size = CGSize(width: 157, height: 41)
            backButton.frame = CGRect(x: 12 , y: hasTopNorth ? 45 : 40, width: 40, height: 40)
            backButton.rounded(cornerRadius: 40.0 / 2.0)

            buttonStackView.arrangedSubviews.forEach({$0.removeFromSuperview()})
            buttonStackView.addArrangedSubview(flashButton)
            buttonStackView.addArrangedSubview(bluetoothButton)
            buttonStackView.addArrangedSubview(layoutButton)
        }
        
        buttonStackView.removeFromSuperview()
        topControlerView.addSubview(buttonStackView)
        NSLayoutConstraint.activate([
            buttonStackView.topAnchor.constraint(equalTo: topControlerView.topAnchor, constant: 0),
            buttonStackView.leftAnchor.constraint(equalTo: topControlerView.leftAnchor, constant: 0),
            buttonStackView.bottomAnchor.constraint(equalTo: topControlerView.bottomAnchor, constant: 0),
            buttonStackView.rightAnchor.constraint(equalTo: topControlerView.rightAnchor, constant: 0)
        ])
        
        buttonStackView.arrangedSubviews.forEach { button in
            NSLayoutConstraint.activate([
                button.widthAnchor.constraint(equalToConstant: 41),
                button.heightAnchor.constraint(equalToConstant: 41),
            ])
        }
    }
    
    func hideReminderView() {
        reminderView.hide()
    }
    
    func showReminderView() {
        reminderView.show()
    }
    
    func hideCountDownView() {
        countdownView.isHidden = true
    }
    
    func addBlurView() {
        blurView.frame = UIScreen.main.bounds
        view.addSubview(blurView)
    }
    
    func removeBlurView() {
        blurView.removeFromSuperview()
    }
    
    func showAlertView(title: String?, message: String?, completion: (() -> Void)? = nil) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "OK", style: .default) { _ in
            alert.dismiss(animated: true, completion: completion)
        }
        okAction.setValue(UIColor.black, forKey: "titleTextColor")

        let cancelAction = UIAlertAction(title: "CANCEL", style: .default)
        cancelAction.setValue(UIColor.black, forKey: "titleTextColor")

        alert.addAction(cancelAction)
        alert.addAction(okAction)

        present(alert, animated: true)
    }
    
    func showFlashAlert() {
        showAlertView(title: nil, message: "Switch to rear camera\nto use camera torch light?") { [weak self] in
            self?.agoraView?.flipCamera()
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) { [weak self] in
                self?.agoraView.tappedFlashButton()
            }
        }
    }
}


extension WellCareViewController {
    
    @objc func tappedBluetoothButton(_ sender: UIButton) {
        if let routePickerButton = airplayVolume.subviews.first(where: { $0 is UIButton }) as? UIButton {
           
            routePickerButton.sendActions(for: .touchUpInside)
        }
    }
    
    @objc func tappedFlashButton(_ sender: UIButton) {
        let isTorchSupported = agoraView?.isTorchSupported ?? false
        if isTorchSupported {
            self.agoraView.tappedFlashButton()
        } else {
            self.showFlashAlert()
        }
    }
    
    @objc func tappedBackButton(_ sender: UIButton) {
        agoraView?.tappedEndCallButton()
    }
    
    @objc func tappedCloseReminderButton(_ sender: UIButton) {
       hideReminderView()
    }
    
    @objc func startCallTimer(seconds: Int) {
        debugPrint("[capacitor-agora] countDownCallTimer 111")

        guard seconds > 0 else { return }
        if !Thread.isMainThread {
            DispatchQueue.main.async {
                self.startCallTimer(seconds: seconds)
            }
            return
        }
        debugPrint("[capacitor-agora] countDownCallTimer 2222")

            self.countDownContainer.isHidden = false
            self.callTimer?.invalidate()
            self.callTimer = nil
            self.callTime = Float(seconds)
            self.callTimer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(self.countDownCallTimer(_:)), userInfo: nil, repeats: true)
        
       
    }
    
    @objc func stopCallTimer() {
        callTimer?.invalidate()
        callTimer = nil
        countDownContainer.isHidden = true
    }
    
    @objc func countDownCallTimer(_ timer: Timer) {
        callTime -= 1
        debugPrint("[capacitor-agora] countDownCallTimer \(callTime)")
        let mins: Float = callTime / 60
        let secs: Float = callTime.truncatingRemainder(dividingBy: 60)
        
        let minsText: String = mins >= 10 ? "\(Int(mins))" : "0\(Int(mins))"
        let secsText: String = secs >= 10 ? "\(Int(secs))" : "0\(Int(secs))"

        countDownContainer.setTitle(String(format: "%@:%@", minsText, secsText), for: .normal)
        if callTime == WellCareViewController.ReminderTimeToEndCall {
            showReminderView()
        }
        
        if callTime <= 0 {
            stopCallTimer()
            endCallTime()
        }
    }
    
    @objc func endCallTime() {
        self.agoraView?.tappedEndCallButton()
    }
    
    func onLeaveChat() {
        
    }
    
    func enterPictureInPictureMode() {
        if !Thread.isMainThread {
            DispatchQueue.main.async {
                self.enterPictureInPictureMode()
            }
            return
        }
        
        self.agoraView?.isPipOn = true

        let minimizedWidth = 190.0 * UIScreen.main.bounds.width / 384.0
        self.view.frame = CGRect(origin: CGPoint(x: 50, y: 50), size: CGSize(width: minimizedWidth, height: minimizedWidth))
        self.view.layer.cornerRadius = 5
        self.view.clipsToBounds = true

        relayoutAgoraVideoView()
        didChangedActiveSpeaker()
        didChangeVideoConfig()
    }
    
    func exitPictureInPictureMode() {
        self.agoraView?.isPipOn = false
        
        let pip = (self.agoraView?.isPipOn ?? false)
        topControlerView.isHidden = pip
        countdownView.isHidden = pip
        backButton.isHidden = pip
        
        self.view.frame = UIScreen.main.bounds
        self.view.layer.cornerRadius = 0
        pipControlView.removeFromSuperview()
        view.removeGestureRecognizer(pandGesture)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            self?.agoraView?.layoutForPIP()
        }
    }
    
    
    func relayoutAgoraVideoView() {
        let pip = (self.agoraView?.isPipOn ?? false)
        topControlerView.isHidden = pip
        countdownView.isHidden = pip
        backButton.isHidden = pip
        

        view.addSubview(pipControlView)
        pipControlView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            pipControlView.topAnchor.constraint(equalTo: view.topAnchor),
            pipControlView.leftAnchor.constraint(equalTo: view.leftAnchor),
            pipControlView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            pipControlView.rightAnchor.constraint(equalTo: view.rightAnchor),
        ])
        
        view.addGestureRecognizer(pandGesture)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            self?.agoraView?.layoutForPIP()
        }
    }
    
    @objc func handleTap(_ sender: UITapGestureRecognizer) {
        agoraView?.minimizedBottomView()
    }
    
    @objc func handlePan(_ sender: UIPanGestureRecognizer) {
        var translation = sender.translation(in: self.view)
        var velocity = sender.velocity(in: self.view)
        let minimizedWidth = 190.0 * UIScreen.main.bounds.width / 384.0

        if sender.state == .began {
            trayOriginalCenter = self.view.center
            
        } else if sender.state == .changed {
            
            var newOffsetY = trayOriginalCenter.y + translation.y
            var newOffsetX = trayOriginalCenter.x + translation.x

            newOffsetX = max(minimizedWidth/2, newOffsetX)
            newOffsetX = min(UIScreen.main.bounds.width -  minimizedWidth/2, newOffsetX)

            
            newOffsetY = max(minimizedWidth/2, newOffsetY)
            newOffsetY = min(UIScreen.main.bounds.height - minimizedWidth/2, newOffsetY)

            self.view.center = CGPoint(x: newOffsetX, y: newOffsetY)
            
        } else if sender.state == .ended {
            
        }
    }
    
    func updateParticipantLists(participants: [IParticipant]) {
        agoraView?.updateParticipantLists(participants: participants)
    }
    
    func didChangedActiveSpeaker() {
        let isYourSelfSpeaking = agoraView?.isYourSelfSpeaking ?? false
        pipControlView.toggleControlView(isHidden: !isYourSelfSpeaking)
    }
    
    func didChangeVideoConfig() {
        let micEnabled = self.agoraView?.agoraSettings.micEnabled ?? false
        pipControlView.toggleMicButton(isOn: !micEnabled)
    }
    
    func onTappedbutton(button: AgoraControlButton) {
        switch button {
        case .flip:
//            DispatchQueue.main.asyncAfter(deadline: .now() + 1) { [weak self] in
//                let isTorchSupported = self?.agoraView?.isTorchSupported ?? false
//                self?.flashButton.alpha = isTorchSupported ? 1 : 0.5
//            }
            break
        default: break
        }
    }
    
    func showRecordingStatus(isShown: Bool) {
        DispatchQueue.main.async{ [weak self] in
            self?.recordImageView.isHidden = !isShown
        }
    }
}

extension WellCareViewController: PIPControlViewDelegate {
    func didSelectButton(_ action: ButtonAction) {
        switch action {
        case .pip:
            agoraView?.isPipOn = false
            exitPictureInPictureMode()
            delegate?.onLeavePIP()
        case .close:
            agoraView?.tappedEndCallButton()
        case .flip:
            agoraView?.flipCamera()
        case .camera:
            agoraView?.toggleCam(nil)
        case .mic:
            agoraView?.toggleMic(nil)
        }
    }
}

extension UIViewController {
    var hasTopNorth: Bool {
        return UIScreen.main.bounds.height >= 812
    }
}

enum UserPermission: Int {
    case doctor = 0
    case patient
}


extension UIView {
    func rounded(cornerRadius: CGFloat = 6) {
        self.layer.cornerRadius = cornerRadius
        self.clipsToBounds = true
    }
}

extension UIButton {

    func setBackgroundColor(color: UIColor, forState: UIControl.State, size: CGSize = CGSize(width: 1, height: 1)) {

        UIGraphicsBeginImageContext(size)
        UIGraphicsGetCurrentContext()?.setFillColor(color.cgColor)
        UIGraphicsGetCurrentContext()?.fill(CGRect(origin: .zero, size: size))
        let colorImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        self.setBackgroundImage(colorImage, for: forState)
    }

}
