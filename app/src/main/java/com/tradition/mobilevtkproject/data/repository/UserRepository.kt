package com.tradition.mobilevtkproject.data.repository

import com.tradition.mobilevtkproject.data.repository.impl.FirebaseUserRepository

interface UserRepository {
    suspend fun getUserInfo(id: String): Map<String, Any>?
    suspend fun userWithThisEmailExists(email: String): Boolean
    suspend fun checkEmailByCreation(email: String): FirebaseUserRepository.EmailCheckResult
}
