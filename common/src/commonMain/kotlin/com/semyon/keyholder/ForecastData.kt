package reksoft.zadorozhnyi.keyholder

import kotlinx.serialization.Serializable

@Serializable
class Forecast(
    var cityName: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currently: Currently?,
    val hourly: Hourly?,
    val daily: Daily?,
    val offset: Int
) {
    override fun toString(): String {
        return "$cityName $latitude $longitude"
    }
}

@Serializable
class Currently(
    val time: Long,
    val summary: String,
    val icon: String?,
    val precipIntensity: Double,
    val precipIntensityError: Double? = null,
    val precipProbability: Double,
    val precipType: String? = null,
    val precipAccumulation: Double? = null,
    val nearestStormDistance: Int? = null,
    val nearestStormBearing: Int? = null,
    val temperature: Double? = null,
    val apparentTemperature: Double?,
    val dewPoint: Double?,
    val humidity: Double?,
    val pressure: Double,
    val windSpeed: Double,
    val windGust: Double,
    val windBearing: Double,
    val cloudCover: Double?,
    val uvIndex: Double,
    val visibility: Double,
    val ozone: Double
) {
    var timezone: String? = null
}

@Serializable
class Hourly(
    val summary: String,
    val icon: String?,
    val data: List<Hour>
) {
    var timezone: String? = null

    fun populateTimeZone() {
        if (data.isNotEmpty()) {
            data.forEach {
                it.timezone = this.timezone
            }
        }
    }
}

@Serializable
class Daily(
    val summary: String,
    val icon: String?,
    val data: List<Day>
) {
    var timezone: String? = null

    fun populateTimeZone() {
        if (data.isNotEmpty()) {
            data.forEach {
                it.timezone = this.timezone
            }
        }
    }
}

@Serializable
class Hour(
    val time: Long,
    val summary: String,
    val icon: String?,
    val precipIntensity: Double,
    val precipProbability: Double,
    val precipType: String? = null,
    val precipAccumulation: Double? = null,
    val temperature: Double? = null,
    val apparentTemperature: Double?,
    val dewPoint: Double?,
    val humidity: Double?,
    val pressure: Double,
    val windSpeed: Double,
    val windGust: Double,
    val windBearing: Double,
    val cloudCover: Double?,
    val uvIndex: Double,
    val visibility: Double,
    val ozone: Double
) {
    var timezone: String? = null
}

@Serializable
class Day(
    val time: Long,
    val summary: String,
    val icon: String?,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val moonPhase: Double,
    val precipIntensity: Double,
    val precipIntensityMax: Double,
    val precipIntensityMaxTime: Double,
    val precipProbability: Double,
    val precipType: String? = null,
    val precipAccumulation: Double? = null,
    val temperatureHigh: Double,
    val temperatureHighTime: Double,
    val temperatureLow: Double,
    val temperatureLowTime: Double,
    val apparentTemperatureHigh: Double?,
    val apparentTemperatureHighTime: Double?,
    val apparentTemperatureLow: Double?,
    val apparentTemperatureLowTime: Double?,
    val dewPoint: Double?,
    val humidity: Double?,
    val pressure: Double,
    val windSpeed: Double,
    val windGust: Double,
    val windGustTime: Double,
    val windBearing: Double,
    val cloudCover: Double?,
    val uvIndex: Double,
    val uvIndexTime: Double,
    val visibility: Double,
    val ozone: Double,
    val temperatureMin: Double,
    val temperatureMinTime: Double,
    val temperatureMax: Double,
    val temperatureMaxTime: Double,
    val apparentTemperatureMin: Double?,
    val apparentTemperatureMinTime: Double?,
    val apparentTemperatureMax: Double?,
    val apparentTemperatureMaxTime: Double?
) {
    var timezone: String? = null
}

@Serializable
data class GeoData(val cityName: String, val lat: Double, val long: Double)