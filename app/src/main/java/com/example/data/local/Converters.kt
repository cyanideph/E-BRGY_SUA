package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class RoomTypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { i -> array.getString(i) }
        }.getOrDefault(emptyList())
    }

    @TypeConverter
    fun fromTimeline(timeline: List<RequestTimelineEvent>?): String {
        if (timeline == null) return "[]"
        val array = JSONArray()
        timeline.forEach { event ->
            val obj = JSONObject().apply {
                put("title", event.title)
                put("description", event.description)
                put("timestamp", event.timestamp)
                put("actorName", event.actorName)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toTimeline(json: String?): List<RequestTimelineEvent> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                RequestTimelineEvent(
                    title = obj.optString("title"),
                    description = obj.optString("description"),
                    timestamp = obj.optLong("timestamp"),
                    actorName = obj.optString("actorName")
                )
            }
        }.getOrDefault(emptyList())
    }

    @TypeConverter
    fun fromRequestStatus(status: RequestStatus?): String = status?.name ?: RequestStatus.SUBMITTED.name

    @TypeConverter
    fun toRequestStatus(value: String?): RequestStatus =
        runCatching { RequestStatus.valueOf(value ?: "") }.getOrDefault(RequestStatus.SUBMITTED)

    @TypeConverter
    fun fromEmergencyType(type: EmergencyType?): String = type?.name ?: EmergencyType.BARANGAY_EMERGENCY.name

    @TypeConverter
    fun toEmergencyType(value: String?): EmergencyType =
        runCatching { EmergencyType.valueOf(value ?: "") }.getOrDefault(EmergencyType.BARANGAY_EMERGENCY)

    @TypeConverter
    fun fromEmergencyStatus(status: EmergencyStatus?): String = status?.name ?: EmergencyStatus.RECEIVED.name

    @TypeConverter
    fun toEmergencyStatus(value: String?): EmergencyStatus =
        runCatching { EmergencyStatus.valueOf(value ?: "") }.getOrDefault(EmergencyStatus.RECEIVED)
}
