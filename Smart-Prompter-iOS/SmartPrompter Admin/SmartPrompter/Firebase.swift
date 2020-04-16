//
//  Firebase.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 4/6/20.
//  Copyright © 2020 Likhon Gomes. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import FirebaseStorage

class FirebaseController {
    
    init() {

    }
    
    func uploadImage(image:UIImage, imageName:String){
        guard let imageData = image.jpegData(compressionQuality: 0.1) else {
            return
        }

        let storageRef = Storage.storage().reference()

        // Create a reference to the file you want to upload
        
        let riversRef = storageRef.child("\(userID!)/\(imageName)Icon")

        // Upload the file to the path "images/rivers.jpg"
        let uploadTask = riversRef.putData(imageData, metadata: nil) { (metadata, error) in
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
          }
        }
    }
    
}
