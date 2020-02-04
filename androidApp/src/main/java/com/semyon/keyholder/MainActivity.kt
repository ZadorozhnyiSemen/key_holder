package com.semyon.keyholder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var datbase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        datbase = FirebaseFirestore.getInstance()
        datbase.collection("keyholder")
            .document("current")
            .get()
            .addOnSuccessListener {
                it?.let { doc ->
                    println(doc.data)
                }
            }
    }
}

data class Current(
    val holderName: String,
    val holderTelegram: String,
    val pickUpTime: Date?,
    val returnTime: Date?
)