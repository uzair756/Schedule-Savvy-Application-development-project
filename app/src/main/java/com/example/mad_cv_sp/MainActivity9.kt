package com.example.mad_cv_sp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity9 : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var messagesContainer: LinearLayout
    private lateinit var userName: String // Variable to store user's name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main9)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("messages")
        messagesContainer = findViewById(R.id.messages_container)

        // Get user's name from intent
        userName = intent.getStringExtra("userName") ?: "Unknown"

        // Fetch and display messages
        fetchMessages()
    }

    private fun fetchMessages() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesContainer.removeAllViews() // Clear previous messages
                for (messageSnapshot in dataSnapshot.children) {
                    val name = messageSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val index = messageSnapshot.child("index").getValue(String::class.java) ?: "Unknown"
                    val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java) ?: 0

                    // Create a LinearLayout to hold name, index, and timestamp
                    val messageLayout = LinearLayout(this@MainActivity9)
                    messageLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    messageLayout.orientation = LinearLayout.VERTICAL
                    messageLayout.setBackgroundResource(R.drawable.rounded_bg_light_green)
                    messageLayout.setPadding(16, 16, 16, 16)
                    val layoutParams = messageLayout.layoutParams as LinearLayout.LayoutParams
                    layoutParams.bottomMargin = 34 // Adjust margin between messages
                    messageLayout.layoutParams = layoutParams

                    // Add name TextView to messageLayout
                    val nameTextView = TextView(this@MainActivity9)
                    nameTextView.text = name
                    nameTextView.setTextColor(Color.BLACK)
                    nameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
                    messageLayout.addView(nameTextView)

                    // Add index TextView to messageLayout
                    val indexTextView = TextView(this@MainActivity9)
                    indexTextView.text = index
                    indexTextView.setTextColor(Color.BLUE)
                    messageLayout.addView(indexTextView)

                    // Add timestamp TextView to messageLayout
                    val timeTextView = TextView(this@MainActivity9)
                    timeTextView.text = timestampToString(timestamp)
                    timeTextView.setTextColor(Color.BLACK)
                    timeTextView.gravity = Gravity.END
                    messageLayout.addView(timeTextView)

                    // Add messageLayout to messagesContainer
                    messagesContainer.addView(messageLayout)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun timestampToString(timestamp: Long): String {
        // Convert timestamp to a readable format
        val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

