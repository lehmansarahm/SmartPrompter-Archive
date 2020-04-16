//
//  AudioRecordVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 3/15/20.
//  Copyright © 2020 Likhon Gomes. All rights reserved.
//

import UIKit
import AVFoundation
import Firebase

class AudioRecordVC: RootViewController, AVAudioRecorderDelegate, AVAudioPlayerDelegate {

//    var recordButton = UIButton()
//    var recordingSession = AVAudioSession()
//    var audioRecorder = AVAudioRecorder()
    
    var recordButton = UIButton()
    let playButton = UIButton()

    let instruction = UITextView()

    let nextButton = UIButton()
    
    var recordingSession: AVAudioSession!
    var audioRecorder: AVAudioRecorder!
    var audioPlayer: AVAudioPlayer?
    var audioFilename:URL? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = #colorLiteral(red: 0.2470588235, green: 0.7019607843, blue: 0.3098039216, alpha: 1)
        viewControllerLabelSetup(labelType: .sub)
        viewContollerLabel.text = "Alarm Sound"
        viewContollerLabel.textColor = .white
        topLeftButtonSetup(buttonType: .square)
        topLeftButton.setImage(#imageLiteral(resourceName: "back"), for: .normal)
        recordButtonSetup()
        playButtonSetup()
        instructionTextViewSetup()
        nextButtonSetup()
        
        recordingSession = AVAudioSession.sharedInstance()

        //MARK: Recorder
        do {
            try recordingSession.setCategory(.playAndRecord, mode: .default)
            try recordingSession.setActive(true)
            recordingSession.requestRecordPermission() { [unowned self] allowed in
                DispatchQueue.main.async {
                    if allowed {
                        self.loadRecordingUI()
                    } else {
                        // failed to record!
                    }
                }
            }
        } catch {
            // failed to record!
        
        }
        
        
        loadAudioPlayer()

        
    }
    
    func loadAudioPlayer() {
        let audioURL = getDocumentsDirectory().appendingPathComponent("recording.m4a")
        //MARK: Audio Player
        do {
            audioPlayer = try AVAudioPlayer(contentsOf: audioURL)
            audioPlayer?.delegate = self
            audioPlayer?.prepareToPlay()
        } catch {
            playButton.isHidden = true
        }
    }
    
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        playButton.isSelected = false
        print("audio stopped")
    }
    

    func loadRecordingUI() {
        //recordButton = UIButton(frame: CGRect(x: 64, y: 64, width: 128, height: 64))
        recordButton.setTitle("Tap to Record", for: .normal)
        recordButton.titleLabel?.font = UIFont.preferredFont(forTextStyle: .title1)
        recordButton.addTarget(self, action: #selector(recordTapped), for: .touchUpInside)
        view.addSubview(recordButton)
    }
    
    
    
    func startRecording() {
        audioFilename = getDocumentsDirectory().appendingPathComponent("recording.m4a")
        //directoryString = audioFilename
        print("directory \(audioFilename)")
        
        let settings = [
            AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
            AVSampleRateKey: 12000,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
        ]

        do {
            audioRecorder = try AVAudioRecorder(url: audioFilename!, settings: settings)
            audioRecorder.delegate = self
            audioRecorder.record()

            recordButton.setTitle("Tap to Stop", for: .normal)
        } catch {
            finishRecording(success: false)
        }
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    func finishRecording(success: Bool) {
        audioRecorder.stop()
        audioRecorder = nil
        loadAudioPlayer()
        playButton.isHidden = false
        if success {
            recordButton.setTitle("Tap to Re-record", for: .normal)
        } else {
            recordButton.setTitle("Tap to Record", for: .normal)
            // recording failed :(
        }
    }
    
    override func topLeftButtonTapped() {
        dismiss(animated: true, completion: nil)
    }
    
    @objc func recordTapped() {
        if audioRecorder == nil {
            startRecording()
        } else {
            finishRecording(success: true)
            uploadToFirebase()
        }
    }
    
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        if !flag {
            finishRecording(success: false)
        }
    }
    
    func uploadToFirebase() {
        print("uploading audio to firebase")
        let audioName = NSUUID().uuidString //You'll get unique audioFile name
        let storageRef = Storage.storage().reference().child("\(userID!)").child("audio")
        let metadata  = StorageMetadata()
        
        

        // Create a reference to the file you want to upload
        let riversRef = storageRef.child("/patientCall.m4a")
        print("audio file name \(audioFilename)")
        if audioFilename != nil {
            // Upload the file to the path "images/rivers.jpg"
            let uploadTask = riversRef.putFile(from: audioFilename!, metadata: nil) { metadata, error in
              guard let metadata = metadata else {
                // Uh-oh, an error occurred!
                return
              }
              // Metadata contains file metadata such as size, content-type.
              let size = metadata.size
              // You can also access to download URL after upload.
              riversRef.downloadURL { (url, error) in
                guard let downloadURL = url else {
                  // Uh-oh, an error occurred!
                  return
                }
                print("downloadURL \(downloadURL)")
              }
            }
        }
    }
    
    
}

