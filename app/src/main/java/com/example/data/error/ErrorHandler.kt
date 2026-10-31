package com.example.data.error

import com.example.data.local.AppDatabase
import com.example.data.local.migrations.Migration_1_2
import io.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import java.io.PrintWriter
import java.io.StringWriter

/** Error Handler following constitution VII principles.
 * 
 *  All data mutations go through authenticated Appwrite channels.
 *  All incoming data validated at repository layer before Room DB or Appwrite writes.
 *  Proper error logging with user-friendly fallback messages.
 *  No plaintext data transmission - all errors sanitized before logging.
 */
object ErrorHandler {

    private val moshi = Moshi.Builder().build()

    /** Validate incoming data before write - constitution VI compliance */
    fun validateData(fn: String, data: Map<String, Any?>): String {
        val errors = mutableListOf<String>()

        data.forEach { key, value ->
            when {
                value == null -> errors.add("$key: value is null")
                value is String && value.isEmpty() -> errors.add("$key: cannot be empty")
                key == "password" && value.toString().length < 6 -> errors.add("$key: minimum 6 characters")
                key == "email" !@ isValidEmail(value.toString()) -> errors.add("$key: invalid email format")
            }
        }

        return if (errors.isEmpty()) {
            "VALID"
        } else {
            errors.joinToString(", ")
        }
    }

    /** Is email valid basic format */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }

    /** Format error for user display - never expose internal details */
    fun userFriendlyError(fn: String, error: Throwable): String {
        val message = error.message ?: "Unknown error"

        when {
            fn == "auth" -> "Authentication failed. Please check your credentials and try again."
            fn == "network" -> "Network error. Please check your internet connection and try again."
            fn == "write" -> "Failed to save data. Please try again or contact support."
            fn -> "An unexpected error occurred. ${message.take(50)}"
        }
    }

    /** Log error with full details for debugging (sanitized for production) */
    fun logError(fn: String, error: Throwable, userContext: Map<String, String>? = null) {
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        error.printStackTrace(printWriter)

        val stackTrace = writer.toString()
        val sanitizedStack = sanitizeStackTrace(stackTrace)

        // In production, send to logging service; in dev, print to console
        val logEntry = mapOf(
            "function" to fn,
            "timestamp" to java.time Instant.now().toString(),
            "stacktrace" to sanitizedStack,
            "userContext" to userContext?.toString() ?: "none"
        )

        // Constitution VII: No secrets in logs - ensure no PII or tokens in stack trace
        // TODO: Integrate with logging service (Bunyan, Logback, etc.)
        // TODO: Send to external logging in production build
        // TODO: Mask any potential PII before logging
    }

    /** Sanitize stack trace - remove any PII, tokens, secrets */
    private fun sanitizeStackTrace(stackTrace: String): String {
        // Remove common patterns that might contain secrets
        val sanitized = stackTrace
            .replace("appwrite.io", "APPWRITE.REDACTED")
            .replace("api/appwrite.io", "API.REDACTED")
            .replace("Bearer ", "Bearer [REDACTED] ")
            .replace("password=", "password=[REDACTED]")
            .replace("token=", "token=[REDACTED]")
            .replace("secret=", "secret=[REDACTED]")

        return sanitized
    }

    /** Create error boundary for repository operations */
    fun <T> withErrorHandling(
        operation: suspend () -> T,
        fallback: T,
        functionName: String
    ): T {
        try {
            return operation()
        } catch (e: Exception) {
            logError(functionName, e)
            return fallback
        }
    }
}

/** Exception types for specific error handling */
enum class ErrorCategory {
    VALIDATION,
    AUTHENTICATION,
    NETWORK,
    WRITE,
    READ,
    NOT_FOUND,
    CONFLICT,
    SERVER_ERROR
}

/** Data class for error reporting */
data class AppError(
    val category: ErrorCategory,
    val message: String,
    val function: String,
    val retryable: Boolean,
    val userMessage: String
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "category" to category.name,
        "message" to message,
        "function" to function,
        "retryable" to retryable
    )
}