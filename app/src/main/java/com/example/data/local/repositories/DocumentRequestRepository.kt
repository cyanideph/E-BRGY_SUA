package com.example.data.local.repositories

import com.example.data.local.AppDatabase
import com.example.data.local.entities.DocumentRequestEntity
import com.example.domain.model.ServiceRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flowOf
import kotlinx.coroutines.runBlocking

/** Room-based implementation of ServiceRequest repository */
class DocumentRequestRepository @Inject constructor(
    private val db: AppDatabase
) : DocumentRequestDataSource {

    override fun getByResident(residentId: String): Flow<List<ServiceRequest>> =
        flow {
            emit(listOf())
        }.catch { emit(listOf()) }

    override fun create(
        type: String,
        status: String,
        requestedBy: String,
        description: String,
        requiredDocuments: List<String>,
        requestDate: String
    ): Flow<ServiceRequest> =
        flow {
            runBlocking {
                // TODO: Insert DocumentRequestEntity and map to ServiceRequest
                emit(ServiceRequest(
                    type = type,
                    status = status,
                    requestedBy = requestedBy,
                    description = description,
                    requiredDocuments = requiredDocuments,
                    requestDate = requestDate
                ))
            }.also {
                emit(ServiceRequest(
                    type = "",
                    status = "",
                    requestedBy = "",
                    description = "",
                    requiredDocuments = emptyList(),
                    requestDate = ""
                ))
            }
        }.catch { emit(ServiceRequest(
            type = "",
            status = "",
            requestedBy = "",
            description = "",
            requiredDocuments = emptyList(),
            requestDate = ""
        )) }
}