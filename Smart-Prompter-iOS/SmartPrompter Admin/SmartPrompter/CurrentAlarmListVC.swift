//
//  CurrentAlarmVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/10/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import UIKit

class CurrentAlarmListVC: RootViewController , UITableViewDataSource, UITableViewDelegate {
    var activeAlarm = [Alarm]()
    let alarmTable = UITableView()
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return activeAlarm.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = alarmTable.dequeueReusableCell(withIdentifier: "tableCell", for: indexPath as IndexPath) as! UITableViewCell
        cell.textLabel?.text = activeAlarm[indexPath.row].label
        cell.textLabel?.font = UIFont.systemFont(ofSize: 22)
        if activeAlarm[indexPath.row].deleteRequest == "Requested" {
            cell.backgroundColor = .red
            cell.textLabel?.textColor = .white
        }
        return cell
    }
    
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = CurrentAlarmViewVC()
        vc.screenName = "Alarm Details"
        vc.editable = false
        vc.saveButton.isHidden = true
        vc.statusStatusLabel.text = "Status: \(activeAlarm[indexPath.row].status!)"
        vc.alarm = activeAlarm[indexPath.row]
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true, completion: nil)
    }
    
    
    


    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        viewControllerLabelSetup(labelType: .sub)
        viewContollerLabel.text = "Current Alarms"
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
        print("Firebase called")
        activeAlarm = [Alarm]()
        //inactiveAlarm = [Alarm]()
        
        ref.child("Patients").child(userID!).child("Alarms").observe(.childAdded, with: { (snapshot) in
        
          let value = snapshot.value as? NSDictionary
            let singleAlarm = Alarm()
            singleAlarm.firebaseID = snapshot.key
            singleAlarm.active = value?["active"] as? Bool
            singleAlarm.scheduledHour = value?["scheduledHour"] as? Int
            singleAlarm.scheduledMinute = value?["scheduledMinute"] as? Int
            singleAlarm.scheduledDay = value?["scheduledDay"] as? Int
            singleAlarm.scheduledMonth = value?["scheduledMonth"] as? Int
            singleAlarm.scheduledYear = value?["scheduledYear"] as? Int
            singleAlarm.label = value?["label"] as? String
            singleAlarm.status = value?["status"] as? String
            singleAlarm.deleteRequest = value?["deleteRequest"] as? String
            
            print(singleAlarm.status)
            if(singleAlarm.status! != "Complete"){
                self.activeAlarm.append(singleAlarm)
                print("Printing current Alarm \(singleAlarm)")
            } else {
                //inactiveAlarm.append(singleAlarm)
            }
            self.alarmTable.reloadData()
            //print("Printing snapshot \(snapshot)")
            
            //print("printing data ..... \(self.alarms[0].minute)")
          // ...
          }) { (error) in
            print(error.localizedDescription)
        }
    }

}
