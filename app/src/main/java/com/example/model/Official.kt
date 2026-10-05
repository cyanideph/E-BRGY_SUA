package com.example.model

data class BarangayOfficial(
    val id: String,
    val name: String,
    val position: String,
    val roleCategory: String,
    val contactNumber: String,
    val officeHours: String,
    val committee: String,
    val isDemoRecord: Boolean = true
)
