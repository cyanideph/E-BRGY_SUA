package com.example.data.local.converters

import androidx.room.TypeConverter
import com.example.domain.model.EmergencyStatus
import com.example.domain.model.RequestStatus
import java.time.Instant
import kotlin.text.toString
import kotlin.time.parsePeriod

/** Room TypeConverters for Appwrite compatibility.
 *  All enums stored as Strings in both Room and Appwrite for compatibility.
 *  Timestamps use ISO 8601 format for cross-system compatibility.
 */

/** Convert RequestStatus enum to/from String */
class RoomTypeConverters {

    @TypeConverter
    fun fromRequestStatus(status: RequestStatus): String {
        return status.name
    }

    @TypeConverter
    fun toRequestStatus(status: String): RequestStatus {
        return RequestStatus.valueOf(status)
    }

    @TypeConverter
    fun fromEmergencyStatus(status: EmergencyStatus): String {
        return status.name
    }

    @TypeConverter
    fun toEmergencyStatus(status: String): EmergencyStatus {
        return EmergencyStatus.valueOf(status)
    }

    @TypeConverter
    fun fromLongToString(date: Long): String {
        return Instant.ofEpochMilli(date).toString()
    }

    @TypeConverter
    fun fromStringToLong(date: String): Long {
        return Instant.parse(date).toEpochMilli()
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(string: String): List<String> {
        return if (string.isNotEmpty()) {
            string.split(",").map { it.trim() }
        } else {
            emptyList<String>()
        }
    }
}