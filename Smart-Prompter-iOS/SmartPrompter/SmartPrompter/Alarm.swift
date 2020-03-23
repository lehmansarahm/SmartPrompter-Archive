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
    var year:Int?
    var month:Int?
    var day:Int?
    var hour:Int?
    var minute:Int?
    var active:Bool?
    var status:String?
    var deleteRequest:String?
    
    
    func encode(to container: inout PersistenceContainer) {
        container["label"] = label
        container["hour"] = hour
        container["minute"] = minute
        container["active"] = active
        container["year"] = year
        container["month"] = month
        container["day"] = day
    }
    
    init(){}
    
    init(label:String, hour:Int, minute:Int, year:Int, month:Int, day:Int, active:Bool) {
        self.label = label
        self.hour = hour
        self.minute = minute
        self.active = active
        self.year = year
        self.month = month
        self.minute = minute
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
