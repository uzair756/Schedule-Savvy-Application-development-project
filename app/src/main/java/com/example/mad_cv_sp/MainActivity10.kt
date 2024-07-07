package com.example.mad_cv_sp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.Locale
import androidx.cardview.widget.CardView

class MainActivity10 : AppCompatActivity() {

    private lateinit var databaseTimetable: DatabaseReference
    private lateinit var databaseMessages: DatabaseReference
    private lateinit var alarmContainer: LinearLayout

    companion object {
        private const val TAG = "MainActivity10"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main10) // Ensure this matches your layout file name

        // Initialize Firebase Database references
        databaseTimetable = FirebaseDatabase.getInstance().reference.child("timetable")
        databaseMessages = FirebaseDatabase.getInstance().reference.child("messages")

        // Reference to the alarm container
        alarmContainer = findViewById(R.id.alarm_container)

        // Fetch timetable data and display it
        fetchTimetableData()

        // Monitor messages for "no class today"
        monitorMessages()

        // Button to add a new alarm
        val addAlarmButton: Button = findViewById(R.id.add_alarm_button)
        addAlarmButton.setOnClickListener {
            showAddAlarmDialog()
        }
    }

    private fun fetchTimetableData() {
        databaseTimetable.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                alarmContainer.removeAllViews() // Clear previous data
                for (snapshot in dataSnapshot.children) {
                    val subject = snapshot.child("subject").value?.toString() ?: ""
                    val day = snapshot.child("day").value?.toString() ?: ""
                    val time = snapshot.child("time").value?.toString() ?: ""

                    if (subject.isNotEmpty() && day.isNotEmpty() && time.isNotEmpty()) {
                        addAlarmToUI(subject, day, time)
                    } else {
                        showToast("Missing data for timetable entry: $snapshot")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Failed to read timetable data: ${databaseError.message}")
            }
        })
    }

    private fun monitorMessages() {
        databaseMessages.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.child("index").value?.toString()?.trim()?.lowercase(Locale.ROOT)
                if (message != null && message.startsWith("no ")) {
                    val parts = message.split(" ")
                    if (parts.size >= 4) { // Ensure correct parsing
                        val subject = parts[1] + " " + parts[2] // "no <subject> today"
                        val day = dataSnapshot.child("day").value?.toString() ?: "" // Fetch the actual day from the database
                        if (day.isNotEmpty()) {
                            deleteTimetableEntry(subject, day)
                        } else {
                            showToast("Invalid day format in the message database: $message")
                        }
                    } else {
                        showToast("Invalid message format: $message")
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Failed to monitor messages: ${databaseError.message}")
            }
        })
    }

    private fun deleteTimetableEntry(subject: String, day: String) {
        showToast("Deleting timetable entry for subject: $subject, day: $day")

        val query = databaseTimetable.orderByChild("subject").equalTo(subject)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val timetableDay = snapshot.child("day").value?.toString()
                    if (timetableDay.equals(day, ignoreCase = true)) {
                        showToast("Removing entry: $snapshot")
                        snapshot.ref.removeValue() // Removes the entire entry with the specified ID
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Failed to delete timetable entry: ${databaseError.message}")
            }
        })
    }

    private fun addAlarmToUI(subject: String, day: String, time: String) {
        val cardView = CardView(this).apply {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16) // Margin between each entry
            }
            this.layoutParams = layoutParams
            radius = 16f // Corner border radius
            setCardBackgroundColor(android.graphics.Color.parseColor("#E0FFFF")) // Light grey background
            setContentPadding(32, 32, 32, 32) // Padding inside the card
        }

        val alarmTextView = TextView(this).apply {
            text = "$subject\n$day\n$time"
            textSize = 18f
            setTextColor(android.graphics.Color.parseColor("#00008B")) // Dark white text color
        }

        cardView.addView(alarmTextView)
        alarmContainer.addView(cardView)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddAlarmDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_alarm, null)
        builder.setView(dialogLayout)

        val inputSubject: EditText = dialogLayout.findViewById(R.id.input_subject)
        val inputDay: EditText = dialogLayout.findViewById(R.id.input_day)
        val inputTime: EditText = dialogLayout.findViewById(R.id.input_time)
        val submitButton: Button = dialogLayout.findViewById(R.id.submit_button)

        val alertDialog = builder.create()

        submitButton.setOnClickListener {
            val subject = inputSubject.text.toString().trim()
            val day = inputDay.text.toString().trim()
            val time = inputTime.text.toString().trim()

            if (subject.isEmpty() || day.isEmpty() || !isValidTime(time)) {
                showToast("Please enter valid data")
            } else {
                saveAlarmToDatabase(subject, day, time)
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    private fun isValidTime(time: String): Boolean {
        val regex = Regex("^\\d{2}:\\d{2} [APap][Mm]$")
        return time.matches(regex)
    }

    private fun saveAlarmToDatabase(subject: String, day: String, time: String) {
        val newAlarm = mapOf(
            "subject" to subject,
            "day" to day,
            "time" to time
        )
        databaseTimetable.push().setValue(newAlarm)
            .addOnSuccessListener {
                showToast("Alarm added successfully")
                addAlarmToUI(subject, day, time)
            }
            .addOnFailureListener {
                showToast("Failed to add alarm: ${it.message}")
            }
    }
}
