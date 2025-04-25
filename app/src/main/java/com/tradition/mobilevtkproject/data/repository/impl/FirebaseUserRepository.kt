package com.tradition.mobilevtkproject.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.data.repository.UserRepository
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(private val firestore: FirebaseFirestore = Firebase.firestore) : UserRepository {
    override suspend fun getUserInfo(id: String): Map<String, Any>? {
        return try {
            firestore.collection("users")
                .whereEqualTo("authId", id)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.data
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun userWithThisEmailExists(email: String): Boolean {
        return try {
            !firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
                .isEmpty
        } catch (e: Exception) {
            false
        }
    }

    /*override suspend fun userWithThisEmailExists(email: String): EmailCheckResult {
        return try {
            val result = Firebase.auth.fetchSignInMethodsForEmail(email).await()
            val signInMethods = result.signInMethods

            if (signInMethods?.isNotEmpty() == true) {
                EmailCheckResult.Registered
            } else {
                EmailCheckResult.Available
            }
        } catch (e: Exception) {
            EmailCheckResult.Error(e.message)
        }
    }

    sealed class EmailCheckResult : Serializable {
        object Registered : EmailCheckResult()
        object Available : EmailCheckResult()
        data class Error(val message: String?) : EmailCheckResult()
    }*/

}