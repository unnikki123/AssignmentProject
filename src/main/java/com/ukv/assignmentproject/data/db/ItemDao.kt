package com.ukv.assignmentproject.data.db


import androidx.room.*
import com.ukv.assignmentproject.data.model.ItemEntity

@Dao
interface ItemDao {
    @Query("SELECT * FROM ItemEntity")
    suspend fun getAll(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)
}
