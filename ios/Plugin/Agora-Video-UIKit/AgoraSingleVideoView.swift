//
//  AgoraSingleVideoView.swift
//  Agora-Video-UIKit
//
//  Created by Max Cobb on 25/11/2020.
//

#if os(iOS)
import UIKit
#elseif os(macOS)
import AppKit
#endif
import AgoraRtcKit

/// View for the individual Agora Camera Feed.
public class AgoraSingleVideoView: MPView {

    /// Is the video turned off for this user.
    public var videoMuted: Bool = true {
        didSet {
            if oldValue != videoMuted {
                self.canvas.view?.isHidden = videoMuted
                self.customCameraView?.isHidden = videoMuted
                self.backgroundView.isHidden = !videoMuted
            }
            self.updateUserOptions()
        }
    }
    /// Is the microphone muted for this user.
    public var audioMuted: Bool = true {
        didSet {
            self.mutedFlag.isHidden = !audioMuted
            self.updateUserOptions()
        }
    }

    weak var singleVideoViewDelegate: SingleVideoViewDelegate?

    /// Whether the options label should be visible or not.
    public var showOptions: Bool = true {
        didSet {
            #if canImport(AgoraRtmControl)
            self.userOptions?.isHidden = !self.showOptions
            #endif
        }
    }
    /// Unique ID for this user, used by the video feed.
    var uid: UInt {
        get { self.canvas.uid }
        set { self.canvas.uid = newValue }
    }
    /// Canvas used to render the Agora RTC Video.
    public var canvas: AgoraRtcVideoCanvas {
        didSet {
            debugPrint("[capacitor-agora] hai update canvas ")
        }
    }
    /// View that the AgoraRtcVideoCanvas is sending the video feed to
    var hostingView: MPView? {
        self.canvas.view
    }

    var customCameraView: CustomVideoSourcePreview? {
        didSet {
            if let oldValue = oldValue {
                oldValue.removeFromSuperview()
            }
            if let customCameraView = customCameraView {
                if let defaultCamView = self.canvas.view {
                    #if os(iOS)
                    self.insertSubview(customCameraView, aboveSubview: defaultCamView)
                    #elseif os(macOS)
                    self.addSubview(customCameraView, positioned: .above, relativeTo: defaultCamView)
                    #endif
                }
                customCameraView.frame = self.bounds
                #if os(iOS)
                customCameraView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
                #elseif os(macOS)
                customCameraView.autoresizingMask = [.width, .height]
                #endif
            }
        }
    }

    var micFlagColor: MPColor

    enum UserOptions: String {
        case camera
        case microphone
    }

    #if canImport(AgoraRtmControl)
    lazy var userOptions: MPView? = {
        #if os(iOS)
        let userOptionsBtn = MPButton.newToggleButton(
            unselected: MPButton.ellipsisSymbol
        )
        userOptionsBtn.layer.zPosition = 3
        userOptionsBtn.tintColor = .systemGray
        #elseif os(macOS)
        let userOptionsBtn = NSPopUpButton(frame: .zero, pullsDown: true)

//        userOptionsBtn.wantsLayer = true
//        userOptionsBtn.layer?.backgroundColor = .white
        (userOptionsBtn.cell as? NSButtonCell)?.backgroundColor = .selectedContentBackgroundColor
        self.addItems(to: userOptionsBtn)
        #endif
        self.addSubview(userOptionsBtn)
        #if os(iOS)
        userOptionsBtn.frame = CGRect(
            origin: CGPoint(x: 10, y: 10),
            size: CGSize(width: 40, height: 25)
        )
        userOptionsBtn.autoresizingMask = [.flexibleBottomMargin, .flexibleRightMargin]
        userOptionsBtn.addTarget(self, action: #selector(optionsBtnSelected), for: .touchUpInside)
        #elseif os(macOS)
        userOptionsBtn.isBordered = false
        userOptionsBtn.wantsLayer = true
        userOptionsBtn.layer?.backgroundColor = .clear
        userOptionsBtn.frame = CGRect(
            origin: CGPoint(x: 10, y: self.frame.height - 30),
            size: CGSize(width: 40, height: 25)
        )
        userOptionsBtn.autoresizingMask = [.minYMargin, .maxXMargin]
        userOptionsBtn.target = self
        userOptionsBtn.action = #selector(optionsBtnSelected)
        #endif
//        userOptionsBtn.isHidden = true
        return userOptionsBtn
    }()
    #endif

    /// Icon to show if this user is muting their microphone
    lazy var mutedFlag: MPButton = {
        
        let muteFlag = UIButton()
        muteFlag.setImage(UIImage(named: "ic-mic-mute"), for: .normal)
        muteFlag.imageView?.contentMode = .scaleAspectFit
        self.addSubview(muteFlag)
        
        muteFlag.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            muteFlag.rightAnchor.constraint(equalTo: self.rightAnchor, constant: -35),
            muteFlag.topAnchor.constraint(equalTo: self.topAnchor, constant: 5),
            muteFlag.widthAnchor.constraint(equalToConstant: 20),
            muteFlag.heightAnchor.constraint(equalToConstant: 20)
        ])
        
