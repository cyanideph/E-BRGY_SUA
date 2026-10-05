package com.example.services

import com.example.model.BarangayProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val GIS_PH_BASE_URL = "https://api.gis.ph/"

private val VERIFIED_SUA_FALLBACK = BarangayProfile(
    name = "Sua",
    municipality = "San Juan",
    province = "Southern Leyte",
    region = "Eastern Visayas",
    psgcCode = "0806414023",
    municipalityPsgcCode = "086414000",
    provincePsgcCode = "086400000",
    regionPsgcCode = "080000000",
    type = "Barangay",
    source = "PSA PSGC-verified configured identity"
)

@JsonClass(generateAdapter = true)
data class GisPhBarangayResponse(
    val data: List<GisPhBarangay> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GisPhBarangay(
    val id: Long? = null,
    val name: String,
    val municipality: String,
    val province: String,
    val region: String,
    val fullName: String? = null,
    val code: String,
    @Json(name = "lCode") val municipalityPsgcCode: String? = null,
    @Json(name = "pCode") val provincePsgcCode: String? = null,
    @Json(name = "rCode") val regionPsgcCode: String? = null,
    val type: String? = null
) {
    fun toProfile(): BarangayProfile = BarangayProfile(
        name = name,
        municipality = municipality,
        province = province,
        region = region,
        psgcCode = code,
        municipalityPsgcCode = municipalityPsgcCode.orEmpty(),
        provincePsgcCode = provincePsgcCode.orEmpty(),
        regionPsgcCode = regionPsgcCode.orEmpty(),
        type = type
    )
}

interface GisPhApi {
    @GET("v1/barangays")
    suspend fun listBarangays(
        @Query("province") province: String,
        @Query("municipality") municipality: String,
        @Query("name") name: String,
        @Query("limit") limit: Int = 1
    ): GisPhBarangayResponse
}

object GisPhService {
    private val api: GisPhApi by lazy {
        Retrofit.Builder()
            .baseUrl(GIS_PH_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GisPhApi::class.java)
    }

    /**
     * Live GIS.PH lookup with a fixed, verified app-identity fallback.
     * The fallback identifies the configured service area; it does not claim
     * that the device's GPS position was independently verified.
     */
    suspend fun fetchSuaProfile(): Result<BarangayProfile> {
        return runCatching {
            val response = api.listBarangays(
                province = "Southern Leyte",
                municipality = "San Juan",
                name = "Sua"
            )
            response.data.firstOrNull()?.toProfile()
                ?: error("Barangay Sua was not found in the live administrative dataset.")
        }.recoverCatching {
            VERIFIED_SUA_FALLBACK
        }
    }
}
