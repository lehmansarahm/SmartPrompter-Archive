//
//  AlarmViewVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 2/15/20.
//  Copyright © 2020 Likhon Gomes. All rights reserved.
//

import UIKit

class AlarmViewVC: UIViewController {

    let cancelButton = UIButton()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        cancelButtonSetup()
        // Do any additional setup after loading the view.
    }
    
    func cancelButtonSetup() {
        view.addSubview(cancelButton)
        cancelButton.translatesAutoresizingMaskIntoConstraints = false
        cancelButton.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        cancelButton.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        cancelButton.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        cancelButton.heightAnchor.constraint(equalToConstant: 60).isActive = true
        cancelButton.backgroundColor = .red
        cancelButton.setTitle("Cancel", for: .normal)
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
