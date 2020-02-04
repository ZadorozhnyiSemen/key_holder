//
//  ViewController.swift
//  iosApp
//
//  Created by user on 12/23/19.
//  Copyright © 2019 Semyon. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        view.backgroundColor = UIColor.clear
        let gradient = CAGradientLayer()
        gradient.frame = view.frame
        gradient.colors = [ UIColor.topColor.cgColor , UIColor.botColor.cgColor]
        gradient.locations = [0, 1]
    
        let mock = UIView(frame: view.bounds)
        mock.backgroundColor = UIColor.red
        view.layer.addSublayer(gradient)
        
        print(gradient.frame)
        
        Forecast.forecast.watch { forecast in
            guard let temp = forecast?.currently?.temperature else {
                return
            }
            print(temp)
        }
    }
    
    @IBAction func showAlert() {
        
    }
    
}
