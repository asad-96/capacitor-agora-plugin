//
//  VideoCallReminderView.swift
//  WellcareCapacitorPluginAgora
//
//  Created by vulcanlabs-hai on 15/02/2023.
//

import Foundation
import UIKit

class VideoCallReminderView: UIView {
    private lazy var okLabel: UILabel = {
        let label = UILabel()
        label.text = "OK"
        label.font = .systemFont(ofSize: 12, weight: .medium)
        label.textColor = .white
        label.textAlignment = .center
        label.backgroundColor = UIColor.black.withAlphaComponent(0.2)
        label.layer.cornerRadius = 8
        label.clipsToBounds = true
        return label
    }()
    
    
    private lazy var messageLabel: UILabel = {
        let label = UILabel()
        label.text = "1 minutes remain. Please wrap up your calls."
        label.font = .systemFont(ofSize: 9, weight: .medium)
        label.textColor = .white
        return label
    }()
    
    private lazy var okButton: UIButton = {
        let button = UIButton()
        button.addTarget(self, action: #selector(tappedOkButton(_:)), for: .touchUpInside)
        return button
    }()
    
    private lazy var alarmImageView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "ic-alarm-big"))
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()
    
    init() {
        super.init(frame: .zero)
        setupUIs()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupUIs() {
        addSubview(alarmImageView)
        addSubview(messageLabel)
        addSubview(okButton)
        okButton.addSubview(okLabel)
        alarmImageView.translatesAutoresizingMaskIntoConstraints = false
        messageLabel.translatesAutoresizingMaskIntoConstraints = false
        okButton.translatesAutoresizingMaskIntoConstraints = false
        okLabel.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            alarmImageView.widthAnchor.constraint(equalToConstant: 23),
            alarmImageView.heightAnchor.constraint(equalToConstant: 23),
            alarmImageView.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            alarmImageView.leftAnchor.constraint(equalTo: self.leftAnchor, constant: 11),
            
            messageLabel.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            messageLabel.leftAnchor.constraint(equalTo: alarmImageView.rightAnchor, constant: 10),
            
            okButton.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            okButton.heightAnchor.constraint(equalTo: self.heightAnchor),
            okButton.rightAnchor.constraint(equalTo: self.rightAnchor),
            okButton.widthAnchor.constraint(equalToConstant: 55),
            
            okLabel.centerYAnchor.constraint(equalTo: okButton.centerYAnchor),
            okLabel.centerXAnchor.constraint(equalTo: okButton.centerXAnchor),
            okLabel.widthAnchor.constraint(equalToConstant: 35),
            okLabel.heightAnchor.constraint(equalToConstant: 24),

        ])
    }
    
    
    func hide() {
        UIView.animate(withDuration: 0.3) { [weak self] in
            self?.alpha = 0
        }
    }
    
    func show() {
        isHidden = false
        alpha = 0
        UIView.animate(withDuration: 0.3) { [weak self] in
            self?.alpha = 1
        }
    }
    
    @objc func tappedOkButton(_ sender: UIButton) {
        hide()
    }
}
