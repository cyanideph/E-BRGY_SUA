package com.example.services

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Real-time Weather & Coastal Marine telemetry service for Barangay Sua, San Juan, Southern Leyte.
 * Powered by Open-Meteo (Free, public, zero-key, high-precision WMO forecasts & ECMWF/Copernicus marine data).
 */
object OpenMeteoService {

    private const val TAG = "OpenMeteoService"

    // Coordinates for Barangay Sua, San Juan, Southern Leyte (Cabalian Bay coast)
    const val SUA_LATITUDE = 10.3340
    const val SUA_LONGITUDE = 124.9810

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Fetches combined atmospheric forecast and marine swell telemetry for Barangay Sua.
     */
    suspend fun fetchCoastalTelemetry(): CoastalTelemetry = withContext(Dispatchers.IO) {
        var temp = 0.0
        var humidity = 0
        var rainProb = 0
        var windSpeed = 0.0
        var windDirDegrees = 0
        var weatherCode = -1
        var waveHeight = 0.0
        var swellHeight = 0.0
        var wavePeriod = 0.0
        var isLive = false

        // 1. Fetch Atmospheric Forecast (Temperature, Rain Probability, Wind Speed, Weather Code)
        try {
            val forecastUrl = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=$SUA_LATITUDE&longitude=$SUA_LONGITUDE" +
                "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m" +
                "&hourly=precipitation_probability,wind_speed_10m" +
                "&timezone=Asia%2FManila"

            val request = Request.Builder().url(forecastUrl).build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val json = JSONObject(body)
                        val current = json.optJSONObject("current")
                        if (current != null) {
                            temp = current.optDouble("temperature_2m", temp)
                            humidity = current.optInt("relative_humidity_2m", humidity)
                            weatherCode = current.optInt("weather_code", weatherCode)
                            windSpeed = current.optDouble("wind_speed_10m", windSpeed)
                            windDirDegrees = current.optInt("wind_direction_10m", windDirDegrees)
                            isLive = true
                        }
                        val hourly = json.optJSONObject("hourly")
                        if (hourly != null) {
                            val probs = hourly.optJSONArray("precipitation_probability")
                            if (probs != null && probs.length() > 0) {
                                // Take max over next 3 hours for conservative rain prediction
                                var maxP = probs.optInt(0, rainProb)
                                for (i in 0 until minOf(4, probs.length())) {
                                    val p = probs.optInt(i, 0)
                                    if (p > maxP) maxP = p
                                }
                                rainProb = maxP
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Open-Meteo forecast fetch error: ${e.message}")
        }

        // 2. Fetch Marine Sea Conditions (Wave height, Swell, Wave Period)
        try {
            val marineUrl = "https://marine-api.open-meteo.com/v1/marine?" +
                "latitude=$SUA_LATITUDE&longitude=$SUA_LONGITUDE" +
                "&current=wave_height,wave_direction,wave_period,swell_wave_height" +
                "&timezone=Asia%2FManila"

            val marineRequest = Request.Builder().url(marineUrl).build()
            httpClient.newCall(marineRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val json = JSONObject(body)
                        val current = json.optJSONObject("current")
                        if (current != null) {
                            waveHeight = current.optDouble("wave_height", waveHeight)
                            swellHeight = current.optDouble("swell_wave_height", swellHeight)
                            wavePeriod = current.optDouble("wave_period", wavePeriod)
                            isLive = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Open-Meteo marine fetch error: ${e.message}")
        }

        // Derive safety status
        val safetyStatus = when {
            waveHeight >= 2.0 || windSpeed >= 40.0 -> SeaSafety.GALE_WARNING
            waveHeight >= 1.2 || windSpeed >= 25.0 -> SeaSafety.MODERATE_SWELL
            else -> SeaSafety.SAFE_SEAS
        }

        val windCardinal = degreesToCardinal(windDirDegrees)
        val conditionDesc = wmoCodeToCondition(weatherCode)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val updateTime = timeFormat.format(Date())

        CoastalTelemetry(
            temperature = temp,
            humidity = humidity,
            rainProbability = rainProb,
            windSpeedKmH = windSpeed,
            windDirection = "$windCardinal ($windDirDegrees°)",
            waveHeightMeters = waveHeight,
            swellHeightMeters = swellHeight,
            wavePeriodSeconds = wavePeriod,
            weatherCode = weatherCode,
            conditionDescription = conditionDesc,
            safetyStatus = safetyStatus,
            highTideEstimate = "Unavailable",
            lowTideEstimate = "Unavailable",
            lastUpdated = updateTime,
            isLive = isLive
        )
    }

    private fun degreesToCardinal(degrees: Int): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((degrees + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    private fun wmoCodeToCondition(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Sunny"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Coastal Fog / Mist"
        51, 53, 55 -> "Light Passing Drizzle"
        61, 63 -> "Moderate Rain Showers"
        65 -> "Heavy Downpour"
        80, 81, 82 -> "Scattered Coastal Rain"
        95, 96, 99 -> "Thunderstorm Warning"
        else -> "Unavailable"
    }
}

enum class SeaSafety(val label: String, val advisory: String) {
    SAFE_SEAS(
        label = "SAFE SEAS",
        advisory = "Favorable conditions for small municipal motorboats and local banca fishing. Sea surface is calm to slight."
    ),
    MODERATE_SWELL(
        label = "MODERATE SWELL",
        advisory = "Caution advised for non-motorized craft and amateur fishermen along Cabalian Bay outer waters."
    ),
    GALE_WARNING(
        label = "GALE WARNING",
        advisory = "Rough seas detected. Small seacraft travel is hazardous and strictly discouraged by MDRRMO/Tanod."
    )
}

data class CoastalTelemetry(
    val temperature: Double = 0.0,
    val humidity: Int = 0,
    val rainProbability: Int = 0,
    val windSpeedKmH: Double = 0.0,
    val windDirection: String = "Unavailable",
    val waveHeightMeters: Double = 0.0,
    val swellHeightMeters: Double = 0.0,
    val wavePeriodSeconds: Double = 0.0,
    val weatherCode: Int = -1,
    val conditionDescription: String = "Unavailable",
    val safetyStatus: SeaSafety = SeaSafety.SAFE_SEAS,
    val highTideEstimate: String = "Unavailable",
    val lowTideEstimate: String = "Unavailable",
    val lastUpdated: String = "Live",
    val isLive: Boolean = false
)
