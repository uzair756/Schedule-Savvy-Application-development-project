package com.example.mad_cv_sp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity11 : AppCompatActivity() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var database: DatabaseReference
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main11)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        database = FirebaseDatabase.getInstance().reference.child("timetable")

        // Create notification channel
        createNotificationChannel()

        // Get current time and day
        val currentTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(currentTime.time)
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentTime.time)

        // Show initial toast message of current time and day
        val initialToastMessage = "Current Time: $formattedTime\nCurrent Day: $dayOfWeek"
        Toast.makeText(this, initialToastMessage, Toast.LENGTH_LONG).show()

        // Fetch and show database time and day, then set alarms
        fetchDatabaseTimeAndSetAlarms()

        // Start a timer to check for alarms periodically
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                fetchDatabaseTimeAndSetAlarms()
            }
        }, 0, 5000) // Check every 5 seconds, adjust as needed
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the timer when activity is destroyed
        timer.cancel()
    }

    private fun fetchDatabaseTimeAndSetAlarms() {
        val currentTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(currentTime.time)
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentTime.time)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val dbTime = childSnapshot.child("time").value.toString()
                    val dbDay = childSnapshot.child("day").value.toString()

                    val subject = childSnapshot.child("subject").value.toString()
                    if (dbDay.equals(dayOfWeek, ignoreCase = true) && dbTime == formattedTime) {
                        try {
                            val alarmTime = Calendar.getInstance().apply {
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                val time = timeFormat.parse(dbTime)
                                time?.let {
                                    timeInMillis = it.time
                                }
                            }

                            val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java).apply {
                                putExtra("subject", subject)
                            }
                            val pendingIntent = PendingIntent.getBroadcast(
                                applicationContext,
                                0,
                                alarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )

                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                alarmTime.timeInMillis,
                                pendingIntent
                            )

                            val alarmToastMessage = "Alarm set for $subject at $dbTime"
                            Toast.makeText(applicationContext, alarmToastMessage, Toast.LENGTH_LONG).show()
                            Log.d("MainActivity", alarmToastMessage)
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error setting alarm for $subject: ${e.message}")
                        }
                    } else {
                        Log.d("MainActivity", "Skipping alarm for $subject. Time or day did not match.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
