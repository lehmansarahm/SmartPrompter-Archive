//
//  RootViewController.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 4/9/20.
//  Copyright © 2020 Likhon Gomes. All rights reserved.
//

import UIKit

class RootViewController: UIViewController {

    let topLeftButton = UIButton()
    let topRightButton = UIButton()
    let viewContollerLabel = UILabel()
    let buttonStack = UIStackView()
    
    let topButtonSize = CGFloat(30)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        buttonStackSetup()
    }
    
    func topLeftButtonSetup(buttonType:ButtonType){
        view.addSubview(topLeftButton)
        topLeftButton.translatesAutoresizingMaskIntoConstraints = false
        topLeftButton.heightAnchor.constraint(equalToConstant: topButtonSize).isActive = true
        if buttonType == .square {
            topLeftButton.widthAnchor.constraint(equalToConstant: topButtonSize).isActive = true
        } else {
            topLeftButton.widthAnchor.constraint(equalToConstant: topButtonSize*2).isActive = true
        }
        topLeftButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 10).isActive = true
        if #available(iOS 11.0, *) {
            topLeftButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10).isActive = true
        } else {
            topLeftButton.topAnchor.constraint(equalTo: view.topAnchor, constant: 10).isActive = true
        }
        topLeftButton.addTarget(self, action: #selector(topLeftButtonTapped), for: .touchUpInside)
    }
    
    @objc func topLeftButtonTapped(){
        print("Top Left Button Tapped")
    }
    
    func topRightButtonSetup(buttonType:ButtonType){
        view.addSubview(topRightButton)
        topRightButton.translatesAutoresizingMaskIntoConstraints = false
        topRightButton.heightAnchor.constraint(equalToConstant: 30).isActive = true
        if buttonType == .square {
            topRightButton.widthAnchor.constraint(equalToConstant: topButtonSize).isActive = true
        } else {
            topRightButton.widthAnchor.constraint(equalToConstant: topButtonSize*2).isActive = true
        }
        topRightButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -10).isActive = true
        if #available(iOS 11.0, *) {
            topRightButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10).isActive = true
        } else {
            topRightButton.topAnchor.constraint(equalTo: view.topAnchor, constant: 10).isActive = true
        }
        topRightButton.addTarget(self, action: #selector(topRightButtonTapped), for: .touchUpInside)
    }
    
    @objc func topRightButtonTapped(){
        print("Top Right Button Tapped")
    }
    
    func viewControllerLabelSetup(labelType:VCType){
        view.addSubview(viewContollerLabel)
        viewContollerLabel.translatesAutoresizingMaskIntoConstraints = false
        viewContollerLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        viewContollerLabel.text = "View Controller"

        if labelType == .main {
            if #available(iOS 11.0, *) {
                viewContollerLabel.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 100).isActive = true
            } else {
                viewContollerLabel.bottomAnchor.constraint(equalTo: view.topAnchor, constant: 100).isActive = true
            }
            viewContollerLabel.font = UIFont.boldSystemFont(ofSize: 35)
        } else {
            if #available(iOS 11.0, *) {
                viewContollerLabel.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 10+topButtonSize).isActive = true
                
            } else {
                viewContollerLabel.bottomAnchor.constraint(equalTo: view.topAnchor, constant: 10+topButtonSize).isActive = true
            }
            viewContollerLabel.font = UIFont.boldSystemFont(ofSize: 25)
        }
    }
    
    func buttonStackSetup(){
        view.addSubview(buttonStack)
        buttonStack.translatesAutoresizingMaskIntoConstraints = false
        buttonStack.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20).isActive = true
        buttonStack.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20).isActive = true
        if #available(iOS 11.0, *) {
            buttonStack.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20).isActive = true
        } else {
            buttonStack.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -20).isActive = true
        }
    }
    
    enum VCType {
        case main
        case sub
    }
    
    enum ButtonType {
        case rectangle
        case square
    }
}


