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
import FirebaseStorage

var dbQueue: DatabaseQueue!
let alarmDB = AlarmDB()
var activeAlarm = [Alarm]()
var inactiveAlarm = [Alarm]()
var ref: DatabaseReference!
let userID = Auth.auth().currentUser?.uid
let scheduler = AlarmScheduler()
let fUtil = FirebaseUtil()


var totalTask = Double()
var completedTask = Double()

@available(iOS 10.0, *)
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?
    //var dbQueue: DatabaseQueue!
    //let alarmDB = AlarmDB()
    
    //let notificationCenter = UNUserNotificationCenter.current()


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        completedTask = 0
        totalTask = 0
        UNUserNotificationCenter.current().delegate = self
        
        FirebaseApp.configure()
        ref = Database.database().reference()
        try! setupDatabase(application)
        activeAlarm = alarmDB.getActiveAlarms()
        inactiveAlarm = alarmDB.getInactiveAlarms()
        print("activeAlarm Count \(activeAlarm.count)")
        
        window = UIWindow(frame: UIScreen.main.bounds)
        //let rootView = ViewController()
        
        if Auth.auth().currentUser != nil {
          self.window?.rootViewController = MainVC()
            
        } else {
          self.window?.rootViewController = SignInVC()
        }
        
        window?.makeKeyAndVisible()
        registerForPushNotifications()
        //getNotificationSettings()
        //fetchFromFirebase()
        
        
        
        application.setMinimumBackgroundFetchInterval(UIApplication.backgroundFetchIntervalMinimum)
        return true
    }
    
    func application(_ application: UIApplication,
                     performFetchWithCompletionHandler completionHandler:
                     @escaping (UIBackgroundFetchResult) -> Void) {
        let count = activeAlarm.count
        
        //fetchFromFirebase()
        if(count == activeAlarm.count){
            completionHandler(.noData)
        } else if (count != activeAlarm.count){
            completionHandler(.newData)
        } else {
            completionHandler(.failed)
        }
       
        print("Refreshing in the background")
        completionHandler(.newData)
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
        print("came to foreground")
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
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current() // 1
                .requestAuthorization(options: [.alert, .sound, .badge]) { // 2
                    granted, error in
                    print("Permission granted: \(granted)") // 3
            }
        } else {
            // Fallback on earlier versions
        }
    }
    
    
    
    func getNotificationSettings() {
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().getNotificationSettings { settings in
                print("Notification settings: \(settings)")
            }
        } else {
            // Fallback on earlier versions
        }
    }
    
    
    var alarms = [Alarm]()
    func fetchFromFirebase(){
        let userID = Auth.auth().currentUser?.uid
        ref.child("Patients").child(userID!).child("Alarms").observe(.childAdded, with: { (snapshot) in
            
        
          let value = snapshot.value as? NSDictionary
            let singleAlarm = Alarm()
            singleAlarm.active = value?["active"] as? Bool
            singleAlarm.hour = value?["hour"] as? Int
            singleAlarm.minute = value?["minute"] as? Int
            singleAlarm.label = value?["label"] as? String
            
            activeAlarm.append(singleAlarm)
            
            print("Printing snapshot \(snapshot)")
            //print("printing data ..... \(self.alarms[0].minute)")
          // ...
          }) { (error) in
            print(error.localizedDescription)
        }
    }
    
    
    // This function will be called when the app receive notification
    @available(iOS 10.0, *)
       func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
           print("Will present")
           
         // show the notification alert (banner), and with sound
         completionHandler([.alert, .sound])
       }
         
       // This function will be called right after user tap on the notification
    @available(iOS 10.0, *)
       func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
           let application = UIApplication.shared
           if(application.applicationState == .active){
             print("user tapped the notification bar when the app is in foreground")
             
           }
           
           if(application.applicationState == .inactive)
           {
             
           }
           
         // tell the app that we have finished processing the user’s action / response
         completionHandler()
       }


    
}

@available(iOS 11.0, *)
extension UIDevice {
    var hasNotch: Bool {
        let bottom = UIApplication.shared.keyWindow?.safeAreaInsets.bottom ?? 0
        return bottom > 0
    }
}


