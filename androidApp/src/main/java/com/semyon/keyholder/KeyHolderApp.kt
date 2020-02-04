package com.semyon.keyholder

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class KeyHolderApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        @Volatile
        lateinit var service: KeyStoreService
    }
}