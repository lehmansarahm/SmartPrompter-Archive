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

class CreateNewAlarmVC: RootViewController, UITextFieldDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    

    let saveButton = UIButton()
    let deleteButton = UIButton()
    
    let alarmNameLabel = UILabel()
    let dateLabel = UILabel()
    let timeLabel = UILabel()
    let imageView = UIImageView()
    let imageButtonStack = UIStackView()
    
    let alarmNameTextField = UITextField()
    let alarmDateTextField = UITextField()
    let alarmTimeTextField = UITextField()
    let statusLabel = UILabel()
    let statusStatusLabel = UILabel()
    var selectedTextField = UITextField()
    let datePicker = UIDatePicker()
    let timePicker = UIDatePicker()
    let topImageView = UIImageView()
    let addImageButton = UIButton()
    let vcName = UILabel()
    let cameraButton = UIButton()
    let imagePickerButton = UIButton()
    let imageBackButton = UIButton()
    
    var status = ""
    var editable = true
    var screenName = String()
    var alarm = Alarm()
    var image = UIImage()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        
        
        
        topImageViewSetup()
        vcNmaeSetup()
        alarmNameLabelSetup()
        alarmNameTextFieldSetup()
        dateLabelSetup()
        alarmDateTextFieldSetup()
        timeLabelSetup()
        alarmTimeTextFieldSetup()
        statusStatusLabelSetup()

        saveButtonSetup()

        showDatePicker()
        showData()
        imageButtonStackSetup()
        imagePickerButtonSetup()
        addImageButtonSetup()
        cameraButtonSetup()
        imageBackButtonSetup()
        hideKeyboardWhenTappedAround()
        
        topLeftButtonSetup(buttonType: .square)
        topLeftButton.setImage(#imageLiteral(resourceName: "back") , for: .normal)
        buttonStack.addArrangedSubview(saveButton)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        picker.dismiss(animated: true)

        
        guard let image = info[.editedImage] as? UIImage else {
            print("No image found")
            return
        }
        self.image = image
        
        imagePickerButton.isHidden = true
        cameraButton.isHidden = true
        addImageButton.isHidden = false
        addImageButton.setTitle("", for: .normal)
        topImageView.image = image
        imageBackButton.isHidden = true
    }
    
    func imageButtonStackSetup(){
        topImageView.addSubview(imageButtonStack)
        imageButtonStack.translatesAutoresizingMaskIntoConstraints = false
        imageButtonStack.leadingAnchor.constraint(equalTo: topImageView.leadingAnchor, constant: 10).isActive = true
        imageButtonStack.trailingAnchor.constraint(equalTo: topImageView.trailingAnchor, constant: -10).isActive = true
        imageButtonStack.bottomAnchor.constraint(equalTo: topImageView.bottomAnchor, constant: -10).isActive = true
        imageButtonStack.topAnchor.constraint(equalTo: topImageView.topAnchor, constant: 30).isActive = true
        imageButtonStack.backgroundColor = .red
        imageButtonStack.distribution = .fillEqually
        imageButtonStack.spacing = 10
    }
    
    
    
    func topImageViewSetup(){
        view.addSubview(topImageView)
        topImageView.translatesAutoresizingMaskIntoConstraints = false
        topImageView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        topImageView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        topImageView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        topImageView.heightAnchor.constraint(equalTo: view.heightAnchor, multiplier: 0.4).isActive = true
        topImageView.backgroundColor = #colorLiteral(red: 0.2470588235, green: 0.7019607843, blue: 0.3098039216, alpha: 1)
        topImageView.contentMode = .scaleAspectFit
        topImageView.isUserInteractionEnabled = true
    }
    
    
    
    func addImageButtonSetup(){
        imageButtonStack.addArrangedSubview(addImageButton)
        addImageButton.translatesAutoresizingMaskIntoConstraints = false
        addImageButton.titleLabel?.font = UIFont.boldSystemFont(ofSize: 25)
        if topImageView.image == nil {
            addImageButton.setTitle("Tap to add image", for: .normal)
        }
        addImageButton.contentMode = .scaleAspectFit
        addImageButton.addTarget(self, action: #selector(addImageTapped), for: .touchUpInside)
    }
    
    @objc func addImageTapped(){
        UIView.animate(withDuration: 0.2) {
            self.cameraButton.isHidden = false
            self.addImageButton.isHidden = true
            self.imagePickerButton.isHidden = false
            self.imageBackButton.isHidden = false
        }
        
    }
    
    func cameraButtonSetup(){
        imageButtonStack.addArrangedSubview(cameraButton)
        cameraButton.translatesAutoresizingMaskIntoConstraints = false
        cameraButton.setTitle("Camera", for: .normal)
        cameraButton.contentMode = .scaleAspectFit
        cameraButton.addTarget(self, action: #selector(cameraButtonTapped), for: .touchUpInside)
        cameraButton.backgroundColor = UIColor.black.withAlphaComponent(0.2)
        cameraButton.isHidden = true
    }
    
    @objc func cameraButtonTapped(){
        let vc = UIImagePickerController()
        vc.sourceType = .camera
        vc.allowsEditing = true
        vc.delegate = self
        present(vc, animated: true)
    }
    
    func imagePickerButtonSetup(){
        imageButtonStack.addArrangedSubview(imagePickerButton)
        imagePickerButton.translatesAutoresizingMaskIntoConstraints = false
        imagePickerButton.setTitle("Photos", for: .normal)
        imagePickerButton.contentMode = .scaleAspectFit
        imagePickerButton.addTarget(self, action: #selector(imagePickerButtonTapped), for: .touchUpInside)
        imagePickerButton.backgroundColor = UIColor.black.withAlphaComponent(0.2)
        imagePickerButton.isHidden = true
    }
    
    @objc func imagePickerButtonTapped(){
        let vc = UIImagePickerController()
        vc.sourceType = .savedPhotosAlbum
        vc.allowsEditing = true
        vc.delegate = self
        present(vc, animated: true)
    }
    
    func imageBackButtonSetup(){
        view.addSubview(imageBackButton)
        imageBackButton.translatesAutoresizingMaskIntoConstraints = false
        imageBackButton.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        imageBackButton.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        imageBackButton.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        imageBackButton.topAnchor.constraint(equalTo: topImageView.bottomAnchor).isActive = true
        imageBackButton.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        imageBackButton.addTarget(self, action: #selector(imageBackButonTapped), for: .touchUpInside)
        imageBackButton.isHidden = true
    }
    
    @objc func imageBackButonTapped(){
        UIView.animate(withDuration: 0.2) {
            self.cameraButton.isHidden = true
            self.imagePickerButton.isHidden = true
            self.imageBackButton.isHidden = true
            self.addImageButton.isHidden = false
        }
    }
    
    override func topLeftButtonTapped() {
        dismiss(animated: true, completion: nil)
    }
    
    func vcNmaeSetup(){
        view.addSubview(vcName)
        vcName.translatesAutoresizingMaskIntoConstraints = false
        vcName.widthAnchor.constraint(equalToConstant: 200).isActive = true
        vcName.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20).isActive = true
        vcName.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20).isActive = true
        vcName.textAlignment = .center
        vcName.textColor = .black
        vcName.heightAnchor.constraint(equalToConstant: 50).isActive = true
        vcName.font = UIFont.boldSystemFont(ofSize: 30)
        vcName.text = "Create New Alarm"
        vcName.topAnchor.constraint(equalTo: topImageView.bottomAnchor, constant: 10).isActive = true
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

    
    func alarmNameLabelSetup() {
        view.addSubview(alarmNameLabel)
        alarmNameLabel.translatesAutoresizingMaskIntoConstraints = false
        alarmNameLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        alarmNameLabel.heightAnchor.constraint(equalToConstant: 30).isActive = true
        alarmNameLabel.topAnchor.constraint(equalTo: vcName.bottomAnchor, constant: 5).isActive = true
        alarmNameLabel.text = "Label"
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
            
            if image != nil {
                print("got called")
                let fbController = FirebaseController()
                fbController.uploadImage(image: image, imageName: alarmNameTextField.text!)
            }
            
            
            alarmTimeTextField.text = ""
            alarmDateTextField.text = ""
            alarmNameTextField.text = ""
            
            alarmTimeTextField.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
            alarmDateTextField.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
            alarmNameTextField.layer.borderColor = #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1)
            
            
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
