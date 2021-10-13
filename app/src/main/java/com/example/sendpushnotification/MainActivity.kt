package com.example.sendpushnotification

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sendpushnotification.databinding.ActivityMainBinding
import com.example.sendpushnotification.databinding.ActivityMainBinding.inflate
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            binding.etToken.setText(it)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.btnSend.setOnClickListener{
            val title = binding.etTitle.text.toString()
            val message = binding.etMessage.text.toString()
            val recipientToken = binding.etToken.text.toString()

            if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, message),
                    recipientToken
                ).also {
                    sendNotification(it)
                }
                Toast.makeText(this@MainActivity, "Sending push notification ...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill your title and message for push notification", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)

            if (response.isSuccessful) {
//                Log.d(TAG, "sendNotification response: ${Gson().toJson(response)}")
                Log.d(TAG, "sendNotification response: $response")
            } else {
                Log.e(TAG, "sendNotification error: "+response.errorBody().toString())
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "sendNotification: "+e.toString())
        }
    }
}