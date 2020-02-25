//
//  AppDelegate.swift
//  iosApp
//
//  Created by user on 12/23/19.
//  Copyright © 2019 Semyon. All rights reserved.
//

import UIKit
import StormyAPI
import Firebase

let Keyholder = KeyStoreService(applicationContext: ApplicationContext())

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(_ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions:
        [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
      FirebaseApp.configure()
        
      return true
    }
}