        muteFlag.backgroundColor = UIColor(named: "colorF8000F")?.withAlphaComponent(0.5)
        muteFlag.layer.cornerRadius = 20.0/2.0
        muteFlag.clipsToBounds = true
        return muteFlag
    }()

    lazy var signalView: MPImageView = {
        let imgView = UIImageView(image: UIImage(named: "ic-signal-good"))
        imgView.contentMode = .scaleAspectFit
        
        return imgView
    }()
    
    var avatarWConstraint: NSLayoutConstraint?
    var avatarHConstraint: NSLayoutConstraint?

    /// Create a new AgoraSingleVideoView to be displayed in your app
    /// - Parameters:
    ///   - uid: User ID of the `AgoraRtcVideoCanvas` inside this view
    ///   - micColor: Color to be applied when the local or remote user mutes their microphone
    ///   - delegate: Object used for accessing the AgoraRtmController and presenting alerts
    public init(
        uid: UInt, micColor: MPColor, delegate: SingleVideoViewDelegate? = nil
    ) {
        self.canvas = AgoraRtcVideoCanvas()
        self.micFlagColor = micColor
        self.singleVideoViewDelegate = delegate
        super.init(frame: .zero)
        self.backgroundView.isHidden = false
        self.canvas.uid = uid
        let hostingView = MPView()
        hostingView.frame = self.bounds
        #if os(iOS)
        hostingView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        #elseif os(macOS)
        hostingView.autoresizingMask = [.width, .height]
        #endif
        self.canvas.view = hostingView
        self.addSubview(hostingView)
        self.setupMutedFlag()
        self.setupOptions(visible: false)
        self.signalView.isHidden = false
        self.layoutSignalView()
    }

    func setupOptions(visible showOptions: Bool) {
        self.showOptions = showOptions
    }

    private func setupMutedFlag() {
        self.audioMuted = true
    }

    private lazy var backgroundView: MPView = {
        let backgroundView = MPView()
        backgroundView.backgroundColor = UIColor.black
        self.addSubview(backgroundView)
        backgroundView.frame = self.bounds
        backgroundView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        backgroundView.addSubview(stackView)
        stackView.addArrangedSubview(avatarImageView)
        stackView.addArrangedSubview(nameLabel)
        avatarImageView.addSubview(shortNameLabel)
        stackView.translatesAutoresizingMaskIntoConstraints = false
        avatarImageView.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        shortNameLabel.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            stackView.centerYAnchor.constraint(equalTo: backgroundView.centerYAnchor, constant: 0),
            stackView.centerXAnchor.constraint(equalTo: backgroundView.centerXAnchor),
            stackView.widthAnchor.constraint(equalTo: backgroundView.widthAnchor, multiplier: 0.8),
            
            shortNameLabel.centerYAnchor.constraint(equalTo: avatarImageView.centerYAnchor),
            shortNameLabel.centerXAnchor.constraint(equalTo: avatarImageView.centerXAnchor),
            shortNameLabel.widthAnchor.constraint(equalTo: avatarImageView.widthAnchor, multiplier: 0.7),
            shortNameLabel.heightAnchor.constraint(equalTo: avatarImageView.heightAnchor, multiplier: 0.7),
            
        ])
        
        
        avatarWConstraint =  avatarImageView.widthAnchor.constraint(equalToConstant: 50)
        avatarHConstraint = avatarImageView.heightAnchor.constraint(equalToConstant: 50)

        avatarWConstraint?.isActive = true
        avatarHConstraint?.isActive = true
        
        return backgroundView
    }()

    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.textColor = .white
        label.font = .systemFont(ofSize: 18, weight: .bold)
        label.textAlignment = .center
        label.text = ""
        label.adjustsFontSizeToFitWidth = true
        label.minimumScaleFactor = 0.5

        return label
    }()
    
    private lazy var avatarImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.backgroundColor = UIColor(named: "color009D23")
        imageView.layer.cornerRadius = 25
        imageView.layer.borderWidth = 1
        imageView.layer.borderColor = UIColor.white.cgColor
        imageView.clipsToBounds = true
        imageView.contentMode = .scaleAspectFill
        return imageView
    }()
    
    private lazy var shortNameLabel: UILabel = {
        let label = UILabel()
        label.textColor = .white
        label.font = .systemFont(ofSize: 26, weight: .bold)
        label.textAlignment = .center
        label.text = ""
        label.adjustsFontSizeToFitWidth = true
        label.minimumScaleFactor = 0.5
        return label
    }()
    
    private lazy var stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 5
        stackView.alignment = .center
        return stackView
    }()
    
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func placeMuteAtBottom(style: AgoraVideoViewer.Style = .pinned) {
        mutedFlag.removeFromSuperview()
        addSubview(mutedFlag)
        for ct in mutedFlag.constraints {
            ct.isActive = false
        }
        
        let ratio = style == .pinned ? 19.0/78.0 : 37.0 / 150.0
        let muteWidth = min(37, self.frame.width * ratio)
        
        NSLayoutConstraint.activate([
            mutedFlag.rightAnchor.constraint(equalTo: self.rightAnchor, constant: style == .pinned ? -5 : -10),
            mutedFlag.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: style == .pinned ? -5 : -10),
            mutedFlag.widthAnchor.constraint(equalToConstant: muteWidth),
            mutedFlag.heightAnchor.constraint(equalToConstant: muteWidth)
        ])
        
        let imagePadding = muteWidth / 5
        mutedFlag.imageEdgeInsets = UIEdgeInsets(top: imagePadding, left: imagePadding, bottom: imagePadding, right: imagePadding)
        mutedFlag.layer.cornerRadius = muteWidth/2.0
        mutedFlag.clipsToBounds = true
        
        let avatarSize: CGFloat = style == .pinned ? 30 : 50
        avatarHConstraint?.constant = avatarSize
        avatarWConstraint?.constant = avatarSize
        avatarImageView.layer.cornerRadius = avatarSize / 2
        layoutSignalView()
    }
    
    func placeMuteAtTop(style: AgoraVideoViewer.Style, top: CGFloat = 5) {
        mutedFlag.removeFromSuperview()
        addSubview(mutedFlag)
        for ct in mutedFlag.constraints {
            ct.isActive = false
        }
        
        let muteFlagSize: CGFloat = 16.0
        let muteRightPadding: CGFloat = style == .pinned ? -35 : -25
        NSLayoutConstraint.activate([
            mutedFlag.rightAnchor.constraint(equalTo: self.rightAnchor, constant: muteRightPadding),
            mutedFlag.topAnchor.constraint(equalTo: self.topAnchor, constant: top),
            mutedFlag.widthAnchor.constraint(equalToConstant: muteFlagSize),
            mutedFlag.heightAnchor.constraint(equalToConstant: muteFlagSize)
        ])
        
        mutedFlag.imageEdgeInsets = UIEdgeInsets(top: 3, left: 3, bottom: 3, right: 3)
        mutedFlag.layer.cornerRadius = muteFlagSize/2.0
        mutedFlag.clipsToBounds = true
        
        let avatarSize: CGFloat = style == .pinned ? 30 : 50
        avatarHConstraint?.constant = avatarSize
        avatarWConstraint?.constant = avatarSize
        avatarImageView.layer.cornerRadius = avatarSize / 2
        layoutSignalView()

    }
    
    func layoutOptionViewForFullVideo(style: AgoraVideoViewer.Style) {
        let margin: CGFloat = style == .pinned ? 15 : 5
        placeMuteAtTop(style: style, top: margin)
        layoutSignalView(top: margin, right: margin)
    }
    
    func layoutSignalView(top: CGFloat = 5,  right: CGFloat = 5) {
        signalView.removeFromSuperview()
        addSubview(signalView)
        signalView.translatesAutoresizingMaskIntoConstraints = false
        for ct in signalView.constraints {
            ct.isActive = false
        }
        
        NSLayoutConstraint.activate([
            signalView.rightAnchor.constraint(equalTo: self.rightAnchor, constant: -right),
            signalView.topAnchor.constraint(equalTo: self.topAnchor, constant: top),
            signalView.widthAnchor.constraint(equalToConstant: 14),
            signalView.heightAnchor.constraint(equalTo: signalView.widthAnchor, multiplier:1)
        ])
        signalView.backgroundColor = .clear
    }
    
    func updateVideoView(with paticipant: IParticipant) {
        if paticipant.name.isEmpty {
            nameLabel.text = "N/a"
        } else {
            nameLabel.text = paticipant.name
        }
        if let url = URL(string: paticipant.avatar.url) {
            avatarImageView.downloaded(from: url)
            shortNameLabel.isHidden = true
        } else {
            shortNameLabel.isHidden = false
        }
        
        if paticipant.name.count > 1 {
            let str = "\(Array(paticipant.name)[0])"
            if !str.isEmpty {
                shortNameLabel.text = "\(str)"
            } else {
                shortNameLabel.text = "N/a"
            }
        }
    }
}
