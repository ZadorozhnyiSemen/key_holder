package com.semyon.keyholder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.utils.io.core.Closeable
import kotlinx.android.synthetic.main.activity_main.one
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var datbase: FirebaseFirestore

    private lateinit var takeKey: View
    private lateinit var returnKey: View

    private lateinit var stateWatcher: Closeable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        takeKey = findViewById(R.id.takeKey)
        returnKey = findViewById(R.id.returnKey)
        findViewById<View>(R.id.history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        stateWatcher = KeyHolderApp.service.keyHolderState.watch {
            println(it)
            when (it) {
                Available -> {
                    findViewById<TextView>(R.id.key_status).text = "Available"
                    takeKey.visibility = View.VISIBLE
                    returnKey.visibility = View.GONE
                }
                TakenByUser -> {
                    findViewById<TextView>(R.id.key_status).text = "Taken by me"
                    takeKey.visibility = View.GONE
                    returnKey.visibility = View.VISIBLE
                }
                is Taken -> {
                    findViewById<TextView>(R.id.key_status).text = "Taken by $it"
                    takeKey.visibility = View.GONE
                    returnKey.visibility = View.GONE
                }
                is Take -> {
                    findViewById<TextView>(R.id.key_status).text = "Take request"
                    val current = hashMapOf(
                        "taken" to true,
                        "holderName" to it.user.name,
                        "holderTelegram" to it.user.telegram,
                        "pickUpTime" to it.user.takeTime
                    )
                    datbase.collection("keyholder").document("current")
                        .set(current)
                    KeyHolderApp.service.updateKeyStatus(KeyStatus(
                        true,
                        it.user.name,
                        it.user.telegram
                    ))
                }
                is Return -> {
                    println("RETURN TRIGGERED")
                    findViewById<TextView>(R.id.key_status).text = "Return request"
                    val current = hashMapOf(
                        "taken" to false
                    )
                    val historyItem = hashMapOf(
                        "name" to it.user.name,
                        "nick" to it.user.telegram,
                        "from" to Timestamp(Date(it.user.takeTime ?: 0L)),
                        "to" to Timestamp(Date(it.user.returnTime ?: 0L))
                    )
                    datbase.collection("keyholder").document("current")
                        .set(current)
                    datbase.collection("keyholder")
                        .document("history")
                        .collection("data")
                        .document()
                        .set(historyItem)

                    KeyHolderApp.service.updateKeyStatus(KeyStatus(false))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        datbase = FirebaseFirestore.getInstance()
        datbase.collection("keyholder")
            .document("current")
            .get()
            .addOnSuccessListener {
                it?.let { doc ->
                    if (doc.getBoolean("taken") == true) {
                        KeyHolderApp.service.updateKeyStatus(KeyStatus(
                            true,
                            doc.getString("holderName") ?: "",
                            doc.getString("holderTelegram") ?: "",
                            doc.getLong("pickUpTime") ?: 0L,
                            doc.getLong("returnTime") ?: 0L
                        ))
                    } else {
                        KeyHolderApp.service.updateKeyStatus(KeyStatus(false))
                    }
                }
            }

        takeKey.setOnClickListener {
            KeyHolderApp.service.takeKey()
        }

        returnKey.setOnClickListener {
            KeyHolderApp.service.returnKey()
        }
    }

    override fun onDestroy() {
        stateWatcher.close()
        super.onDestroy()
    }
}
