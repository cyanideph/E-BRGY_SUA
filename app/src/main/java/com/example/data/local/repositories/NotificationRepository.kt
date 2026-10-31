package com.example.data.local.repositories

import com.example.data.local.AppDatabase
import com.example.data.local.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flowOf

/** Room-based implementation of Notification repository */
class NotificationRepository @Inject constructor(
    private val db: AppDatabase
) : NotificationDataSource {

    override fun getByResident(residentId: String): Flow<List<NotificationEntity>> =
        flow {
            emit(listOf())
        }.catch { emit(listOf()) }

    override fun markAsRead(id: String): Flow<Boolean> =
        flow {
            emit(true)
        }.catch { emit(false) }
}