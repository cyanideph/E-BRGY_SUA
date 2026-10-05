package com.example.model

data class Facility(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val hours: String? = null,
    val emergencyAvailable: Boolean = false,
    val active: Boolean = true
)
