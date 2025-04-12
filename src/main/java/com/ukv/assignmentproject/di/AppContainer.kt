package com.ukv.assignmentproject.di

import android.content.Context
import androidx.room.Room
import com.ukv.assignmentproject.data.db.AppDatabase
import com.ukv.assignmentproject.data.network.ApiService
import com.ukv.assignmentproject.data.repository.ItemRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // --- Room DB ---
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "assignment_db" // Use an appropriate name for your database
    ).build()

    private val itemDao = database.itemDao()
    private val deletedItemDao = database.deletedItemDao()

    // --- Retrofit Setup ---
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.restful-api.dev/")  // Base URL for the API
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    // --- Repository ---
    val itemRepository: ItemRepository = ItemRepository(
        api = apiService,
        itemDao = itemDao,
        deletedItemDao = deletedItemDao
    )
}
