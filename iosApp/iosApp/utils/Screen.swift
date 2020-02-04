//
//  UIColor.swift
//  iosApp
//
//  Created by user on 12/23/19.
//  Copyright © 2019 Semyon. All rights reserved.
//

import Foundation
import UIKit

extension UIColor {
    
    @nonobjc class var topColor: UIColor {
        return UIColor(displayP3Red: 40.0 / 255.0, green: 40.0 / 255.0, blue: 30.0 / 255.0, alpha: 1.0)
    }
    
    @nonobjc class var botColor: UIColor {
        return UIColor(displayP3Red: 200.0 / 255.0, green: 200.0 / 255.0, blue: 180.0 / 255.0, alpha: 1.0)
    }
}
