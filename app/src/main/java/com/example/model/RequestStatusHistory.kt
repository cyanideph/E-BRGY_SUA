package com.example.model

data class RequestStatusHistory(
    val id: String = "",
    val requestId: String = "",
    val status: String = "",
    val remarks: String = "",
    val changedBy: String = "",
    val changedAt: String = ""
)
