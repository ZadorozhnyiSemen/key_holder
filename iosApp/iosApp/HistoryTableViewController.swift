//
//  HistoryTableViewController.swift
//  iosApp
//
//  Created by user on 2/11/20.
//  Copyright © 2020 Semyon. All rights reserved.
//

import UIKit
import FirebaseFirestore
import StormyAPI

class HistoryTableViewController: UITableViewController {
    
    private let db = Firestore.firestore()
    private var historyStateWatcher: Ktor_ioCloseable?
    
//    var historyRecord: [History] = []
    var historyRecord = [History]()

    override func viewDidLoad() {
        super.viewDidLoad()

        historyStateWatcher = Keyholder.historyState.watch { state in
            switch (state) {
                case let it as HistoryLoaded:
                    self.historyRecord = it.component1().map({ history in
                        return History(name: history.component1(),
                                       nick: history.component2(),
                                       from: history.component3(),
                                       to: history.component4()
                        )
                    })
                    self.tableView.reloadData()
                
                default:
                    break
            }
        }
        
        db.collection("keyholder")
        .document("history")
        .collection("data")
        .order(by: "to", descending: true)
            .getDocuments { (snap, error) in
                if let docs = snap {
                    let historyItems = docs.documents.map { doc in
                        return History(name: doc.data()["name"] as? String ?? "",
                                nick: doc.data()["nick"] as? String ?? "",
                                from: (doc.data()["from"] as? Timestamp)?.dateValue().description ?? "",
                                to: (doc.data()["to"] as? Timestamp)?.dateValue().description ?? "")
                    }
                    Keyholder.showHistoory(items: historyItems)
                }
        }
    }

    
}

extension HistoryTableViewController {
    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return historyRecord.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "HistoryCell", for: indexPath) as? HistoryTableViewCell else {
            return UITableViewCell()
        }
        let historyItem = historyRecord[indexPath.row]
        
        cell.name.text = historyItem.name
        cell.telegram.text = historyItem.nick
        cell.fromDate.text = historyItem.from
        cell.toDate.text = historyItem.to

        return cell
    }
}

class HistoryTableViewCell: UITableViewCell {
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var telegram: UILabel!
    @IBOutlet weak var fromDate: UILabel!
    @IBOutlet weak var toDate: UILabel!
}
