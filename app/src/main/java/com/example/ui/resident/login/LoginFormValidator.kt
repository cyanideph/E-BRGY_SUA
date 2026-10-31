package com.example.ui.resident.login

import androidx.compose.material3.outlinedtextfield.outlinedTextFieldErrorValue

/** Login Form Validator - validates login form inputs.
 * 
 *  Follows constitution principle VI: data integrity.
 *  All incoming data validated at the repository layer before writes.
 */
object LoginFormValidator {

    /** Validate email format */
    fun validateEmail(email: String): String {
        if (email.isEmpty()) {
            return "Email is required"
        }
        if (!email.contains("@")) {
            return "Email must contain @"
        }
        if (!email.contains(".")) {
            return "Email must contain a domain"
        }
        if (email.length < 6) {
            return "Email is too short"
        }
        return "" // valid
    }

    /** Validate password strength */
    fun validatePassword(password: String): String {
        if (password.isEmpty()) {
            return "Password is required"
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }
        return "" // valid
    }

    /** Validate both fields - returns list of error messages */
    fun validate(email: String, password: String): List<String> {
        val errors = mutableListOf<String>()
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        if (emailError.isNotEmpty()) {
            errors.add(emailError)
        }
        if (passwordError.isNotEmpty()) {
            errors.add(passwordError)
        }

        return errors
    }
}