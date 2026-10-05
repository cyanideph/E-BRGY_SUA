package com.example.model

data class BarangayProfile(
    val name: String,
    val municipality: String,
    val province: String,
    val region: String,
    val psgcCode: String,
    val municipalityPsgcCode: String,
    val provincePsgcCode: String,
    val regionPsgcCode: String,
    val type: String? = null,
    val source: String = "GIS.PH"
)
