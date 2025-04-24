package com.tradition.mobilevtkproject.data.repository

interface UserRepository {
    suspend fun getUserInfo(id: String): Map<String, Any>?
    suspend fun userWithThisEmailExists(email: String): Boolean
}
