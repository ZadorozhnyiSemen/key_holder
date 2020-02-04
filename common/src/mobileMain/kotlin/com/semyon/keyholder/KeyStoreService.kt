package com.semyon.keyholder

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import reksoft.zadorozhnyi.keyholder.dispatcher
import reksoft.zadorozhnyi.keyholder.storage.AppStorage
import reksoft.zadorozhnyi.keyholder.storage.ApplicationContext
import reksoft.zadorozhnyi.keyholder.wrap
import kotlin.coroutines.CoroutineContext

class KeyStoreService(
    applicationContext: ApplicationContext
) : CoroutineScope {

    private val exceptionHandler = object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            _errors.offer(exception)
        }
    }

    private val storage: AppStorage = AppStorage(applicationContext)

    override val coroutineContext: CoroutineContext =
        dispatcher() + SupervisorJob() + exceptionHandler

    private val _errors = ConflatedBroadcastChannel<Throwable>()
    val errors = _errors.wrap()
}