package com.ukv.assignmentproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String?
)

