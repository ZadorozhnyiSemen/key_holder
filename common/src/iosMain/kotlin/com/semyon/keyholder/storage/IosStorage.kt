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
}