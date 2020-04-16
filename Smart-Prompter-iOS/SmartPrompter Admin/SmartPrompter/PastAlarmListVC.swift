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

class PastAlarmListVC: RootViewController, UITableViewDelegate, UITableViewDataSource {
    
    var inactiveAlarm = [Alarm]()
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return inactiveAlarm.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = alarmTable.dequeueReusableCell(withIdentifier: "tableCell", for: indexPath as IndexPath) as! UITableViewCell
        cell.textLabel?.font = UIFont.systemFont(ofSize: 22)
        cell.textLabel?.text = inactiveAlarm[indexPath.row].label
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = PastAlarmViewVC()
        vc.screenName = inactiveAlarm[indexPath.row].label!
        vc.editable = false
        vc.statusStatusLabel.text = "Status: \(inactiveAlarm[indexPath.row].status!)"
        vc.alarm = inactiveAlarm[indexPath.row]
        vc.editable = false
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true, completion: nil)
    }

    let backButton = UIButton()
    let alarmTable = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .white
        viewControllerLabelSetup(labelType: .sub)
        viewContollerLabel.text = "Past Alarms"
        topLeftButtonSetup(buttonType: .square)
        topLeftButton.setImage(#imageLiteral(resourceName: "backButton"), for: .normal)
        
        alarmTableSetup()
        fetchFromFirebase()
        alarmTable.reloadData()
    }
    
    override func topLeftButtonTapped() {
        dismiss(animated: true, completion: nil)
    }
    
    func alarmTableSetup() {
        view.addSubview(alarmTable)
        alarmTable.translatesAutoresizingMaskIntoConstraints = false
        if #available(iOS 11.0, *) {
            alarmTable.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 70).isActive = true
        } else {
            alarmTable.topAnchor.constraint(equalTo: view.topAnchor, constant: 70).isActive = true
        }
        alarmTable.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        alarmTable.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        alarmTable.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        alarmTable.register(UITableViewCell.self, forCellReuseIdentifier: "tableCell")
        alarmTable.delegate = self
        alarmTable.dataSource = self

    }
    
    func fetchFromFirebase(){
        //activeAlarm = [Alarm]()
        inactiveAlarm = [Alarm]()
        ref.child("Patients").child(userID!).child("Alarms").observe(.childAdded, with: { (snapshot) in
        
          let value = snapshot.value as? NSDictionary
            let singleAlarm = Alarm()
            singleAlarm.active = value?["active"] as? Bool
            singleAlarm.scheduledHour = value?["scheduledHour"] as? Int
            singleAlarm.scheduledMinute = value?["scheduledMinute"] as? Int
            singleAlarm.scheduledDay = value?["scheduledDay"] as? Int
            singleAlarm.scheduledMonth = value?["scheduledMonth"] as? Int
            singleAlarm.scheduledYear = value?["scheduledYear"] as? Int
            
            singleAlarm.acknowledgedHour = value?["acknowledgeHour"] as? Int
            singleAlarm.acknowledgedMinute = value?["acknowledgeMinute"] as? Int
            singleAlarm.acknowledgedDay = value?["acknowledgeDay"] as? Int
            singleAlarm.acknowledgedMonth = value?["acknowledgeMonth"] as? Int
            singleAlarm.acknowledgedYear = value?["acknowledgeYear"] as? Int
            
            singleAlarm.completedHour = value?["completionHour"] as? Int
            singleAlarm.completedMinute = value?["completionMinute"] as? Int
            singleAlarm.completedDay = value?["completionDay"] as? Int
            singleAlarm.completedMonth = value?["completionMonth"] as? Int
            singleAlarm.completedYear = value?["completionYear"] as? Int
            
            singleAlarm.label = value?["label"] as? String
            singleAlarm.status = value?["status"] as? String
            
            
            if(singleAlarm.status == "Complete"){
                self.inactiveAlarm.append(singleAlarm)
            }
            
            
            print(value)
            self.alarmTable.reloadData()
            //print("Printing snapshot \(snapshot)")
            
            //print("printing data ..... \(self.alarms[0].minute)")
          // ...
          }) { (error) in
            print(error.localizedDescription)
        }
    }

    

}
