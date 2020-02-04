package reksoft.zadorozhnyi.keyholder

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import reksoft.zadorozhnyi.keyholder.exceptions.UnknownLatitudeException
import reksoft.zadorozhnyi.keyholder.exceptions.UnknownLongitudeException
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ClientApi {
    val backendUrl = "https://api.forecast.io/forecast"
    val apiKey = "4047a59817eabe74153da1798525bdd3"
    lateinit var latitude: String
    lateinit var longitude: String

    val endpoint
        get() = "$backendUrl/$apiKey/$latitude,$longitude?exclude=flags,minutely,alerts&units=auto"

    private val client = HttpClient {
        install(JsonFeature)
    }

    suspend fun loadForecast(lat: String? = latitude, long: String? = longitude): Forecast {
        latitude = lat ?: throw UnknownLatitudeException()
        longitude = long ?: throw UnknownLongitudeException()
        return client.get<Forecast>(endpoint).apply {
            println(endpoint)
            currently?.timezone = timezone
            daily?.timezone = timezone
            hourly?.timezone = timezone
            daily?.populateTimeZone()
            hourly?.populateTimeZone()
        }
    }
}