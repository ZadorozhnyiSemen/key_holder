package reksoft.zadorozhnyi.keyholder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import reksoft.zadorozhnyi.keyholder.storage.ApplicationContext

actual class LocationService actual constructor(
    private val context: ApplicationContext
) {
    actual fun getDeviceLocation(): Pair<Double, Double>? {
        val manager = context.activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(context.activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context.activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                42
            )
            return null
        } else {
            val location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            return location.latitude to location.longitude
        }
    }
}