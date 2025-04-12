package com.ukv.assignmentproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeletedItemEntity(
    @PrimaryKey val id: String,
    val name: String, // Item name
    val dataJson: String? // Store the JSON data as is
)
