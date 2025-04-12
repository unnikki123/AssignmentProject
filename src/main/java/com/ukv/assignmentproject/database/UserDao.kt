package com.ukv.assignmentproject.database

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_table LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()
}
