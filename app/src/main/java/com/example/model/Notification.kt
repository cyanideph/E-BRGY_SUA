package com.example.model

data class BarangayNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val category: String = "Service Request",
    val priority: String = "Normal",
    val referenceId: String? = null
)
