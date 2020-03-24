//
//  Extensions.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/9/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import Foundation
import UIKit

extension UIView {
    
    func addNavigationBar(viewControllerName:String, leftButton:UIButton = UIButton(), rightButton:UIButton = UIButton())  {
        let navView = UIView()
        addSubview(navView)
        navView.translatesAutoresizingMaskIntoConstraints = false
        navView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        navView.widthAnchor.constraint(equalToConstant: UIScreen.main.bounds.size.width).isActive = true
        navView.heightAnchor.constraint(equalToConstant: 200).isActive = true
        navView.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
        navView.layer.borderWidth = 0.2
        navView.backgroundColor = .purple
        
        
        let viewControllerLabel = UILabel()
        navView.addSubview(viewControllerLabel)
        viewControllerLabel.translatesAutoresizingMaskIntoConstraints = false
        viewControllerLabel.centerXAnchor.constraint(equalTo: navView.centerXAnchor).isActive = true
        if #available(iOS 11.0, *) {
            viewControllerLabel.topAnchor.constraint(equalTo: navView.safeAreaLayoutGuide.topAnchor, constant: 10).isActive = true
        } else {
            viewControllerLabel.topAnchor.constraint(equalTo: navView.topAnchor, constant: 10).isActive = true
        }
        viewControllerLabel.textColor = .white
        viewControllerLabel.text = viewControllerName
        viewControllerLabel.font = UIFont.boldSystemFont(ofSize: 18)
        navView.addSubview(leftButton)
        leftButton.translatesAutoresizingMaskIntoConstraints = false
        leftButton.leadingAnchor.constraint(equalTo: navView.leadingAnchor, constant: 10).isActive = true
        if #available(iOS 11.0, *) {
            leftButton.topAnchor.constraint(equalTo: navView.safeAreaLayoutGuide.topAnchor, constant: 10).isActive = true
        } else {
            leftButton.topAnchor.constraint(equalTo: navView.topAnchor, constant: 10).isActive = true
        }
        leftButton.heightAnchor.constraint(equalToConstant: 25).isActive = true
        leftButton.backgroundColor = .clear
        leftButton.widthAnchor.constraint(equalToConstant: 25).isActive = true
        //leftButton.backgroundColor = .blue
        
        
    }
}

extension UIViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }

    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}

extension DateComponents {
    mutating func addAMinute() {
        if(self.minute != 59){
            self.minute! += 1
        } else {
            self.hour! += 1
            self.minute! = 0
        }
    }
}

