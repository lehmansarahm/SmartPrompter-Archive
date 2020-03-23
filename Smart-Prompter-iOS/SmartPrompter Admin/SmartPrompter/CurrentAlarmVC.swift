//
//  CurrentAlarmVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/10/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import UIKit

class CurrentAlarmVC: UIViewController, UITableViewDataSource, UITableViewDelegate {
    var activeAlarm = [Alarm]()
    let backButton = UIButton()
    let alarmTable = UITableView()
    //let data = ["Ula","La","La","La","Le","Yo"]
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return activeAlarm.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = alarmTable.dequeueReusableCell(withIdentifier: "tableCell", for: indexPath as IndexPath) as! UITableViewCell
        cell.textLabel?.text = activeAlarm[indexPath.row].label
        return cell
    }
    
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = AlarmVC()
        vc.screenName = "Alarm Details"
        vc.editable = false
        vc.saveButton.isHidden = true
        vc.cancelButton.isHidden = true
        vc.statusStatusLabel.text = "Status: \(activeAlarm[indexPath.row].status!)"
        vc.alarm = activeAlarm[indexPath.row]
        vc.modalTransitionStyle = .crossDissolve
        present(vc, animated: true, completion: nil)
    }
    
    
    


    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.addNavigationBar(viewControllerName: "Current Alarms", leftButton: backButton)
        view.backgroundColor = .white
        backButtonSetup()
        alarmTableSetup()
        fetchFromFirebase()
        alarmTable.reloadData()
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
        alarmTable.topAnchor.constraint(equalTo: view.topAnchor, constant: 60).isActive = true
        alarmTable.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        alarmTable.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        alarmTable.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        alarmTable.register(UITableViewCell.self, forCellReuseIdentifier: "tableCell")
        alarmTable.delegate = self
        alarmTable.dataSource = self
        
    }

    func fetchFromFirebase(){
        
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
