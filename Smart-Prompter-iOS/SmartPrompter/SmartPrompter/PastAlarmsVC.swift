//
//  PastAlarmsVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/10/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import UIKit
import SQLite3
import GRDB

class PastAlarmsVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return inactiveAlarm.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = alarmTable.dequeueReusableCell(withIdentifier: "tableCell", for: indexPath as IndexPath) as! UITableViewCell
        cell.textLabel?.text = inactiveAlarm[indexPath.row].label
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = AlarmVC()
        //vc.alarmDateTextField.text = inactiveAlarm[indexPath.row].date
        //vc.alarmTimeTextField.text = inactiveAlarm[indexPath.row].time
        vc.alarmNameTextField.text = inactiveAlarm[indexPath.row].label
        vc.statusStatusLabel.text = "\(inactiveAlarm[indexPath.row].active!)"
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true, completion: nil)
    }

    let backButton = UIButton()
    let alarmTable = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.addNavigationBar(viewControllerName: "Past Alarms", leftButton: backButton)
        view.backgroundColor = .white
        backButtonSetup()
        alarmTableSetup()
    }
    
    func backButtonSetup() {
        backButton.translatesAutoresizingMaskIntoConstraints = false
        //backButton.leadingAnchor.constraint(equalTo: navView.leadingAnchor, constant: 5).isActive = true
        //backButton.bottomAnchor.constraint(equalTo: navView.bottomAnchor, constant: -5).isActive = true
        backButton.heightAnchor.constraint(equalToConstant: 30).isActive = true
        backButton.widthAnchor.constraint(equalToConstant: 30).isActive = true
        //backButton.backgroundColor = .blue
        backButton.setBackgroundImage(UIImage(named: "backButton"), for: .normal)
        backButton.contentMode = .scaleAspectFill
        backButton.addTarget(self, action: #selector(backButtonClicked), for: .touchUpInside)
    }
    
    @objc func backButtonClicked() {
        dismiss(animated: true, completion: nil)
    }
    
    func alarmTableSetup() {
        view.addSubview(alarmTable)
        alarmTable.translatesAutoresizingMaskIntoConstraints = false
        if #available(iOS 11.0, *) {
            alarmTable.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 90).isActive = true
        } else {
            alarmTable.topAnchor.constraint(equalTo: view.topAnchor, constant: 90).isActive = true
        }
        alarmTable.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        alarmTable.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        alarmTable.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        alarmTable.register(UITableViewCell.self, forCellReuseIdentifier: "tableCell")
        alarmTable.delegate = self
        alarmTable.dataSource = self

    }
    

}
