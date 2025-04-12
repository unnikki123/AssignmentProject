package com.ukv.assignmentproject.data.network


import com.ukv.assignmentproject.data.model.ApiObject
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("objects")
    suspend fun getObjects(): List<ApiObject>

    // Notify backend about a deleted item (for sending FCM)
    @POST("notify-item-deleted")
    suspend fun sendDeleteNotification(
        @Query("id") id: String,
        @Query("name") name: String,
        @Query("dataJson") dataJson: String
    )
}
