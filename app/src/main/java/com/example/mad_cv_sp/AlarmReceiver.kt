package com.example.mad_cv_sp

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // This method will be called when the alarm is triggered
        val subject = intent?.getStringExtra("subject")
        context?.let {
            try {
                // Show notification
                showNotification(it, subject)
                // Play alarm sound
                playAlarmSound(it)
                // Show toast message indicating that the alarm is triggered
                val toastMessage = "Alarm triggered for $subject"
                Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // Handle any errors gracefully
                Log.e("AlarmReceiver", "Error handling alarm: ${e.message}")
            }
        }
    }

    private fun showNotification(context: Context, subject: String?) {
        // Create a notification to be shown when the alarm is triggered
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("Alarm")
            .setContentText("It's time for $subject!")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun playAlarmSound(context: Context) {
        try {
            // Create a MediaPlayer instance
            val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
            // Start playing the alarm sound
            mediaPlayer.start()
            // Release the MediaPlayer once the sound is finished playing
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
        } catch (e: Exception) {
            // Handle any errors gracefully
            Log.e("AlarmReceiver", "Error playing alarm sound: ${e.message}")
        }
    }
}
