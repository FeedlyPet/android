package com.example.feedlypet.ui.auth

import android.util.Patterns
import com.example.feedlypet.R

fun validateEmail(email: String): Int? = when {
    email.isBlank() -> R.string.auth_error_email_required
    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.auth_error_email_invalid
    else -> null
}

fun validatePasswordRequired(password: String): Int? =
    if (password.isBlank()) R.string.auth_error_password_required else null

fun validatePasswordMin(password: String): Int? = when {
    password.isBlank() -> R.string.auth_error_password_required
    password.length < 8 -> R.string.auth_error_password_min
    else -> null
}

fun validateName(name: String): Int? = when {
    name.isBlank() -> R.string.auth_error_name_required
    name.length < 2 -> R.string.auth_error_name_min
    else -> null
}
