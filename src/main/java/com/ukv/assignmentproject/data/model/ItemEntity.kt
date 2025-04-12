package com.ukv.assignmentproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dataJson: String?,
    val isModifiedLocally: Boolean = false
)


