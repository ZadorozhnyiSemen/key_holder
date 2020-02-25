//
//  ViewController.swift
//  iosApp
//
//  Created by user on 12/23/19.
//  Copyright © 2019 Semyon. All rights reserved.
//

import UIKit
import StormyAPI

class ViewController: UIViewController {
    
    @IBOutlet weak var nameField: UITextField!
    @IBOutlet weak var telegramField: UITextField!
    @IBOutlet weak var continueButton: UIButton!
    
    private var registrationWatcher: Ktor_ioCloseable? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        registrationWatcher = Keyholder.registrationState.watch { state in
            switch (state) {
                
            case RegistrationState.userSaved:
                self.goToMain()
            case RegistrationState.alreadyRegistered:
                self.goToMain()
            default:
                print("default")
            }
        }
        
        Keyholder.checkRegistration()
    }
    
    private func goToMain() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondVC = storyboard.instantiateViewController(identifier: "MainScreen")
        show(secondVC, sender: self)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        registrationWatcher?.close()
    }
    
    @IBAction func continueClicked(_ sender: Any) {
        Keyholder.saveUser(name: nameField.text!, nickname: telegramField.text!)
    }
    
    @IBAction func textFieldEditingDidChange(_ sender: Any) {
        if nameField.text != "" && telegramField.text != "" {
            continueButton.isEnabled = true
        } else {
            continueButton.isEnabled = false
        }
    }
    
}
