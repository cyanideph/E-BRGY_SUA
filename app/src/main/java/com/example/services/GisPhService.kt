package com.example.services

import com.example.model.BarangayProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val GIS_PH_BASE_URL = "https://api.gis.ph/"

@JsonClass(generateAdapter = true)
data class GisPhBarangayResponse(
    val data: List<GisPhBarangay> = emptyList(),
    val error: Any? = null
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

    suspend fun fetchSuaProfile(): Result<BarangayProfile> = runCatching {
        val response = api.listBarangays(
            province = "Southern Leyte",
            municipality = "San Juan",
            name = "Sua"
        )
        response.data.firstOrNull()?.toProfile()
            ?: error("Barangay Sua was not found in the GIS.PH administrative dataset.")
    }
}
