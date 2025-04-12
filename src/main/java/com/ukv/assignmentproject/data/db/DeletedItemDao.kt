package com.ukv.assignmentproject.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ukv.assignmentproject.data.model.DeletedItemEntity

@Dao
interface DeletedItemDao {

    // Insert a deleted item into the database, replacing any existing entry with the same ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deletedItem: DeletedItemEntity)

    // Fetch all deleted items (with full details like name, dataJson, etc.)
    @Query("SELECT * FROM DeletedItemEntity")
    suspend fun getAll(): List<DeletedItemEntity>

    // Fetch only the IDs of deleted items (to track deletion status)
    @Query("SELECT id FROM DeletedItemEntity")
    suspend fun getAllIds(): List<String>

    // Optionally, you can add a method to delete a specific deleted item (if needed)
    @Query("DELETE FROM DeletedItemEntity WHERE id = :id")
    suspend fun deleteById(id: String)

    // You can also add other query methods if needed (for example, to remove old items)
    @Query("DELETE FROM DeletedItemEntity WHERE id IN (:ids)")
    suspend fun deleteItemsByIds(ids: List<String>)
}
