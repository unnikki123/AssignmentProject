/*
package com.ukv.assignmentproject.notification

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import java.io.FileInputStream

object FcmSender {

    init {
        val serviceAccount = FileInputStream("path/to/your-service-account-key.json")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        FirebaseApp.initializeApp(options)
    }

    fun sendDeleteBroadcast(id: String, name: String, dataJson: String) {
        val message = Message.builder()
            .setTopic("items_deleted")
            .setNotification(
                Notification.builder()
                    .setTitle("Item Deleted")
                    .setBody("Item '$name' was deleted.")
                    .build()
            )
            .putData("id", id)
            .putData("name", name)
            .putData("dataJson", dataJson)
            .build()

        val response = FirebaseMessaging.getInstance().send(message)
        println("Successfully sent message: $response")
    }
}
*/
