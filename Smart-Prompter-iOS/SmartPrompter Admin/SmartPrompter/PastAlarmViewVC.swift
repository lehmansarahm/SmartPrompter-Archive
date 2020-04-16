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

class PastAlarmViewVC: RootViewController, UITextFieldDelegate {
    
    let backButton = UIButton()
    let detailTextView = UITextView()
    
    let cancelButton = UIButton()
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
        //cancelButtonSetup()

        imageViewSetup()
        statusStatusLabelSetup()
        detailTextViewSetup()
        prepareDataforDetailTextView()
        //showData()
    }
    
    override func topLeftButtonTapped() {
        dismiss(animated: true, completion: nil)
    }

    func detailTextViewSetup(){
        view.addSubview(detailTextView)
        detailTextView.translatesAutoresizingMaskIntoConstraints = false
        detailTextView.leadingAnchor.constraint(equalTo: view.leadingAnchor,constant: 20).isActive = true
        detailTextView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20).isActive = true
        detailTextView.bottomAnchor.constraint(equalTo: view.bottomAnchor,constant: -10).isActive = true
        detailTextView.topAnchor.constraint(equalTo: statusStatusLabel.bottomAnchor, constant: 10).isActive = true
        detailTextView.isSelectable = false
        detailTextView.isEditable = false
        detailTextView.isScrollEnabled = true
        detailTextView.font = UIFont.systemFont(ofSize: 18)
        detailTextView.textAlignment = .center
        //detailTextView.text = "the quick brown fox jumps over the lazy dog"
        
    }
        
    
    
    func prepareDataforDetailTextView(){
        

        detailTextView.text = """
        Scheduled Date:  \(alarm.scheduledMonth!)/\(alarm.scheduledDay!)/\(alarm.scheduledYear!)
        Scheduled Time:  \(alarm.scheduledHour!%12):\(alarm.scheduledMinute!)\n\n
        """
        
        if alarm.acknowledgedMonth != nil && alarm.acknowledgedDay != nil && alarm.acknowledgedYear != nil {
            detailTextView.text.append("""
                Acknowledge Date: \(alarm.acknowledgedMonth!)/\(alarm.acknowledgedDay!)/\(alarm.acknowledgedYear!)\n
        """)
        }
        
        if alarm.acknowledgedHour != nil && alarm.acknowledgedMinute != nil {
            detailTextView.text.append("""
                Acknowledge Time: \(alarm.acknowledgedHour!):\(alarm.acknowledgedMinute!)\n
        """)
        }

        if alarm.completedMonth != nil && alarm.completedDay != nil && alarm.completedYear != nil {
            detailTextView.text.append("""
                \nCompletion Date: \(alarm.completedMonth!)/\(alarm.completedDay!)/\(alarm.completedYear!)\n
        """)
        }
        
        if alarm.completedHour != nil && alarm.completedMinute != nil {
            detailTextView.text.append("""
                Completion Time: \(alarm.completedHour!):\(alarm.completedMinute!)
        """)
        }
                
    }
   
    
    func imageViewSetup(){
        view.addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.topAnchor.constraint(equalTo: view.topAnchor, constant: 70).isActive = true
        imageView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        imageView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        imageView.heightAnchor.constraint(equalToConstant: 300).isActive = true
        imageView.contentMode = .scaleAspectFit
        imageView.backgroundColor = .black
        
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
        func backButtonSetup() {
            view.addSubview(backButton)
            backButton.translatesAutoresizingMaskIntoConstraints = false
            backButton.heightAnchor.constraint(equalToConstant: 60).isActive = true
            backButton.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
            backButton.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
            //backButton.setBackgroundImage(UIImage(named: "backButton"), for: .normal)
            //backButton.contentMode = .scaleAspectFill
            backButton.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
            backButton.setTitle("Back", for: .normal)
            backButton.backgroundColor = .red
            backButton.addTarget(self, action: #selector(backButtonClicked), for: .touchUpInside)
        }
    }
    
    @objc func backButtonClicked() {
        dismiss(animated: true, completion: nil)
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
        cancelButton.addTarget(self, action: #selector(cancelButtonClicked), for: .touchUpInside)
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
    }

    
    func statusStatusLabelSetup() {
        view.addSubview(statusStatusLabel)
        statusStatusLabel.translatesAutoresizingMaskIntoConstraints = false
        statusStatusLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        statusStatusLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        statusStatusLabel.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: 10).isActive = true
        
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
