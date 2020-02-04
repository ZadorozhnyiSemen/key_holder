package reksoft.zadorozhnyi.keyholder

import reksoft.zadorozhnyi.keyholder.storage.ApplicationContext

actual class LocationService actual constructor(
    context: ApplicationContext
) {
    actual fun getDeviceLocation(): Pair<Double, Double>? {
        return 60.000573 to 30.334711
    }

}