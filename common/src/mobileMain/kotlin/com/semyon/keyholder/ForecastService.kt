package reksoft.zadorozhnyi.keyholder

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import reksoft.zadorozhnyi.keyholder.storage.AppStorage
import reksoft.zadorozhnyi.keyholder.storage.ApplicationContext
import reksoft.zadorozhnyi.keyholder.storage.live
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class ForecastService(
    applicationContext: ApplicationContext
) : CoroutineScope {

    private val exceptionHandler = object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            _errors.offer(exception)
        }
    }

    private val locationService = LocationService(applicationContext)
    private val storage: AppStorage = AppStorage(applicationContext)

    override val coroutineContext: CoroutineContext =
        dispatcher() + SupervisorJob() + exceptionHandler

    private val _forecast = ConflatedBroadcastChannel<Forecast>()
    private val _userForecasts = ConflatedBroadcastChannel<HashMap<String, Forecast>>()
    private val _current = ConflatedBroadcastChannel<Currently?>()
    private val _daily = ConflatedBroadcastChannel<Daily?>()
    private val _hourly = ConflatedBroadcastChannel<Hourly?>()
    private val _errors = ConflatedBroadcastChannel<Throwable>()

    private val _userGeoPoints = ConflatedBroadcastChannel<HashMap<String, Forecast>>()

    val forecast = _forecast.wrap()
    val userForecast = _userForecasts.wrap()
    val current = _current.wrap()
    val daily = _daily.wrap()
    val hourly = _hourly.wrap()
    val errors = _errors.wrap()

    private val _userCityList by storage.live { mutableListOf<GeoData>() }


    init {
        launch {
            try {
                locationService.getDeviceLocation().let {
                    loadForecast(it?.first.toString(), it?.second.toString())
                }
            } catch (e: Throwable) {
                _errors.offer(e)
            }
        }
        launch {
            val userCities = _userCityList.value
            try {
                loadForecast(userCities)
            } catch (e: Throwable) {
                _errors.offer(e)
            }
        }
    }

    fun refreshDeviceLocation() {
        launch {
            locationService.getDeviceLocation().let {
                loadForecast(it?.first.toString(), it?.second.toString())
            }
        }
    }

    fun refresh() {
        launch {
            ClientApi.loadForecast().apply {
                _forecast.offer(this)
                _current.offer(this.currently)
                _daily.offer(this.daily)
                _hourly.offer(this.hourly)
            }
        }
    }

    fun addLocation(city: GeoData) {
        println("Added location $city")
        launch {
            try {
                val current = _userCityList.value
                current.add(city)
                _userCityList.offer(current)
                loadForecast(current)
            } catch (e: Throwable) {
                _errors.offer(e)
            }
        }
    }

    fun removeLocation(city: GeoData) {
        launch {
            val current = _userCityList.value
            if (city in current) {
                current.remove(city)
                _userCityList.offer(current)
            }
        }
    }

    fun changeLocation(lat: String, long: String) {
        launch {
            ClientApi.latitude = lat
            ClientApi.longitude = long
            refresh()
        }
    }

    private suspend fun loadForecast(lat: String, long: String) {
        println("Loading forecasts for city: [lat: $lat, long: $long]")
        ClientApi.loadForecast(lat, long).apply {
            _forecast.offer(this)
            _current.offer(this.currently)
            _daily.offer(this.daily)
            _hourly.offer(this.hourly)
        }
    }

    private suspend fun loadForecast(cities: List<GeoData>) {
        println("Loading forecasts for cities: $cities")
        if (cities.isEmpty()) return

        val result = mutableMapOf<String, Forecast>()

        cities.forEach {
            ClientApi.loadForecast(it.lat.toString(), it.long.toString()).apply {
                result[it.cityName] = this.apply {
                    this.cityName = it.cityName
                }
            }
        }

        _userForecasts.offer(HashMap(result))
    }
}