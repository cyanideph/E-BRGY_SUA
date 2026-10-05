package com.example.services

import android.content.Context
import android.net.Uri
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.models.InputFile
import java.io.File

/** Private resident document upload through the authenticated Appwrite session. */
object ResidentFileService {
    suspend fun upload(
        context: Context,
        uri: Uri,
        displayName: String,
        userId: String
    ): Result<String> = runCatching {
        val safeName = displayName.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val cacheFile = File(context.cacheDir, "resident_upload_" + System.currentTimeMillis() + "_" + safeName)
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Unable to open selected document." }
            cacheFile.outputStream().use { output -> input.copyTo(output) }
        }
        try {
            val file = Appwrite.storage().createFile(
                bucketId = Appwrite.RESIDENT_FILES_BUCKET_ID,
                fileId = ID.unique(),
                file = InputFile.fromPath(cacheFile.absolutePath),
                permissions = listOf(Permission.read(Role.user(userId)), Permission.write(Role.user(userId)))
            )
            file.id
        } finally {
            cacheFile.delete()
        }
    }
}
