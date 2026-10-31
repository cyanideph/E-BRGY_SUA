package com.example.data.provider.appwrite

import com.example.data.remote.AppwriteDataSource
import com.example.domain.model.Resident
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine

/** AppWrite Auth Provider - handles authentication flows.
 * 
 *  Follows constitution principles:
 *  - III: Appwrite as exclusive backend
 *  - VI: Data integrity and authentication security
 *  - VII: Environment-based secrets, no hardcoded credentials
 */
class AuthProvider @Inject constructor(
    private val appwriteDataSource: AppwriteDataSource
) {

    /** Login with email and password */
    fun login(email: String, password: String): Flow<Resident?> =
        flow {
            suspendCancellableCoroutine { cotc ->
                appwriteDataSource.signInWithEmail(email, password).collect { result ->
                    when (result) {
                        is String -> {
                            if (result.isNotEmpty()) {
                                // In production, fetch resident data from Appwrite
                                // using the authenticated session
                                cotc.resume(Resident(
                                    id = result,
                                    fullName = "",
                                    address = "",
                                    birthDate = "",
                                    age = 0,
                                    profileImageId = null,
                                    status = "ACTIVE",
                                    dateCreated = "",
                                    lastLogin = ""
                                ))
                            } else {
                                cotc.resume(null)
                            }
                        }
                        else -> cotc.resume(null)
                    }
                }
            }.catch { /* log error - constitution VII: no secrets in logs */ }
        }

    /** Sign out */
    fun logout(): Flow<Boolean> =
        flow {
            appwriteDataSource.signOut().collect { result ->
                cotc.resume(result)
            }
        }.catch { cotc.resume(false) }

    /** Get current resident from auth state */
    fun getCurrentResident(): Flow<Resident?> =
        flow {
            // Observe auth state and fetch resident data
            cotc.resume(null)
        }.catch { cotc.resume(null) }
}