extension AudioRecordVC {
    
    func playButtonSetup(){
        view.addSubview(playButton)
        playButton.translatesAutoresizingMaskIntoConstraints = false
        playButton.widthAnchor.constraint(equalToConstant: 100).isActive = true
        playButton.heightAnchor.constraint(equalToConstant: 100).isActive = true
        playButton.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        playButton.topAnchor.constraint(equalTo: recordButton.bottomAnchor, constant: 20).isActive = true
        playButton.setTitle("Play", for: .normal)
        playButton.setTitle("Stop", for: .selected)
        playButton.setTitleColor(#colorLiteral(red: 0.2470588235, green: 0.7019607843, blue: 0.3098039216, alpha: 1), for: .normal)
        playButton.backgroundColor = .white
        playButton.layer.cornerRadius = 50
        playButton.titleLabel?.font = UIFont.boldSystemFont(ofSize: 25)
        playButton.addTarget(self, action: #selector(playButtonTapped), for: .touchUpInside)
    }
    
    @objc func playButtonTapped(){
        if playButton.isSelected == true {
            playButton.isSelected = false
            audioPlayer?.stop()
        } else {
            playButton.isSelected = true
            audioPlayer?.play()
            print("Playing audio")
        }
    }
    
    func recordButtonSetup() {
        view.addSubview(recordButton)
        recordButton.translatesAutoresizingMaskIntoConstraints = false
        recordButton.heightAnchor.constraint(equalToConstant: 200).isActive = true
        recordButton.widthAnchor.constraint(equalToConstant: 200).isActive = true
        recordButton.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        recordButton.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
        recordButton.backgroundColor = .red
        recordButton.layer.cornerRadius = 100
        recordButton.setTitle("Record", for: .normal)
    }
    

    
    func instructionTextViewSetup(){
        view.addSubview(instruction)
        instruction.translatesAutoresizingMaskIntoConstraints = false
        if #available(iOS 11.0, *) {
            instruction.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 70).isActive = true
        } else {
            instruction.topAnchor.constraint(equalTo: view.topAnchor, constant: 70).isActive = true
        }
        instruction.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 40).isActive = true
        instruction.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -40).isActive = true
        instruction.heightAnchor.constraint(equalToConstant: 150).isActive = true
        instruction.text = "Press the red button to record a Notification tone for your client. Press the red button again to stop recording. Press the white play button to play back the recording. Press done button afterwards"
        instruction.font = UIFont.systemFont(ofSize: 18)
        instruction.textAlignment = .center
        instruction.backgroundColor = .clear
        instruction.textColor = .white
    }
    
    
    
    @objc func backButtonClicked(){
        dismiss(animated: true, completion: nil)
    }
    
    func nextButtonSetup() {
        view.addSubview(nextButton)
        nextButton.translatesAutoresizingMaskIntoConstraints = false
        nextButton.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        nextButton.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -40).isActive = true
        nextButton.setTitle("Next", for: .normal)
        nextButton.addTarget(self, action: #selector(nextButtonTapped), for: .touchUpInside)
    }
    
    @objc func nextButtonTapped(){
        let vc = MainVC()
        vc.modalPresentationStyle = .fullScreen
        vc.modalTransitionStyle = .crossDissolve
        present(vc, animated: true, completion: nil)
    }
    
}
