package com.tradition.mobilevtkproject.data.repository.impl

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.data.repository.UserRepository
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun checkEmailByCreation(email: String): EmailCheckResult {
        val auth = Firebase.auth
        val tempPassword = "8204e440221df2ef6e7f7efb36d7bea3c728e8f"

        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, tempPassword)
                .addOnCompleteListener { task ->
                    try {
                        if (task.isSuccessful) {
                            // Пользователь создан - email свободен
                            task.result?.user?.delete()?.addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    continuation.resume(EmailCheckResult.Available)
                                } else {
                                    continuation.resume(EmailCheckResult.Error(deleteTask.exception?.message))
                                }
                            }
                        } else {
                            when (val exception = task.exception) {
                                is FirebaseAuthUserCollisionException -> {
                                    continuation.resume(EmailCheckResult.Registered)
                                }
                                else -> {
                                    continuation.resume(EmailCheckResult.Error(exception?.message))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        continuation.resume(EmailCheckResult.Error(e.message))
                    }
                }
        }
    }

    /*override suspend fun userWithThisEmailExists(email: String): EmailCheckResult {
        val auth = Firebase.auth
        val tempPassword = "8204e440221df2ef6e7f7efb36d7bea3c728e8f"

        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, tempPassword)
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            auth.signOut()
                            continuation.resume(EmailCheckResult.Registered)
                        }
                        task.exception is FirebaseAuthInvalidUserException -> {
                            continuation.resume(EmailCheckResult.Available)
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            continuation.resume(EmailCheckResult.Registered)
                        }
                        else -> {
                            continuation.resume(EmailCheckResult.Error(task.exception?.message))
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(EmailCheckResult.Error(exception.message))
                }
        }
    }*/

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
    }*/

    sealed class EmailCheckResult : Serializable {
        object Registered : EmailCheckResult()
        object Available : EmailCheckResult()
        data class Error(val message: String?) : EmailCheckResult()
    }

}