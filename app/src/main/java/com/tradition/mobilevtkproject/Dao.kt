package com.tradition.mobilevtkproject

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    fun insertItem(item: User)
    @Query("SELECT * FROM USERS")
    fun getAllItem(): Flow<List<User>>
    @Query("SELECT * FROM USERS WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    @Query("SELECT * FROM USERS WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int?): User?
    @Query("SELECT * FROM USERS WHERE accessLevel = :accessLevel")
    suspend fun getUsersByRole(accessLevel: Level): List<User>
}