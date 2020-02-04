package reksoft.zadorozhnyi.keyholder

import reksoft.zadorozhnyi.keyholder.storage.ApplicationContext

expect class LocationService(context: ApplicationContext) {
    fun getDeviceLocation(): Pair<Double, Double>?
}