//
//  NewAlarmVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/9/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import UIKit
import Firebase
import FirebaseStorage

class CurrentAlarmViewVC: RootViewController, UITextFieldDelegate {
    
    let backButton = UIButton()
    let saveButton = UIButton()
    let deleteButton = UIButton()

    
    let alarmDetailsLabel = UILabel()
    let alarmNameLabel = UILabel()
    let dateLabel = UILabel()
    let timeLabel = UILabel()
    let imageView = UIImageView()
    
    
    let alarmNameTextField = UITextField()
    let alarmDateTextField = UITextField()
    let alarmTimeTextField = UITextField()
    let statusLabel = UILabel()
    let statusStatusLabel = UILabel()
    var selectedTextField = UITextField()
    let datePicker = UIDatePicker()
    let timePicker = UIDatePicker()
    
    
    
    var status = ""
    var editable = true
    var screenName = String() 
    var alarm = Alarm()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        topLeftButtonSetup(buttonType: .square)
        topLeftButton.setImage(#imageLiteral(resourceName: "backButton"), for: .normal)
        
        viewControllerLabelSetup(labelType: .sub)
        viewContollerLabel.text = screenName
        
        alarmDetailsLabelSetup()
        alarmNameLabelSetup()
        
        
        alarmNameTextFieldSetup()
        dateLabelSetup()
        alarmDateTextFieldSetup()
        timeLabelSetup()
        alarmTimeTextFieldSetup()
        //statusLabelSetup()
        statusStatusLabelSetup()
        backButtonSetup()

        saveButtonSetup()
        deleteButtonSetup()
        showDatePicker()
        //showTimePicker()
        //imageViewSetup()
        showData()
        
        buttonStack.addArrangedSubview(saveButton)
        buttonStack.addArrangedSubview(deleteButton)
        
        self.hideKeyboardWhenTappedAround()
    }
    
    override func topLeftButtonTapped() {
        dismiss(animated: true, completion: nil)
    }
    
