//
//  AgoraVideoViewer+TableView.swift
//  WellCareVideoCall
//
//  Created by vulcanlabs-hai on 07/02/2023.
//

import Foundation
import UIKit


extension AgoraVideoViewer {
    func setupBottomUserList() {
        guard let controlContainer = controlContainer else { return }
        let containerH = self.agoraSettings.buttonSize + 40 + 20
        controlContainer.addSubview(userListTableView)
        userListTableView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            userListTableView.topAnchor.constraint(equalTo: controlContainer.topAnchor, constant: containerH),
            userListTableView.leftAnchor.constraint(equalTo: controlContainer.leftAnchor),
            userListTableView.bottomAnchor.constraint(equalTo: controlContainer.bottomAnchor, constant: (hasTopNorth ? -20 : -5)),
            userListTableView.rightAnchor.constraint(equalTo: controlContainer.rightAnchor),
        ])
    }
    
    func updateParticipantLists(participants: [IParticipant]) {
        
        for participant in participants {
            if let index = self.participants.firstIndex(where: {$0.uid == participant.uid}){
                self.participants[index].name = participant.name
                self.participants[index].avatar = participant.avatar
            } else {
                self.participants.append(participant)
            }
            
            if let uid = UInt(participant.uid), let videoFeed = self.videoLookup[uid] {
                videoFeed.updateVideoView(with: participant)
            }
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.userListTableView.reloadData()
            self?.updateControlContainerLayout()
        }
    }
    
    func updateControlContainerLayout() {
        let bottomTableHeight = bottomTableHeight

        let containerH = agoraSettings.buttonSize + 40 + 20

        guard let controlContainer = controlContainer else { return }

        controlContainer.frame.size = CGSize(width: controlContainer.frame.width, height: containerH + bottomTableHeight)

    }
}


extension AgoraVideoViewer: UITableViewDelegate {
    
}


extension AgoraVideoViewer: UITableViewDataSource {
    
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 68
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let videoCount = participants.count
        debugPrint("[capacitor-agora] hai video count \(videoCount)")
        return videoCount
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "IParticipantTVC", for: indexPath) as? IParticipantTVC else {
            
            return UITableViewCell()
        }
        
        let participant = participants[indexPath.row]
        cell.updateContent(with: participant)

        cell.actionHandler = {[weak self] action in
            self?.delegate?.onSendAction(action: action, to: participant)
        }
        return cell
    }
}


class IParticipantTVC: UITableViewCell {
    private lazy var thumbImageView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "ic-avatar-default"))
        imageView.contentMode = .scaleAspectFill
        imageView.layer.cornerRadius = 16
        imageView.layer.borderWidth = 4
        imageView.clipsToBounds = true
        return imageView
    }()
    
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.text = "Floyd Warren"
        label.textColor = .white
        label.font = .systemFont(ofSize: 18, weight: .medium)
        
        return label
    }()
    
    private lazy var roleLabel: UILabel = {
        let label = UILabel()
        label.text = "Father"
        label.textColor = .white
        label.font = .systemFont(ofSize: 14, weight: .light)
        return label
    }()
    
    private lazy var callButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "ic-call-green"), for: .normal)
        button.imageView?.contentMode = .scaleAspectFit
        button.addTarget(self, action: #selector(tappedCallButton(_:)), for: .touchUpInside)
        return button
    }()
    
    private lazy var nudgeButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "ic-nudge-green"), for: .normal)
        button.imageView?.contentMode = .scaleAspectFit
        button.addTarget(self, action: #selector(tappedNudgeButton(_:)), for: .touchUpInside)
        return button
    }()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUIs()
        selectionStyle = .none
    }
    
    var actionHandler: ((IParticipantAction) ->())?
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupUIs() {
        backgroundColor = .clear
        contentView.backgroundColor = .clear
        
        contentView.addSubview(thumbImageView)
        contentView.addSubview(nameLabel)
        contentView.addSubview(roleLabel)
        contentView.addSubview(callButton)
        contentView.addSubview(nudgeButton)

        thumbImageView.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        roleLabel.translatesAutoresizingMaskIntoConstraints = false
        callButton.translatesAutoresizingMaskIntoConstraints = false
        nudgeButton.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            thumbImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            thumbImageView.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 16),
            thumbImageView.widthAnchor.constraint(equalToConstant: 48),
            thumbImageView.heightAnchor.constraint(equalToConstant: 48),
            
            nameLabel.topAnchor.constraint(equalTo: thumbImageView.topAnchor, constant: 5),
            nameLabel.leftAnchor.constraint(equalTo: thumbImageView.rightAnchor, constant: 8),
            
            roleLabel.bottomAnchor.constraint(equalTo: thumbImageView.bottomAnchor, constant: -5),
            roleLabel.leftAnchor.constraint(equalTo: thumbImageView.rightAnchor, constant: 8),

            
            nudgeButton.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            nudgeButton.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -16),
            nudgeButton.heightAnchor.constraint(equalToConstant: 24),
            nudgeButton.widthAnchor.constraint(equalToConstant: 86),


            callButton.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            callButton.rightAnchor.constraint(equalTo: nudgeButton.leftAnchor, constant: -7),
            callButton.heightAnchor.constraint(equalToConstant: 24),
            callButton.widthAnchor.constraint(equalToConstant: 30),
        ])
    }
    
    func updateContent(with participant: IParticipant) {
        callButton.isHidden = participant.hasJoined
        nudgeButton.isHidden = participant.hasJoined
        if !participant.name.isEmpty {
            nameLabel.text = participant.name
        } else {
            nameLabel.text = "N/A"
        }
        
        roleLabel.text = participant.subtitle
        
        thumbImageView.layer.borderColor = participant.role == .host ? UIColor(named: "colorFF5555")?.cgColor : UIColor(named: "color3BC638")?.cgColor
        if let url = URL(string: participant.avatar.url) {
            thumbImageView.downloaded(from: url)
        }
    }
    
    @objc func tappedCallButton(_ sender: UIButton) {
        actionHandler?(.call)
    }
    
    @objc func tappedNudgeButton(_ sender: UIButton) {
        actionHandler?(.nudge)
    }
}
