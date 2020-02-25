//
//  MainScreenViewController.swift
//  iosApp
//
//  Created by user on 2/9/20.
//  Copyright © 2020 Semyon. All rights reserved.
//

import Foundation
import UIKit
import StormyAPI
import FirebaseFirestore

class MainScreenViewController : UIViewController {
    
    private let db = Firestore.firestore()
    
    private var stateWatcher: Ktor_ioCloseable? = nil
    
    @IBOutlet weak var status: UILabel!
    @IBOutlet weak var take: UIButton!
    @IBOutlet weak var returnButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        stateWatcher = Keyholder.keyHolderState.watch { state in
            switch (state) {
            case is Available:
                self.status.text = "Available"
                self.take.isHidden = false
                self.returnButton.isHidden = true
            case is TakenByUser:
                self.status.text = "Taken by me"
                self.take.isHidden = true
                self.returnButton.isHidden = false
            case let state as Taken:
                self.status.text = "Taken by \(state.component1())"
                self.take.isHidden = true
                self.returnButton.isHidden = false
            case let state as Take:
                self.status.text = "Take request"
                self.db.collection("keyholder").document("current")
                    .setData([
                        "taken" : true,
                        "holderName" : state.component1().component1(),
                        "holderTelegram" : state.component1().component2(),
                        "pickUpTime" : state.component1().component3()
                    ])
                Keyholder.updateKeyStatus(status: KeyStatus(
                    taken: true,
                    holderName: state.component1().component1(),
                    holderTelegram: state.component1().component2(),
                    pickUpTime: nil,
                    returnTime: 0
                ))
            case let state as Return:
                self.status.text = "Return request"
                let current = ["taken" : false]
                let historyItem = [
                    "name" : state.component1().component1(),
                    "nick" : state.component1().component2(),
                    "from" : Timestamp(seconds: (state.component1().component3() as? Int64 ?? 0) / 1000, nanoseconds: 0),
                    "to" : Timestamp(seconds: (state.component1().component4() as? Int64 ?? 0) / 1000, nanoseconds: 0)
                    ] as [String : Any]
                self.db.collection("keyholder").document("current").setData(current)
                self.db.collection("keyholder")
                    .document("history")
                    .collection("data")
                    .document()
                    .setData(historyItem)
                Keyholder.updateKeyStatus(status: KeyStatus(taken: false, holderName: "nil", holderTelegram: "nil", pickUpTime: nil, returnTime: 0))
            default:
                return
            }
        }
        
        db.collection("keyholder").document("current").getDocument { (snap, error) in
            if let snap = snap, snap.exists, let snapData = snap.data() {
                guard let taken = snapData["taken"] as? Bool else { return }
                if taken {
                    guard let name = snapData["holderName"] as? String else { return }
                    guard let telegram = snapData["holderTelegram"] as? String else { return }
                    let takeTime = snapData["pickUpTime"] as? Int64 ?? 0
                    let returnTime = snapData["returnTime"] as? Int64 ?? 0
                    Keyholder.updateKeyStatus(status: KeyStatus(
                        taken: taken, holderName: name, holderTelegram: telegram, pickUpTime: takeTime as? KotlinLong, returnTime: returnTime
                    ))
                } else {
                    Keyholder.updateKeyStatus(status: KeyStatus(taken: false, holderName: "nil", holderTelegram: "nil", pickUpTime: nil, returnTime: 0))
                }
                
            }
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        stateWatcher?.close()
    }
    
    @IBAction func takeKeyAction(_ sender: Any) {
        Keyholder.takeKey()
    }
    
    @IBAction func returnKeyAction(_ sender: Any) {
        Keyholder.returnKey()
    }
    
    @IBAction func openHistory(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondVC = storyboard.instantiateViewController(identifier: "HistoryScreen")
        present(secondVC, animated: true, completion: nil)
    }
}