    func showData(){
        if(editable == false){
            alarmNameTextField.text = alarm.label
            alarmTimeTextField.text = "\(alarm.scheduledHour!):\(alarm.scheduledMinute!)"
            alarmDateTextField.text = "\(alarm.scheduledMonth!)/\(alarm.scheduledDay!)/\(alarm.scheduledYear!)"
        }

    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if(textField == alarmTimeTextField){
            alarmTimeTextField.inputView = datePicker
            datePicker.datePickerMode = .time

        } else if (textField == alarmDateTextField){
            alarmDateTextField.inputView = datePicker
            datePicker.datePickerMode = .date

        }
        selectedTextField = textField
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        if(textField == alarmTimeTextField){
            let formatter = DateFormatter()
            formatter.dateFormat = "hh:mm a"
            alarmTimeTextField.text = formatter.string(from: datePicker.date)

        } else if (textField == alarmDateTextField){
            let formatter = DateFormatter()
            formatter.dateFormat = "MM/dd/yyyy"
            alarmDateTextField.text = formatter.string(from: datePicker.date)

        }
    }

    
    func imageViewSetup(){
        if(alarm.label! != nil){
            view.addSubview(imageView)
            imageView.translatesAutoresizingMaskIntoConstraints = false
            imageView.topAnchor.constraint(equalTo: statusStatusLabel.bottomAnchor, constant: 10).isActive = true
            imageView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
            imageView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
            imageView.bottomAnchor.constraint(equalTo: buttonStack.topAnchor, constant: -10).isActive = true
            imageView.contentMode = .scaleAspectFit
            
            
            let islandRef = Storage.storage().reference().child("\(userID!)/\(alarm.label!)")

            // Download in memory with a maximum allowed size of 1MB (1 * 1024 * 1024 bytes)
            islandRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
              if let error = error {
                // Uh-oh, an error occurred!
                print("downloading image \(error)")
              } else {
                // Data for "images/island.jpg" is returned
                let image = UIImage(data: data!)
                self.imageView.image = image
              }
            }
        }
        
    }
    
        
    func showDatePicker(){
        //Formate Date
        datePicker.datePickerMode = .date
        
        //ToolBar
        let toolbar = UIToolbar();
        toolbar.sizeToFit()
        let doneButton = UIBarButtonItem(title: "Done", style: .plain, target: self, action: #selector(donedatePicker));
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelDatePicker));
        toolbar.setItems([doneButton,spaceButton,cancelButton], animated: false)
        alarmDateTextField.inputAccessoryView = toolbar
        alarmTimeTextField.inputAccessoryView = toolbar
        //alarmDateTextField.inputView = datePicker
        
    }
    

    @objc func donedatePicker(){
        self.view.endEditing(true)
    }
    
    @objc func donetimePicker(){
        
        self.view.endEditing(true)
    }
    
    @objc func cancelDatePicker(){
        self.view.endEditing(true)
    }
    

    
    func alarmNameTextFieldSetup() {
        view.addSubview(alarmNameTextField)
        alarmNameTextField.translatesAutoresizingMaskIntoConstraints = false
        alarmNameTextField.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 10).isActive = true
        alarmNameTextField.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -10).isActive = true
        alarmNameTextField.heightAnchor.constraint(equalToConstant: 30).isActive = true
        alarmNameTextField.topAnchor.constraint(equalTo: alarmNameLabel.bottomAnchor, constant: 2).isActive = true
        alarmNameTextField.textAlignment = .center
        alarmNameTextField.placeholder = "Label"
        alarmNameTextField.layer.borderColor = #colorLiteral(red: 0.501960814, green: 0.501960814, blue: 0.501960814, alpha: 1)
        alarmNameTextField.layer.borderWidth = 0.5
        alarmNameTextField.isEnabled = editable
        alarmNameTextField.delegate = self
    }
    
    func alarmDateTextFieldSetup() {
        
        
        view.addSubview(alarmDateTextField)
        alarmDateTextField.translatesAutoresizingMaskIntoConstraints = false
        alarmDateTextField.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 10).isActive = true
        alarmDateTextField.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -10).isActive = true
        alarmDateTextField.heightAnchor.constraint(equalToConstant: 30).isActive = true
        alarmDateTextField.topAnchor.constraint(equalTo: dateLabel.bottomAnchor, constant: 2).isActive = true
        alarmDateTextField.placeholder = "Date"
        alarmDateTextField.textAlignment = .center
        alarmDateTextField.layer.borderColor = #colorLiteral(red: 0.501960814, green: 0.501960814, blue: 0.501960814, alpha: 1)
        alarmDateTextField.layer.borderWidth = 0.5
        alarmDateTextField.isEnabled = editable
        alarmDateTextField.delegate = self
    }
    
    func alarmTimeTextFieldSetup() {
        view.addSubview(alarmTimeTextField)
        alarmTimeTextField.translatesAutoresizingMaskIntoConstraints = false
        alarmTimeTextField.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 10).isActive = true
        alarmTimeTextField.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -10).isActive = true
        alarmTimeTextField.heightAnchor.constraint(equalToConstant: 30).isActive = true
        alarmTimeTextField.topAnchor.constraint(equalTo: timeLabel.bottomAnchor, constant: 2).isActive = true
        alarmTimeTextField.placeholder = "Time"
        alarmTimeTextField.textAlignment = .center
        alarmTimeTextField.layer.borderColor = #colorLiteral(red: 0.501960814, green: 0.501960814, blue: 0.501960814, alpha: 1)
        alarmTimeTextField.layer.borderWidth = 0.5
        alarmTimeTextField.isEnabled = editable
        alarmTimeTextField.delegate = self
    }
    
    func statusLabelSetup() {
        view.addSubview(statusLabel)
        statusLabel.translatesAutoresizingMaskIntoConstraints = false
        statusLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        statusLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        statusLabel.topAnchor.constraint(equalTo: alarmTimeTextField.bottomAnchor, constant: 10).isActive = true
        if (editable == false){
            statusLabel.text = "Status"
        }
    }
    
    func alarmNameLabelSetup() {
        view.addSubview(alarmNameLabel)
        alarmNameLabel.translatesAutoresizingMaskIntoConstraints = false
        alarmNameLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        alarmNameLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        alarmNameLabel.topAnchor.constraint(equalTo: alarmDetailsLabel.bottomAnchor, constant: 5).isActive = true
        alarmNameLabel.text = "Label"
    }
    
    func alarmDetailsLabelSetup() {
        view.addSubview(alarmDetailsLabel)
        alarmDetailsLabel.translatesAutoresizingMaskIntoConstraints = false
        alarmDetailsLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        alarmDetailsLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        alarmDetailsLabel.topAnchor.constraint(equalTo: view.topAnchor, constant: 70).isActive = true
        //alarmDetailsLabel.text = "Alarm Details"
        alarmDetailsLabel.font = UIFont.systemFont(ofSize: 30)
    }

    
    func dateLabelSetup() {
        view.addSubview(dateLabel)
        dateLabel.translatesAutoresizingMaskIntoConstraints = false
        dateLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        dateLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        dateLabel.topAnchor.constraint(equalTo: alarmNameTextField.bottomAnchor, constant: 10).isActive = true
        dateLabel.text = "Date"
    }
    
    func timeLabelSetup() {
        view.addSubview(timeLabel)
        timeLabel.translatesAutoresizingMaskIntoConstraints = false
        timeLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        timeLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        timeLabel.topAnchor.constraint(equalTo: alarmDateTextField.bottomAnchor, constant: 10).isActive = true
        timeLabel.text = "Time"
    }


    
    
    
    
    func backButtonSetup() {
        backButton.translatesAutoresizingMaskIntoConstraints = false
        backButton.heightAnchor.constraint(equalToConstant: 30).isActive = true
        backButton.widthAnchor.constraint(equalToConstant: 30).isActive = true
        backButton.setBackgroundImage(UIImage(named: "backButton"), for: .normal)
        backButton.contentMode = .scaleAspectFill
        backButton.addTarget(self, action: #selector(backButtonClicked), for: .touchUpInside)
    }
    
    @objc func backButtonClicked() {
        dismiss(animated: true, completion: nil)
    }

    func saveButtonSetup() {
        saveButton.translatesAutoresizingMaskIntoConstraints = false
        saveButton.setTitle("Save", for: .normal)
        saveButton.backgroundColor = .red
        saveButton.addTarget(self, action: #selector(saveButtonClicked), for: .touchUpInside)
    }
    
    @objc func saveButtonClicked() {
        
        if(alarmTimeTextField.text != "" && alarmDateTextField.text != "" && alarmNameTextField.text != ""){
            //let alarm = Alarm(label: alarmNameTextField.text!, date: alarmDateTextField.text!, time: alarmTimeTextField.text!, active: 1)
            //alarmDB.insert(user: alarm)
                
            
            let components = Calendar.current.dateComponents([.hour, .minute, .day, .month, .year], from: datePicker.date)
                
                //let hour = components.hour!
                //let minute = components.minute!
                
                                                                                                                                                                                                                                                                                                                                                                             
            /* ref.child("Patients").child(Auth.auth().currentUser!.uid).child("Alarms").childByAutoId().setValue(["label":alarmNameTextField.text!,"scheduledHour":components.hour,"minute":components.minute, "active":true, "year":components.year,"month":components.month,"day":components.day])
             */
            
            //print("xxxxxxxxxx \(dateComponent.day)")
            ref.child("Patients").child(Auth.auth().currentUser!.uid).child("Alarms").childByAutoId().setValue([
                "label":alarmNameTextField.text!,
                "scheduledHour":components.hour,
                "scheduledMinute":components.minute,
                "scheduledDay":components.day,
                "scheduledYear":components.year,
                "scheduledMonth":components.month,
                "active":false,
                "status":"Active"])
            
            alarmTimeTextField.text = ""
            alarmDateTextField.text = ""
            alarmNameTextField.text = ""
            
            alarmTimeTextField.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
            alarmDateTextField.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
            alarmNameTextField.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
            
            //activeAlarm = alarmDB.getActiveAlarms()
            //inactiveAlarm = alarmDB.getInactiveAlarms()
            dismiss(animated: true, completion: nil)
        } else {
            if (alarmTimeTextField.text == ""){
                alarmTimeTextField.layer.borderColor = #colorLiteral(red: 0.9254902005, green: 0.2352941185, blue: 0.1019607857, alpha: 1)
                alarmTimeTextField.layer.borderWidth = 1
            }
            if(alarmDateTextField.text == ""){
                alarmDateTextField.layer.borderColor = #colorLiteral(red: 0.9254902005, green: 0.2352941185, blue: 0.1019607857, alpha: 1)
                alarmDateTextField.layer.borderWidth = 1
            }
            if(alarmNameTextField.text == ""){
                alarmNameTextField.layer.borderColor = #colorLiteral(red: 0.9254902005, green: 0.2352941185, blue: 0.1019607857, alpha: 1)
                alarmNameTextField.layer.borderWidth = 1
            }
        }
        
    }
    

    
    @objc func cancelButtonClicked() {
        dismiss(animated: true, completion: nil)
    }
    
    func deleteButtonSetup() {
        deleteButton.translatesAutoresizingMaskIntoConstraints = false
        deleteButton.setTitle("Delete", for: .normal)
        deleteButton.backgroundColor = .red
        deleteButton.addTarget(self, action: #selector(deleteButtonClicked), for: .touchUpInside)
    }
    
    @objc func deleteButtonClicked() {
        print(alarm.label)
        alarm.deleteRequest = "Requested"
        ref.child("Patients").child(Auth.auth().currentUser!.uid).child("Alarms").child("\(alarm.firebaseID!)").child("deleteRequest").setValue("Requested")
        dismiss(animated: true, completion: nil)
    }

    
    
    func statusStatusLabelSetup() {
        view.addSubview(statusStatusLabel)
        statusStatusLabel.translatesAutoresizingMaskIntoConstraints = false
        statusStatusLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        statusStatusLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        statusStatusLabel.topAnchor.constraint(equalTo: alarmTimeTextField.bottomAnchor, constant: 20).isActive = true
        
        if (statusStatusLabel.text == "Complete"){
            statusStatusLabel.text = status
            statusStatusLabel.textColor = #colorLiteral(red: 0.2745098174, green: 0.4862745106, blue: 0.1411764771, alpha: 1)
        } else if (statusStatusLabel.text == "Incomplete"){
            statusStatusLabel.text = status
            statusStatusLabel.textColor = #colorLiteral(red: 0.521568656, green: 0.1098039225, blue: 0.05098039284, alpha: 1)
        }
        //statusStatusLabel.text = "Active"
        statusStatusLabel.font = UIFont.boldSystemFont(ofSize: 20)
    }

}
