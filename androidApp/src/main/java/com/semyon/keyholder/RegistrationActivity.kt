package com.semyon.keyholder

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import io.ktor.utils.io.core.Closeable
import reksoft.zadorozhnyi.keyholder.storage.ApplicationContext

class RegistrationActivity : AppCompatActivity() {

    private var nameEntered = false
    private var nicknameEntered = false

    private lateinit var next: TextView
    private lateinit var name: TextView
    private lateinit var nickname: TextView

    private lateinit var stateWatcher: Closeable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_registration)

        KeyHolderApp.service = KeyStoreService(ApplicationContext(this))

        stateWatcher = KeyHolderApp.service.registrationState.watch {
            when (it) {
                RegistrationState.INITIAL -> {
                }
                RegistrationState.USER_SAVED -> openMainScreen()
                RegistrationState.ALREADY_REGISTERED -> openMainScreen()
            }
        }

        KeyHolderApp.service.checkRegistration()

        next = findViewById(R.id.done)
        name = findViewById(R.id.name_text)
        nickname = findViewById(R.id.telegram_text)

        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameEntered = s?.isNotEmpty() ?: false
                next.isEnabled = credsEntered()
            }

        })

        nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nicknameEntered = s?.isNotEmpty() ?: false
                next.isEnabled = credsEntered()
            }

        })

        next.setOnClickListener {
            KeyHolderApp.service.saveUser(name.text.toString(), nickname.text.toString())
        }


    }

    override fun onDestroy() {
        stateWatcher.close()
        super.onDestroy()
    }

    private fun credsEntered() = nameEntered && nicknameEntered

    private fun openMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}