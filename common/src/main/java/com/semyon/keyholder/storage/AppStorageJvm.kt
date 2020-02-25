package reksoft.zadorozhnyi.keyholder.storage

import android.content.Context
import android.preference.PreferenceManager

actual fun AppStorage(context: ApplicationContext): AppStorage = AndroidStorage(context.activity)

internal class AndroidStorage(
    context: Context
) : AppStorage {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun getBoolean(key: String): Boolean? {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getLong(key: String): Long? {
        return sharedPreferences.getLong(key, 0L)
    }

    override fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
}