package com.example.physicsproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "email") var email: String = "",
    @ColumnInfo(name = "accessLevel") var accessLevel: Level = Level.RegularUser,
    @ColumnInfo(name = "pass") var pass: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "surname") var surname: String = "",
    @ColumnInfo(name = "age") var age: Int = 0
): Serializable