package com.ukv.assignmentproject.data.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.ukv.assignmentproject.data.model.DeletedItemEntity
import com.ukv.assignmentproject.data.model.ItemEntity

@Database(entities = [ItemEntity::class, DeletedItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun deletedItemDao(): DeletedItemDao
}
