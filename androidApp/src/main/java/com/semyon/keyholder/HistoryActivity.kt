package com.semyon.keyholder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.utils.io.core.Closeable
import kotlinx.android.synthetic.main.activity_history.history_rv
import java.text.DateFormat

class HistoryActivity : AppCompatActivity() {

    private lateinit var datbase: FirebaseFirestore
    private val historyAdapter: HistoryItemAdapter by lazy {
        HistoryItemAdapter()
    }
    private lateinit var stateWatcher: Closeable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        history_rv.adapter = historyAdapter

        stateWatcher = KeyHolderApp.service.historyState.watch {
            when (it) {
                is HistoryLoaded -> {
                    historyAdapter.items = it.data
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        datbase = FirebaseFirestore.getInstance()
        datbase.collection("keyholder")
            .document("history")
            .collection("data")
            .orderBy("to")
            .get()
            .addOnSuccessListener {
                KeyHolderApp.service.showHistoory(it.documents
                    .map { doc ->
                        History(
                            doc.getString("name") ?: "",
                            doc.getString("nick") ?: "",
                            doc.getDate("from").toString(),
                            doc.getDate("to").toString()
                        )
                    }.sortedByDescending { history -> history.to }
                )
            }
            .addOnFailureListener {
                println(it.message)
            }
    }

    override fun onDestroy() {
        stateWatcher.close()
        super.onDestroy()
    }
}
