package com.semyon.keyholder

import io.ktor.util.date.GMTDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
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
    private val _registrationState = ConflatedBroadcastChannel<RegistrationState>()
    private val _keyState = ConflatedBroadcastChannel<KeyHolderState>()
    private val _historyState = ConflatedBroadcastChannel<HistoryState>()

    val errors = _errors.wrap()
    val registrationState = _registrationState.wrap()
    val keyHolderState = _keyState.wrap()
    val historyState = _historyState.wrap()

    fun checkRegistration() {
        launch {
            if (storage.getBoolean("registered") == true) {
                _registrationState.offer(RegistrationState.ALREADY_REGISTERED)
            }
        }
    }

    fun saveUser(name: String, nickname: String) {
        println("Saving [$name, $nickname]")

        launch {
            storage.putString("name", name)
            storage.putString("nickname", nickname)
            storage.putBoolean("registered", true)
            _registrationState.offer(RegistrationState.USER_SAVED)
        }
    }

    fun updateKeyStatus(status: KeyStatus) {
        launch {
            if (status.taken) {
                if (storage.getString("name") == status.holderName) {
                    _keyState.offer(TakenByUser)
                } else {
                    _keyState.offer(Taken(status.holderName, status.holderTelegram, status.pickUpTime
                        ?: GMTDate().timestamp))
                }
            } else {
                _keyState.offer(Available)
            }
        }
    }

    fun takeKey() {
        launch {
            val takeTime = GMTDate().timestamp
            val user = User(
                storage.getString("name") ?: "",
                storage.getString("nickname") ?: "",
                takeTime,
                null
            )
            storage.setLong("takeTime", takeTime)
            _keyState.offer(Take(user))
        }
    }

    fun returnKey() {
        launch {
            val user = User(
                storage.getString("name") ?: "",
                storage.getString("nickname") ?: "",
                storage.getLong("takeTime") ?: 0L,
                GMTDate().timestamp
            )
            _keyState.offer(Return(user))
        }
    }

    fun showHistoory(items: List<History>) {
        launch {
            _historyState.offer(HistoryLoaded(items))
        }
    }
}

enum class RegistrationState {
    INITIAL,
    USER_SAVED,
    ALREADY_REGISTERED
}

sealed class KeyHolderState
object Available : KeyHolderState()
object TakenByUser : KeyHolderState()
data class Taken(val name: String, val nickname: String, val since: Long) : KeyHolderState()
data class Take(val user: User) : KeyHolderState()
data class Return(val user: User) : KeyHolderState()

data class User(
    val name: String,
    val telegram: String,
    val takeTime: Long?,
    val returnTime: Long?
)

data class KeyStatus(
    val taken: Boolean,
    val holderName: String = "",
    val holderTelegram: String = "",
    val pickUpTime: Long? = null,
    val returnTime: Long = 0
)

sealed class HistoryState
data class HistoryLoaded(val data: List<History>) : HistoryState()

data class History(
    val name: String,
    val nick: String,
    val from: String,
    val to: String
)