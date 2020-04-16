//
//  AppDelegate.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/5/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import UIKit
import GRDB
import CoreData
import UserNotifications
import Firebase
import FirebaseDatabase
import FirebaseAnalytics

var dbQueue: DatabaseQueue!
let alarmDB = AlarmDB()
//var activeAlarm = [Alarm]()
//var inactiveAlarm = [Alarm]()
var ref: DatabaseReference!
let userID = Auth.auth().currentUser?.uid


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    //var dbQueue: DatabaseQueue!
    //let alarmDB = AlarmDB()

        
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        FirebaseApp.configure()
        ref = Database.database().reference()

        try! setupDatabase(application)
        //activeAlarm = alarmDB.getActiveAlarms()
        //inactiveAlarm = alarmDB.getInactiveAlarms()
        //print("activeAlarm Count \(activeAlarm.count)")
        
        window = UIWindow(frame: UIScreen.main.bounds)
        //let rootView = UserInfo()
        
        if Auth.auth().currentUser != nil {
            fetchFromFirebase()
          self.window?.rootViewController = MainVC()
            //self.window?.rootViewController = RootViewController()
        } else {
          self.window?.rootViewController = SignInVC()
        }
        
        window?.makeKeyAndVisible()
        registerForPushNotifications()
        //fetchFromFirebase()
        return true
    }
    
    

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
        ) {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let token = tokenParts.joined()
        print("Device Token: \(token)")
    }
    
    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register: \(error)")
    }

    private func setupDatabase(_ application: UIApplication) throws {
        let databaseURL = try FileManager.default
            .url(for: .applicationSupportDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
            .appendingPathComponent("SmartPrompter.sqlite")
        dbQueue = try AppDatabase.openDatabase(atPath: databaseURL.path)
        print("DB Path\(dbQueue.path)")
        // Be a nice iOS citizen, and don't consume too much memory
        // See https://github.com/groue/GRDB.swift/blob/master/README.md#memory-management
        dbQueue.setupMemoryManagement(in: application)
    }
    
    func registerForPushNotifications() {
        UNUserNotificationCenter.current() // 1
            .requestAuthorization(options: [.alert, .sound, .badge]) { // 2
                granted, error in
                print("Permission granted: \(granted)") // 3
        }
    }
    
    func fetchFromFirebase(){
        let userID = Auth.auth().currentUser?.uid
        ref.child("Patients").child(userID!).child("Alarms").observe(.childAdded, with: { (snapshot) in
            
        
          let value = snapshot.value as? NSDictionary
            let singleAlarm = Alarm()
            singleAlarm.active = value?["active"] as? Bool
            singleAlarm.scheduledHour = value?["hour"] as? Int
            singleAlarm.scheduledMinute = value?["minute"] as? Int
            singleAlarm.label = value?["label"] as? String
            
            
            if(singleAlarm.active == true){
                //activeAlarm.append(singleAlarm)
            } else {
                //inactiveAlarm.append(singleAlarm)
            }
            
            //print("Printing snapshot \(snapshot)")
            
            //print("printing data ..... \(self.alarms[0].minute)")
          // ...
          }) { (error) in
            print(error.localizedDescription)
        }
    }
    
}

