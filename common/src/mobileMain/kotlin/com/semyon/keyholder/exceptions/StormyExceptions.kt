package reksoft.zadorozhnyi.keyholder.exceptions

open class StormyException(msg: String) : Throwable(msg)
class UnknownLatitudeException : StormyException("Unknown latitude")
class UnknownLongitudeException : StormyException("Unknown longitude")