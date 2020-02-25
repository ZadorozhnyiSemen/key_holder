package reksoft.zadorozhnyi.keyholder.storage

import platform.Foundation.NSUserDefaults

actual class ApplicationContext

actual fun AppStorage(context: ApplicationContext): AppStorage = IosStorage()

internal class IosStorage : AppStorage {
    private val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults()

    override fun putString(key: String, value: String) {
        delegate.setObject(value, key)
    }

    override fun getString(key: String): String? = delegate.stringForKey(key)

    private fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null

    override fun getBoolean(key: String): Boolean? {
        return delegate.boolForKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        delegate.setBool(value, key)
    }

    override fun getLong(key: String): Long? {
        return delegate.doubleForKey(key).toLong()
    }

    override fun setLong(key: String, value: Long) {
        delegate.setDouble(value.toDouble(), key)
    }
}