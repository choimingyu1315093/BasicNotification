package com.example.basicnotification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

class MainActivity : AppCompatActivity() {
    private val channelID = "com.example.basicnotification.channel1"
    private var notificationManager: NotificationManager? = null
    private val KEY_REPLY = "key_reply"

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            showNotification()
        }else {
            Log.d("TAG", "Notification permission denied.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "DemoChannel", "this is a demo")

        val btnNotification = findViewById<Button>(R.id.btnNotification)
        btnNotification.setOnClickListener {
            permissionNotification()
        }
    }

    private fun permissionNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }else {
                showNotification()
            }
        }else {
            showNotification()
        }
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }

            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(){
        val notificationId = 45
        //터치이벤트
        val tapResultIntent = Intent(this, SecondActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_MUTABLE
        )

        //액션버튼
        val intent2 = Intent(this, DetailActivity::class.java)
        val pendingIntent2 = PendingIntent.getActivity(
            this,
            0,
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val action2 = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "DetailActivity", pendingIntent2).build()

        val intent3 = Intent(this, SettingActivity::class.java)
        val pendingIntent3 = PendingIntent.getActivity(
            this,
            0,
            intent3,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val action3 = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "SettingActivity", pendingIntent3).build()

        //답장
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
            setLabel("Insert your name here")
            build()
        }
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "Reply", pendingIntent).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Demo Title")
            .setContentText("This is a demo notification")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(action2)
            .addAction(action3)
            .addAction(replyAction)
            .build()
        notificationManager?.notify(notificationId, notification)

    }
}