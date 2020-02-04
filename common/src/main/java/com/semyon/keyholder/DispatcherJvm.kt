package reksoft.zadorozhnyi.keyholder

import kotlinx.coroutines.*

internal actual fun dispatcher(): CoroutineDispatcher {
    return Dispatchers.Main
}
