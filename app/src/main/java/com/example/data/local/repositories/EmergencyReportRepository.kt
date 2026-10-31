package com.example.data.local.repositories

import com.example.data.local.AppDatabase
import com.example.data.local.entities.EmergencyReportEntity
import com.example.domain.model.EmergencyReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flowOf
import kotlinx.coroutines.runBlocking

/** Room-based implementation of EmergencyReport repository */
class EmergencyReportRepository @Inject constructor(
    private val db: AppDatabase
) : EmergencyReportDataSource {

    override fun getActive: Flow<List<EmergencyReport>> =
        flow {
            emit(listOf())
        }.catch { emit(listOf()) }

    override fun getByResident(residentId: String): Flow<List<EmergencyReport>> =
        flow {
            emit(listOf())
        }.catch { emit(listOf()) }

    override fun create(
        type: String,
        severity: String,
        reportedBy: String,
        location: String,
        description: String,
        reportedAt: String
    ): Flow<EmergencyReport> =
        flow {
            runBlocking {
                // TODO: Insert EmergencyReportEntity and map to domain model
                emit(EmergencyReport(
                    type = type,
                    severity = severity,
                    reportedBy = reportedBy,
                    location = location,
                    description = description,
                    reportedAt = reportedAt
                ))
            }.also {
                emit(EmergencyReport(
                    type = "",
                    severity = "",
                    reportedBy = "",
                    location = "",
                    description = "",
                    reportedAt = ""
                ))
            }
        }.catch { emit(EmergencyReport(
            type = "",
            severity = "",
            reportedBy = "",
            location = "",
            description = "",
            reportedAt = ""
        )) }
}