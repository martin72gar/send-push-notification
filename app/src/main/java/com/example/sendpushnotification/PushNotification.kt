package com.example.sendpushnotification

data class PushNotification(
    val data: NotificationData,
    val to: String
)