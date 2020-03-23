//
//  Alarm.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 9/19/19.
//  Copyright © 2019 Likhon Gomes. All rights reserved.
//

import Foundation
import GRDB

class Alarm: PersistableRecord, Codable, FetchableRecord {
    
    var id:Int?
    var firebaseID:String?
    var label:String?
    var scheduledHour:Int?
    var scheduledMinute:Int?
    var active:Bool?
    var scheduledYear:Int?
    var scheduledMonth:Int?
    var scheduledDay:Int?
    
    var acknowledgedDay:Int?
    var acknowledgedMonth:Int?
    var acknowledgedYear:Int?
    var acknowledgedHour:Int?
    var acknowledgedMinute:Int?
    
    var completedDay:Int?
    var completedMonth:Int?
    var completedYear:Int?
    var completedHour:Int?
    var completedMinute:Int?
    
    var status:String?
    var deleteRequest:String?
    
    
//    func encode(to container: inout PersistenceContainer) {
//        container["label"] = label
//        container["hour"] = scheduledHour
//        container["minute"] = scheduledMinute
//        container["active"] = active
//        container["year"] = scheduledYear
//        container["month"] = scheduledMonth
//        container["day"] = scheduledDay
//    }
    
    
    
    init(){}
    
    init(label:String, hour:Int, minute:Int, year:Int, month:Int, day:Int, active:Bool, status:String) {
        self.label = label
        self.scheduledHour = hour
        self.scheduledMinute = minute
        self.active = active
        self.scheduledYear = year
        self.scheduledMonth = month
        self.scheduledMinute = minute
        self.status = status
        self.deleteRequest = ""
    }
}

class AlarmDB{
    
    init() {
        _ = try! dbQueue.write { db in
            // Create database table
            try db.create(table: "Alarm", ifNotExists: true) { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("label", .text).notNull()
                t.column("hour", .integer).notNull()
                t.column("minute", .integer).notNull()
                t.column("active", .integer).notNull()
                t.column("year", .integer).notNull()
                t.column("month", .integer).notNull()
                t.column("day", .integer).notNull()
                print("alarm table created")
            }
        }
    }
    
    func insert(user:Alarm) {
        try! dbQueue.write { db in
            try! user.insert(db)
        }
    }
    
    func getAll() -> [Alarm] {
        
        //    let dbQueue = try! DatabaseQueue(path: pathToDatabase.absoluteString)
        var allAlarm = [Alarm]()
        try! dbQueue.read { db in
            allAlarm = try! Alarm.fetchAll(db)
            sleep(1)
        }
        return allAlarm
    }
    
    func getActiveAlarms() -> [Alarm] {
        var presenters = Array<Alarm>()
        try! dbQueue.read { db in
            presenters = try Alarm.fetchAll(db, sql: "SELECT * FROM Alarm WHERE active = ?", arguments: [1])
        }
        return presenters
    }
    
    func getInactiveAlarms() -> [Alarm] {
        var presenters = Array<Alarm>()
        try! dbQueue.read { db in
            presenters = try Alarm.fetchAll(db, sql: "SELECT * FROM Alarm WHERE active = ?", arguments: [0])
        }
        return presenters
    }
    
}
