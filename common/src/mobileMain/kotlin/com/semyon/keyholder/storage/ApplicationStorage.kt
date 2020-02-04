package reksoft.zadorozhnyi.keyholder.storage

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import reksoft.zadorozhnyi.keyholder.ForecastService
import reksoft.zadorozhnyi.keyholder.wrap
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

expect class ApplicationContext

expect fun AppStorage(context: ApplicationContext): AppStorage

interface AppStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}

@UseExperimental(UnstableDefault::class)
inline operator fun <reified T> AppStorage.invoke(
    serializer: KSerializer<T>,
    crossinline block: () -> T
): ReadWriteProperty<ForecastService, T> = object : ReadWriteProperty<ForecastService, T> {
    private var currentValue: T? = null

    override fun setValue(thisRef: ForecastService, property: KProperty<*>, value: T) {
        val key = property.name
        currentValue = value
        putString(key, kotlinx.serialization.json.Json.stringify(serializer, value))
    }

    override fun getValue(thisRef: ForecastService, property: KProperty<*>): T {
        currentValue?.let { return it }

        val key = property.name

        val result = try {
            getString(key)?.let { kotlinx.serialization.json.Json.parse(serializer, it) }
        } catch (cause: Throwable) {
            null
        } ?: block()

        setValue(thisRef, property, result)
        return result
    }
}

@UseExperimental(ImplicitReflectionSerializer::class, ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
inline fun <reified T> AppStorage.live(
    crossinline initial: () -> T
): ReadOnlyProperty<ForecastService, ConflatedBroadcastChannel<T>> {
    val serializer = kotlinx.serialization.serializer<T>()

    return object : ReadOnlyProperty<ForecastService, ConflatedBroadcastChannel<T>> {
        private var channel: ConflatedBroadcastChannel<T>? = null
        private var key: String? = null

        override fun getValue(thisRef: ForecastService, property: KProperty<*>): ConflatedBroadcastChannel<T> {
            if (channel == null) {
                key = property.name
                val value = try {
                    getString(key!!)?.let { kotlinx.serialization.json.Json.parse(serializer, it) }
                } catch (_: Throwable) {
                    null
                } ?: initial()

                channel = ConflatedBroadcastChannel(value)
                channel!!.asFlow().wrap().watch {
                    putString(key!!, kotlinx.serialization.json.Json.stringify(serializer, it))
                }
            }

            return channel!!
        }
    }
}