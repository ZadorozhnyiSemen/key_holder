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
}