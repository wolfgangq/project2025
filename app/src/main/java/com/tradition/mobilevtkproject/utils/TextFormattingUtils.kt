package com.tradition.mobilevtkproject.utils

object TextFormattingUtils {

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isPassValid(pass: String): Boolean {
        return (pass.length >= 8)
    }
}