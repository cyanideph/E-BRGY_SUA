package com.example.model

data class AuditLog(
    val id: String = "",
    val actorUid: String = "",
    val actorName: String = "",
    val actorRole: String = "",
    val action: String = "",
    val targetType: String = "",
    val targetId: String = "",
    val previousState: String? = null,
    val newState: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
